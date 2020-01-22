package Documentation.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object Documentation_XenkoDocs : GitVcsRoot({
    name = "xenko-docs"
    url = "git@github.com:xenko3d/xenko-docs.git"
    branchSpec = "refs/heads/*"
    agentCleanPolicy = GitVcsRoot.AgentCleanPolicy.ALWAYS
    authMethod = uploadedKey {
        uploadedKey = "GitHub"
    }
})
