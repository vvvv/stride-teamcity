package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Engine_Package_BuildPackage'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Engine_Package_BuildPackage")) {
    params {
        expect {
            checkbox("XenkoGraphicsApiDependentBuildAll", "true", label = "Build all graphics platforms",
                      checked = "true", unchecked = "false")
        }
        update {
            checkbox("XenkoGraphicsApiDependentBuildAll", "false", label = "Build all graphics platforms",
                      checked = "true", unchecked = "false")
        }
        expect {
            select("XenkoPlatforms", "Windows;Android;UWP;iOS;Linux;macOS", label = "Platforms",
                    allowMultiple = true, valueSeparator = ";",
                    options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
        }
        update {
            select("XenkoPlatforms", "Windows", label = "Platforms",
                    allowMultiple = true, valueSeparator = ";",
                    options = listOf("Windows", "UWP", "iOS", "Android", "Linux", "macOS"))
        }
        add {
            param("XenkoBuildPrerequisitesInstaller", "false")
        }
        add {
            param("XenkoOfficialBuild", "false")
        }
        add {
            param("XenkoSign", "false")
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
            args = """/nr:false /p:XenkoPlatforms="%XenkoPlatforms%" /p:XenkoGraphicsApiDependentBuildAll=%XenkoGraphicsApiDependentBuildAll% /p:XenkoBuildPrerequisitesInstaller=%XenkoBuildPrerequisitesInstaller% /p:XenkoSign=%XenkoSign% /p:XenkoOfficialBuild=%XenkoOfficialBuild%"""
        }
    }
}
