package Engine_Samples.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_Samples_RunSamplesAndroid : BuildType({
    name = "Run Samples Android"

    artifactRules = """screenshots\*.png => screenshots-Android-%build.number%.zip"""

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        powerShell {
            name = "Unlock screen"
            scriptMode = script {
                content = """
                    adb kill-server
                    sleep 2
                    adb start-server
                    sleep 2
                    ${'$'}state = adb shell "dumpsys power | grep state=OFF"
                    if(${'$'}state -match "Display Power: state=OFF") { adb shell input keyevent 82 }
                    sleep 2
                """.trimIndent()
            }
        }
        powerShell {
            name = "Install APKs"
            scriptMode = script {
                content = """
                    cd apks
                    ${'$'}apk_files = Get-ChildItem -recurse | Where-Object { ${'$'}_.Name -match ".apk" }
                    foreach(${'$'}file in ${'$'}apk_files) { adb uninstall ${'$'}file.Name.substring(0, ${'$'}file.Name.indexof("-Signed.apk")) }
                    foreach(${'$'}file in ${'$'}apk_files) { adb install -r ${'$'}file.Fullname }
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
                    adb kill-server
                    sleep 2
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                    start Bin\Windows\SiliconStudio.Xenko.ConnectionRouter.exe
                    sleep 2
                    adb logcat -c
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
            name = "Lock screen"
            scriptMode = script {
                content = """
                    ${'$'}state = adb shell "dumpsys power | grep state=ON"
                    if(${'$'}state -match "Display Power: state=ON") { adb shell input keyevent 26 }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Kill router and adb"
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
            name = "Download artifacts"
            scriptMode = script {
                content = """
                    ${'$'}params = @{build_id='%teamcity.build.id%';build_number='%build.number%';repo='PDX_Samples_RunSamplesAndroid';platform='Android';branch='%teamcity.build.branch%'}
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
                artifactRules = """
                    build\Xenko.*.nupkg!** => .
                    Windows-Direct3D11-Extra.zip!** => .
                """.trimIndent()
            }
        }
        dependency(Engine_Samples_SamplesAndroid) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = """
                    apks.zip!** => apks
                    samples.zip!** => samples
                """.trimIndent()
            }
        }
    }
})
