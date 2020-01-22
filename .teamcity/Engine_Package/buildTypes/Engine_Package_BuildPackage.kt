package Engine_Package.buildTypes

import _Self.vcsRoots.Xenko
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Engine_Package_BuildPackage : BuildType({
    name = "Build Package"

    allowExternalStatus = true
    artifactRules = """
        bin/packages/**/*.nupkg => bin/packages
        bin/vsix/**/*.nupkg => bin/vsix
        build/Xenko.build => build
        build/Xenko.version => build
        build/.nuget/NuGet.exe => build/.nuget
    """.trimIndent()

    params {
        checkbox("env.XenkoOfficialBuild", "",
                  checked = "true")
        checkbox("XenkoGraphicsApiDependentBuildAll", "true", label = "Build all graphics platforms",
                  checked = "true", unchecked = "false")
        select("XenkoPlatforms", "Windows;Android;UWP;iOS;Linux;macOS", label = "Platforms",
                allowMultiple = true, valueSeparator = ";",
                options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
    }

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        msBuild {
            path = """build\Xenko.build"""
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "Package"
            args = """/nr:false /p:XenkoPlatforms="%XenkoPlatforms%" /p:XenkoGraphicsApiDependentBuildAll=%XenkoGraphicsApiDependentBuildAll%"""
        }
        powerShell {
            name = "Update build number with actual package version"
            enabled = false
            workingDir = "build"
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-Content .\Xenko.version) | Write-Host"""
            }
        }
    }

    triggers {
        vcs {
            enabled = false
            branchFilter = ""
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

    cleanup {
        artifacts(builds = 10, days = 3)
    }
})
