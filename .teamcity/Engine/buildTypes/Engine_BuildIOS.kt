package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildIOS : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build iOS"

    params {
        param("RuntimeBuildTarget", "iOS")
    }
})
