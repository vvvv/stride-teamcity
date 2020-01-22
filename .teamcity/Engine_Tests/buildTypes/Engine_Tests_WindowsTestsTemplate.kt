package Engine_Tests.buildTypes

import _Self.vcsRoots.Xenko
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Engine_Tests_WindowsTestsTemplate : Template({
    name = "Windows Tests Template"

    allowExternalStatus = true
    artifactRules = """
        build\TestResults => TestResults
        tests\local => local
    """.trimIndent()

    params {
        param("env.XENKO_BUILD_NUMBER", "%build.number%")
        param("env.XenkoDisableAssetCompilerExecServerProxy", "true")
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        powerShell {
            name = "teh Killer"
            id = "RUNNER_43"
            scriptMode = script {
                content = """
                    adb kill-server
                    sleep 2
                    taskkill /IM SiliconStudio.Xenko.ConnectionRouter.exe /f
                    sleep 2
                """.trimIndent()
            }
        }
        msBuild {
            id = "RUNNER_8"
            path = """build\Xenko.build"""
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "RunTestsWindows"
            args = "/nr:false /p:XenkoTestCategories=%TestCategories%;VisualStudioVersion=15.0"
            reduceTestFeedback = true
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            branchFilter = "+:master"
        }
    }

    failureConditions {
        executionTimeoutMin = 30
        failOnMetricChange {
            id = "BUILD_EXT_3"
            metric = BuildFailureOnMetric.MetricType.TEST_COUNT
            threshold = 20
            units = BuildFailureOnMetric.MetricUnit.PERCENTS
            comparison = BuildFailureOnMetric.MetricComparison.LESS
            compareTo = build {
                buildRule = lastSuccessful()
            }
        }
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_2"
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
