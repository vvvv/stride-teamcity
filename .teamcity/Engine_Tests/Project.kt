package Engine_Tests

import Engine_Tests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Engine_Tests")
    name = "Tests"

    buildType(Engine_Tests_WindowsVSPackage)
    buildType(Engine_Tests_WindowsD3D11)
    buildType(Engine_Tests_WindowsSamplesD3D)
    buildType(Engine_Tests_WindowsSimple)

    template(Engine_Tests_AndroidTestsTemplate)
    template(Engine_Tests_WindowsTestsTemplate)
})
