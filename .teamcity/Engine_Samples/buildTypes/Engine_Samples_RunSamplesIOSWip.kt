package Engine_Samples.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_Samples_RunSamplesIOSWip : BuildType({
    name = "Run Samples iOS (WIP)"

    artifactRules = """screenshots\*.png => screenshots-iOS-%build.number%.zip"""

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        powerShell {
            name = "imobiledevice setup"
            scriptMode = script {
                content = """Copy-Item deps-restricted\imobiledevice-1.2.0-r3\* Bin\Windows-Direct3D11\"""
            }
        }
        powerShell {
            name = "Install ipas"
            scriptMode = script {
                content = """
                    cd ipas
                    ${'$'}ipa_files = Get-ChildItem -recurse | Where-Object { ${'$'}_.Name -match ".ipa" }
                    foreach(${'$'}file in ${'$'}ipa_files) { %env.SiliconStudioXenkoDir%\Bin\Windows-Direct3D11\ideviceinstaller.exe -g ${'$'}file.FullName; echo ${'$'}file.FullName; sleep 5; }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Prepare environment"
            scriptMode = script {
                content = """
                    mkdir build
                    echo "null" > build\Xenko.sln
                    mkdir screenshots
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                    start Bin\Windows-Direct3D11\SiliconStudio.Xenko.ConnectionRouter.exe
                    sleep 2
                """.trimIndent()
            }
        }
        nunit {
            nunitPath = """sources\common\deps\NUnit\nunit3-console.exe"""
            includeTests = "TODO"
            param("nunit_command_line", "--workers=1")
        }
        powerShell {
            scriptMode = script {
                content = "taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f"
            }
        }
        powerShell {
            name = "Download artifacts"
            scriptMode = script {
                content = """
                    ${'$'}params = @{build_id='%teamcity.build.id%';build_number='%build.number%';repo='PDX_Samples_RunSamplesIOS';platform='iOS';branch='%teamcity.build.branch%'}
                    Invoke-WebRequest -Uri http://XXX/teamcity_upload.php -Method POST -Body ${'$'}params
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
                artifactRules = """build\Xenko.*.nupkg!** => ."""
            }
        }
    }
})
