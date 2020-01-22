package Engine_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_Tests_WindowsVSPackage : BuildType({
    templates(Engine_Tests_WindowsTestsTemplate)
    name = "Windows - VSPackage"
    paused = true

    params {
        param("TestCategories", "VSPackage")
    }
})
