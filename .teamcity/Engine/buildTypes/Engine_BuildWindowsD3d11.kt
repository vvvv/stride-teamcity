package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsD3d11 : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows D3D11"

    params {
        param("RuntimeBuildTarget", "Windows")
    }

    steps {
        step {
            id = "RUNNER_47"
            type = "Engine_BuildWarningReportGenerator"
            param("BuildCheckoutDirectoryPath", ".")
            param("BuildLogPath", "%BuildLogFile%")
        }
        stepsOrder = arrayListOf("RUNNER_7", "RUNNER_47")
    }
})
