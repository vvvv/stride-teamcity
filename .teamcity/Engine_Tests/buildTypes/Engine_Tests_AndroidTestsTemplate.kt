package Engine_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_Tests_AndroidTestsTemplate : Template({
    name = "Android Tests Template"

    params {
        param("env.XENKO_BUILD_NUMBER", "%build.number%")
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        msBuild {
            name = "Build Windows"
            id = "RUNNER_38"
            path = """build\Xenko.build"""
            version = MSBuildStep.MSBuildVersion.V15_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            targets = "BuildWindows"
            args = "/m /nr:false /p:XenkoSkipUnitTests=true"
        }
        msBuild {
            id = "RUNNER_39"
            path = """build\Xenko.build"""
            version = MSBuildStep.MSBuildVersion.V15_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            targets = "BuildAndroid"
            args = "/m /nr:false /p:XenkoSkipUnitTests=true"
        }
        powerShell {
            name = "Unlock screen"
            id = "RUNNER_40"
            scriptMode = script {
                content = """
                    Remove-Item .\Bin\Windows\connectionrouter.lock
                    adb kill-server
                    sleep 2
                    adb start-server
                    sleep 2
                    ${'$'}state = adb shell "dumpsys power | grep state=OFF"
                    if(${'$'}state -match "Display Power: state=OFF") { adb shell input keyevent 82 }
                    sleep 2
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                    Copy-Item .\Bin\Windows\Direct3D11\* .\Bin\Windows\
                    Copy-Item .\Bin\Android\OpenGLES\* .\Bin\Android\
                    start .\Bin\Windows\SiliconStudio.Xenko.ConnectionRouter.exe
                    sleep 2
                """.trimIndent()
            }
        }
        msBuild {
            id = "RUNNER_41"
            path = """build\Xenko.build"""
            version = MSBuildStep.MSBuildVersion.V15_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            targets = "RunTestsMobile"
            args = "/p:PlatformToBuild=Android"
        }
        powerShell {
            name = "Lock screen"
            id = "RUNNER_42"
            scriptMode = script {
                content = """
                    ${'$'}state = adb shell "dumpsys power | grep state=ON"
                    if(${'$'}state -match "Display Power: state=ON") { adb shell input keyevent 26 }
                    sleep 2
                    taskkill /F /FI "Imagename eq SiliconStudio.*"
                    taskkill /F /FI "Imagename eq Xenko.*"
                    taskkill /F /IM adb.exe
                    sleep 2
                    adb kill-server
                    sleep 2
                """.trimIndent()
            }
        }
    }

    features {
        feature {
            id = "BUILD_EXT_8"
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "nunit")
            param("xmlReportParsing.reportDirs", "Bin/Windows/TestResults/**/*.xml")
            param("xmlReportParsing.verboseOutput", "true")
        }
    }
})
