package Engine.buildTypes

import _Self.vcsRoots.Xenko
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Engine_BuildRuntimeFromSourceTemplate : Template({
    name = "Build Runtime from Source Template"

    allowExternalStatus = true
    artifactRules = "%BuildLogFile%"

    params {
        param("BuildLogFile", "BuildLog.Log")
        param("MSBuildLogClause", "/l:FileLogger,Microsoft.Build.Engine;logfile=%BuildLogFile%;Append")
        param("env.XenkoDisableAssetCompilerExecServerProxy", "true")
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        msBuild {
            name = "Build Runtime"
            id = "RUNNER_7"
            path = """build\Xenko.build"""
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "Build%RuntimeBuildTarget%"
            args = "/m /nr:false /p:XenkoSkipUnitTests=true /p:XenkoEnableCodeAnalysis=true /p:VisualStudioVersion=16.0 %MSBuildLogClause%"
        }
        step {
            id = "RUNNER_47"
            type = "Engine_BuildWarningReportGenerator"
            param("BuildCheckoutDirectoryPath", ".")
            param("BuildLogPath", "%BuildLogFile%")
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            branchFilter = "+:master"
        }
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_1"
            vcsRootExtId = "${Xenko.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:df649119-0086-4cc6-bde6-e1fa7127681b"
                }
            }
        }
    }
})
