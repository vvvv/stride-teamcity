package Engine_Package.buildTypes

import _Self.vcsRoots.Xenko
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Engine_Package_BuildLauncher : BuildType({
    name = "Build Launcher"

    artifactRules = """
        build/.nuget/NuGet.exe => build/.nuget
        bin/launcher/*.nupkg => bin/launcher
        bin/launcher/XenkoSetup*.exe => bin/launcher
        build/Xenko.build => build
    """.trimIndent()

    params {
        text("CustomInstallerStoreUrl", "", allowEmpty = true)
        text("CustomInstaller", "", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        msBuild {
            path = "build/Xenko.build"
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "FullBuildLauncher"
            args = """/nr:false /p:CustomInstaller="%CustomInstaller%" /p:CustomInstallerStoreUrl="%CustomInstallerStoreUrl%""""
        }
        powerShell {
            name = "Update build number with actual package version"
            workingDir = "sources/launcher/build"
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-ChildItem -Path .\Xenko.Launcher.*.nupkg -Name) -replace 'Xenko\.Launcher\.(.*).nupkg','${'$'}1' | Write-Host"""
            }
        }
    }

    triggers {
        vcs {
            triggerRules = "+:/sources/launcher/**"
            branchFilter = "+:master"
        }
    }

    features {
        commitStatusPublisher {
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
