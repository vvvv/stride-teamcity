package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildAndroid : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Android"

    params {
        param("RuntimeBuildTarget", "Android")
    }
})
