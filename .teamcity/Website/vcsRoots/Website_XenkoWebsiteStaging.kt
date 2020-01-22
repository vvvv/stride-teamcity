package Website.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object Website_XenkoWebsiteStaging : GitVcsRoot({
    name = "xenko-website - staging"
    url = "git@github.com:xenko3d/xenko-website.git"
    branch = "staging"
    authMethod = uploadedKey {
        uploadedKey = "GitHub"
    }
})
