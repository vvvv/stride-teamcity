package Engine_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_Tests_WindowsD3D11 : BuildType({
    templates(Engine_Tests_WindowsTestsTemplate)
    name = "Windows - D3D11"

    params {
        param("TestCategories", "Game")
    }
    
    disableSettings("vcsTrigger")
})
