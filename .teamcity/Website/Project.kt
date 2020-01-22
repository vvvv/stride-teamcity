package Website

import Website.buildTypes.*
import Website.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Website")
    name = "Website"

    vcsRoot(Website_XenkoWebsiteStaging)
    vcsRoot(Website_XenkoWebsiteRelease)

    buildType(Website_BuildAndDeployStaging)
    buildType(Website_BuildAndDeployRelease)
    buildType(PurgeCloudflareCache)

    features {
        feature {
            id = "PROJECT_EXT_2"
            type = "OAuthProvider"
            param("clientId", "be25629bf466bc9d841a")
            param("defaultTokenScope", "public_repo,repo,repo:status,write:repo_hook")
            param("secure:clientSecret", "credentialsJSON:f80e1007-1637-422a-9a63-19937cb44763")
            param("displayName", "GitHub.com")
            param("gitHubUrl", "https://github.com/")
            param("providerType", "GitHub")
        }
    }
})
