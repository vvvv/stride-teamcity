package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsOpenGL : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows OpenGL"

    params {
        param("RuntimeBuildTarget", "WindowsOpenGL")
    }
})
