package Engine_Samples.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.msBuild

object Engine_Samples_SamplesAndroid : BuildType({
    name = "Samples Android"

    artifactRules = """
        samplesGenerated\**\*Signed.apk => apks.zip
        samples\**\Bin\** => samples.zip
    """.trimIndent()

    vcs {
        root(_Self.vcsRoots.Xenko)
    }

    steps {
        msBuild {
            name = "Build Tests Android"
            path = """build\XenkoSamples.build"""
            version = MSBuildStep.MSBuildVersion.V15_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V15_0
            targets = "NugetExtractFix;BuildTests;GenerateSamples;BuildSamples"
            args = "/nr:false /p:PlatformToBuild=Android"
        }
    }

    dependencies {
        dependency(Engine_Package.buildTypes.Engine_Package_BuildPackage) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = """build\Xenko.*.nupkg!** => ."""
            }
        }
    }
})
