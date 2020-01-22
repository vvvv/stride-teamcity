package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsUWP : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows UWP"

    params {
        param("RuntimeBuildTarget", "UWP")
    }
})
