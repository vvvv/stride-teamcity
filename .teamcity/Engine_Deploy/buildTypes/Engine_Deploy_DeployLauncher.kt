package Engine_Deploy.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.vcsLabeling
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Engine_Deploy_DeployLauncher : BuildType({
    name = "Deploy Launcher"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        powerShell {
            name = "Update build number with actual version"
            workingDir = "sources/launcher/build"
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-ChildItem -Path .\Xenko.Launcher.*.nupkg -Name) -replace 'Xenko\.Launcher\.(.*).nupkg','${'$'}1' | Write-Host"""
            }
        }
        msBuild {
            path = """build\Xenko.build"""
            version = MSBuildStep.MSBuildVersion.V15_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            targets = "PublishLauncher"
            args = "/p:XenkoStoreUrl=%XenkoStoreUrl% /p:XenkoStoreApiKey=%XenkoStoreApiKey%"
        }
    }

    features {
        vcsLabeling {
            vcsRootId = "Engine_Package_XenkoLauncher"
            labelingPattern = "launcher/%build.number%"
            successfulOnly = true
            branchFilter = "+:*"
        }
    }

    dependencies {
        dependency(Engine_Package.buildTypes.Engine_Package_BuildLauncher) {
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
