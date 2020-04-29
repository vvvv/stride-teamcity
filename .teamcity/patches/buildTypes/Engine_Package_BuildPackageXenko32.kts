package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Engine_Package_BuildPackageXenko32'
in the project with id = 'Engine_Package', and delete the patch script.
*/
create(RelativeId("Engine_Package"), BuildType({
    id("Engine_Package_BuildPackageXenko32")
    name = "Build Package Xenko 3.2"

    allowExternalStatus = true
    artifactRules = """
        bin/packages/**/*.nupkg => bin/packages
        bin/vsix/**/*.nupkg => bin/vsix
        build/Stride.build => build
        build/Stride.version => build
        build/.nuget/NuGet.exe => build/.nuget
    """.trimIndent()

    params {
        param("XenkoBuildPrerequisitesInstaller", "false")
        checkbox("env.StrideOfficialBuild", "",
                  checked = "true")
        checkbox("XenkoGraphicsApiDependentBuildAll", "false", label = "Build all graphics platforms",
                  checked = "true", unchecked = "false")
        param("XenkoOfficialBuild", "false")
        param("XenkoSign", "false")
        select("XenkoPlatforms", "Windows", label = "Platforms",
                allowMultiple = true, valueSeparator = ";",
                options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
    }

    vcs {
        root(RelativeId("Xenko"))
    }

    steps {
        msBuild {
            path = """build\Xenko.build"""
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V16_0
            targets = "Package"
            args = """/nr:false /p:XenkoPlatforms="%XenkoPlatforms%" /p:XenkoGraphicsApiDependentBuildAll=%XenkoGraphicsApiDependentBuildAll% /p:XenkoBuildPrerequisitesInstaller=%XenkoBuildPrerequisitesInstaller% /p:XenkoSign=%XenkoSign% /p:XenkoOfficialBuild=%XenkoOfficialBuild%"""
        }
        powerShell {
            name = "Update build number with actual package version"
            enabled = false
            workingDir = "build"
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-Content .\Stride.version) | Write-Host"""
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
            vcsRootExtId = "Xenko_Xenko"
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
}))

