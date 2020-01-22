package Engine_Samples

import Engine_Samples.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Engine_Samples")
    name = "Samples"

    buildType(Engine_Samples_RunSamplesNvidia)
    buildType(Engine_Samples_SamplesAndroid)
    buildType(Engine_Samples_RunSamplesIOSWip)
    buildType(Engine_Samples_SamplesD3d)
    buildType(Engine_Samples_RunSamplesAndroid)

    params {
        param("env.XenkoDir", "%system.teamcity.build.workingDir%")
        param("env.XenkoDisableAssetCompilerExecServerProxy", "true")
    }
})
