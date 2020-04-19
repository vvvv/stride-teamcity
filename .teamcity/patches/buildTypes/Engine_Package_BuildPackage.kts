package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Engine_Package_BuildPackage'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Engine_Package_BuildPackage")) {
    check(artifactRules == """
        bin/packages/**/*.nupkg => bin/packages
        bin/vsix/**/*.nupkg => bin/vsix
        build/Xenko.build => build
        build/Xenko.version => build
        build/.nuget/NuGet.exe => build/.nuget
    """.trimIndent()) {
        "Unexpected option value: artifactRules = $artifactRules"
    }
    artifactRules = """
        bin/packages/**/*.nupkg => bin/packages
        bin/vsix/**/*.nupkg => bin/vsix
        build/Stride.build => build
        build/Stride.version => build
        build/.nuget/NuGet.exe => build/.nuget
    """.trimIndent()

    params {
        remove {
            checkbox("XenkoGraphicsApiDependentBuildAll", "true", label = "Build all graphics platforms",
                      checked = "true", unchecked = "false")
        }
        remove {
            select("XenkoPlatforms", "Windows;Android;UWP;iOS;Linux;macOS", label = "Platforms",
                    allowMultiple = true, valueSeparator = ";",
                    options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
        }
        remove {
            checkbox("env.XenkoOfficialBuild", "",
                      checked = "true")
        }
        add {
            param("StrideOfficialBuild", "false")
        }
        add {
            checkbox("env.StrideOfficialBuild", "",
                      checked = "true")
        }
        add {
            select("StridePlatforms", "Windows", label = "Platforms",
                    allowMultiple = true, valueSeparator = ";",
                    options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
        }
        add {
            param("StrideBuildPrerequisitesInstaller", "false")
        }
        add {
            checkbox("StrideGraphicsApiDependentBuildAll", "false", label = "Build all graphics platforms",
                      checked = "true", unchecked = "false")
        }
        add {
            param("StrideSign", "false")
        }
    }

    expectSteps {
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
    steps {
        update<MSBuildStep>(0) {
            path = """build\Stride.build"""
            args = """/nr:false /p:StridePlatforms="%StridePlatforms%" /p:StrideGraphicsApiDependentBuildAll=%StrideGraphicsApiDependentBuildAll% /p:StrideBuildPrerequisitesInstaller=%StrideBuildPrerequisitesInstaller% /p:StrideSign=%StrideSign% /p:StrideOfficialBuild=%StrideOfficialBuild%"""
        }
        update<PowerShellStep>(1) {
            scriptMode = script {
                content = """"##teamcity[buildNumber '{0}']" -f (Get-Content .\Stride.version) | Write-Host"""
            }
        }
    }

    expectDisabledSettings()
    updateDisabledSettings("RUNNER_2")
}
