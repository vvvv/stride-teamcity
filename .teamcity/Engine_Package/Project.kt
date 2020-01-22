package Engine_Package

import Engine_Package.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Engine_Package")
    name = "Package"

    buildType(Engine_Package_BuildPackage)
    buildType(Engine_Package_BuildLauncher)
})
