package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object Xenko : GitVcsRoot({
    name = "xenko"
    url = "git@github.com:xenko3d/xenko.git"
    branchSpec = """
        +:refs/heads/*
        +:refs/(pull/*/merge)
    """.trimIndent()
    agentCleanPolicy = GitVcsRoot.AgentCleanPolicy.ALWAYS
    authMethod = uploadedKey {
        uploadedKey = "GitHub"
    }
})
