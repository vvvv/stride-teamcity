package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildLinuxOpenGL : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Linux OpenGL"

    params {
        param("RuntimeBuildTarget", "Linux")
    }
})
