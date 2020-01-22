package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsD3d12 : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows D3D12"

    params {
        param("RuntimeBuildTarget", "WindowsDirect3D12")
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
