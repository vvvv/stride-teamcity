package Engine.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_BuildLinuxVulkan : BuildType({
    templates(Engine_BuildRuntimeFromSourceTemplate)
    name = "Build Linux Vulkan"

    params {
        param("RuntimeBuildTarget", "LinuxVulkan")
    }
})
