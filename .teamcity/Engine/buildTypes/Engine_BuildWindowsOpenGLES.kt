package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsOpenGLES : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows OpenGL ES"

    params {
        param("RuntimeBuildTarget", "WindowsOpenGLES")
    }
})
