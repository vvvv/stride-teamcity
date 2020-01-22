package Engine_Deploy

import Engine_Deploy.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Engine_Deploy")
    name = "Deploy"

    buildType(Engine_Deploy_DeployVSPackage)
    buildType(Engine_Deploy_DeployXenko)
    buildType(Engine_Deploy_DeployLauncher)

    params {
        text("XenkoStoreUrl", "https://api.nuget.org/v3/index.json", label = "NuGet Server Push URL", display = ParameterDisplay.PROMPT, allowEmpty = true)
        password("XenkoStoreApiKey", "credentialsJSON:ca50f1d8-904d-4bf0-88f5-cb095f92aacd", label = "NuGet Server API Key", display = ParameterDisplay.PROMPT)
    }
})
