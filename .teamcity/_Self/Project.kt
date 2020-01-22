package _Self

import _Self.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    description = "Contains all other projects"

    vcsRoot(Xenko)

    features {
        feature {
            id = "PROJECT_EXT_1"
            type = "ReportTab"
            param("startPage", "coverage.zip!index.html")
            param("title", "Code Coverage")
            param("type", "BuildReportTab")
        }
        feature {
            id = "PROJECT_EXT_5"
            type = "CloudImage"
            param("subnetId", "default")
            param("imageId", "/subscriptions/61816513-881b-426e-877a-0f9108b16ecc/resourceGroups/TEAMCITYAGENTS/providers/Microsoft.Compute/images/xenko-build1-image-20180616171008")
            param("groupId", "TeamcityAgents")
            param("agent_pool_id", "0")
            param("vmUsername", "xenko")
            param("reuseVm", "true")
            param("source-id", "xenko-build")
            param("deployTarget", "SpecificGroup")
            param("vmPublicIp", "false")
            param("imageUrl", "")
            param("osType", "Windows")
            param("networkId", "/subscriptions/61816513-881b-426e-877a-0f9108b16ecc/resourceGroups/TeamcityAgents/providers/Microsoft.Network/virtualNetworks/TeamcityAgents-vnet")
            param("storageAccountType", "Premium_LRS")
            param("vmSize", "Standard_F4s_v2")
            param("maxInstances", "2")
            param("region", "westus2")
            param("imageType", "Image")
        }
        feature {
            id = "PROJECT_EXT_6"
            type = "CloudImage"
            param("subnetId", "default")
            param("imageId", "/subscriptions/61816513-881b-426e-877a-0f9108b16ecc/resourceGroups/TEAMCITYAGENTS/providers/Microsoft.Compute/images/xenko-build-image-20191004211722")
            param("groupId", "TeamcityAgents")
            param("agent_pool_id", "0")
            param("vmUsername", "xenko")
            param("reuseVm", "true")
            param("source-id", "xenko-tc")
            param("deployTarget", "SpecificGroup")
            param("vmPublicIp", "false")
            param("profileId", "arm-2")
            param("imageUrl", "")
            param("osType", "Windows")
            param("networkId", "/subscriptions/61816513-881b-426e-877a-0f9108b16ecc/resourceGroups/TeamcityAgents/providers/Microsoft.Network/virtualNetworks/TeamcityAgents-vnet")
            param("storageAccountType", "Standard_LRS")
            param("vmSize", "Standard_F4s_v2")
            param("maxInstances", "1")
            param("region", "westus2")
            param("imageType", "Image")
        }
        feature {
            id = "arm-2"
            type = "CloudProfile"
            param("clientId", "4d5ec341-3169-4f9c-a85d-a665b54f0502")
            param("secure:clientSecret", "credentialsJSON:9b935358-fe3e-4b94-838a-b530ca0577bb")
            param("profileServerUrl", "")
            param("system.cloud.profile_id", "arm-2")
            param("total-work-time", "")
            param("credentialsType", "service")
            param("description", "")
            param("cloud-code", "arm")
            param("enabled", "true")
            param("environment", "AZURE")
            param("agentPushPreset", "")
            param("profileId", "arm-2")
            param("name", "Xenko Teamcity")
            param("tenantId", "04bbf5b8-ff80-4a4f-a5f1-4d9cdce844c1")
            param("next-hour", "")
            param("subscriptionId", "61816513-881b-426e-877a-0f9108b16ecc")
            param("terminate-idle-time", "10")
            param("secure:passwords_data", "credentialsJSON:6532f5c6-876f-4654-a77e-24a084782774")
        }
    }

    cleanup {
        preventDependencyCleanup = false
    }
    subProjectsOrder = arrayListOf(RelativeId("Engine"), RelativeId("Documentation"), RelativeId("Website"))

    subProject(Documentation.Project)
    subProject(Website.Project)
    subProject(Engine.Project)
})
