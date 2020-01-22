package Engine_Samples.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Engine_Samples_RunSamplesNvidia : BuildType({
    name = "Run Samples NVIDIA"

    artifactRules = """screenshots\*.png => screenshots-Nvidia-%build.number%.zip"""

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        script {
            name = "Setup folders and extract package"
            scriptContent = """
                mkdir build
                echo "null" > build\Xenko.sln
                mkdir screenshots
                xcopy /Y C:\VSLibs\*.dll samples\Others\NativeLinking\Bin\Windows\Debug\
                cd tools
                packageinstall.exe /extract
            """.trimIndent()
        }
        powerShell {
            name = "Kill ADB and Start Connection Router"
            scriptMode = script {
                content = """
                    adb kill-server
                    sleep 2
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                    start Bin\Windows\SiliconStudio.Xenko.ConnectionRouter.exe
                    sleep 2
                """.trimIndent()
            }
        }
        nunit {
            nunitPath = """sources\common\deps\NUnit\nunit3-console.exe"""
            includeTests = """samples\Tests\**\bin\Debug\*Test.dll"""
            param("nunit_command_line", "--workers=1")
        }
        powerShell {
            name = "Kill all"
            scriptMode = script {
                content = """
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                    taskkill /IM adb.exe /f
                    sleep 2
                    adb kill-server
                    sleep 2
                """.trimIndent()
            }
        }
        powerShell {
            name = "Trigger Download queue"
            scriptMode = script {
                content = """
                    ${'$'}params = @{build_id='%teamcity.build.id%';build_number='%build.number%';repo='PDX_Samples_RunSamplesNVidia';platform='Nvidia';branch='%teamcity.build.branch%'}
                    Invoke-WebRequest -Uri http://XXXX/teamcity_upload.php -Method POST -Body ${'$'}params
                """.trimIndent()
            }
        }
    }

    dependencies {
        dependency(Engine_Package.buildTypes.Engine_Package_BuildPackage) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = """
                    build\Xenko.*.nupkg!** => .
                    Windows-Direct3D11-Extra.zip!** => .
                """.trimIndent()
            }
        }
        dependency(Engine_Samples_SamplesD3d) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = """
                    samples.zip!** => samples
                    samplesGenerated.zip!** => samplesGenerated
                """.trimIndent()
            }
        }
    }
})
