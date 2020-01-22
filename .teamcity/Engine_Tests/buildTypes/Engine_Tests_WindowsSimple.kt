package Engine_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_Tests_WindowsSimple : BuildType({
    templates(Engine_Tests_WindowsTestsTemplate)
    name = "Windows - Simple"

    params {
        param("TestCategories", "Simple")
    }
})
