package Website.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object Website_XenkoWebsiteRelease : GitVcsRoot({
    name = "xenko-website - release"
    url = "git@github.com:xenko3d/xenko-website.git"
    branch = "release"
    authMethod = uploadedKey {
        uploadedKey = "GitHub"
    }
})
