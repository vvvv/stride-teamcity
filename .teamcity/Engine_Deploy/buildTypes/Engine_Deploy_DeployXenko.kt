package Engine_Deploy.buildTypes

import _Self.vcsRoots.Xenko
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.vcsLabeling
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_Deploy_DeployXenko : BuildType({
    name = "Deploy Xenko"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        checkbox("reverse.dep.*.env.XenkoOfficialBuild", "", label = "Official Build", description = "If unchecked, beta00123-g<githash> will be appended to nupkg", display = ParameterDisplay.PROMPT,
                  checked = "true")
        checkbox("XenkoPublishSkipSamplesTemplates", "true",
                  checked = "true")
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        powerShell {
            name = "Update build number with actual package version"
            workingDir = "build"
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-Content .\Xenko.version) | Write-Host"""
            }
        }
        msBuild {
            workingDir = "build"
            path = "build/Xenko.build"
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "Publish"
            args = "/p:XenkoStoreUrl=%XenkoStoreUrl% /p:XenkoStoreApiKey=%XenkoStoreApiKey% /p:XenkoPublishSkipSamplesTemplates=%XenkoPublishSkipSamplesTemplates%"
        }
        powerShell {
            name = "Update success message with store and version it was deployed to"
            workingDir = "build"
            scriptMode = script {
                content = """"##teamcity[buildStatus text='{build.status.text} - deployed %build.number% to %XenkoStoreUrl%']" | Write-Host"""
            }
        }
    }

    features {
        vcsLabeling {
            enabled = false
            vcsRootId = "${Xenko.id}"
            labelingPattern = "releases/%build.number%"
            successfulOnly = true
            branchFilter = "+:*"
        }
    }

    dependencies {
        dependency(Engine_Package.buildTypes.Engine_Package_BuildPackage) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = "**/*.* => ."
            }
        }
    }
})
