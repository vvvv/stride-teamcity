package Documentation

import Documentation.buildTypes.*
import Documentation.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Documentation")
    name = "Documentation"

    vcsRoot(Documentation_XenkoDocs)

    buildType(Documentation_Deploy)
    buildType(Documentation_Build)
})
