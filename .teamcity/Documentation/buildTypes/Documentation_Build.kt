package Documentation.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Documentation_Build : BuildType({
    name = "Build"

    artifactRules = """
        xenko-docs/_site/ => xenko_doc.zip
        xenko-docs/web.config => xenko_doc.zip
    """.trimIndent()

    vcs {
        root(_Self.vcsRoots.Xenko, "+:. => xenko")
        root(Documentation.vcsRoots.Documentation_XenkoDocs, "+:. => xenko-docs")
    }

    steps {
        exec {
            name = "Build Website"
            workingDir = "xenko-docs"
            path = "build.bat"
        }
    }

    triggers {
        vcs {
            triggerRules = "+:xenko-docs/**"
        }
    }

    cleanup {
        artifacts(builds = 3)
    }
})
