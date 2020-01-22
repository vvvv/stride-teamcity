package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildWindowsVulkan : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Windows Vulkan"

    params {
        param("RuntimeBuildTarget", "WindowsVulkan")
    }
})
