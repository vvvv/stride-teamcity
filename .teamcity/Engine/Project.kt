package Engine

import Engine.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    id("Engine")
    name = "Engine"

    buildType(Engine_BuildIOS)
    buildType(Engine_BuildWindowsD3d11)
    buildType(Engine_BuildWarningReportGenerator)
    buildType(Engine_BuildWindowsOpenGLES)
    buildType(Engine_BuildWindowsD3d12)
    buildType(Engine_BuildLinuxVulkan)
    buildType(Engine_BuildAndroid)
    buildType(Engine_BuildWindowsOpenGL)
    buildType(Engine_BuildWindowsUWP)
    buildType(Engine_BuildWindowsVulkan)
    buildType(Engine_BuildLinuxOpenGL)

    template(Engine_BuildRuntimeFromSourceTemplate)

    features {
        feature {
            id = "PROJECT_EXT_4"
            type = "IssueTracker"
            param("secure:password", "")
            param("name", "Xenko GitHub")
            param("pattern", """#(\d+)""")
            param("authType", "anonymous")
            param("repository", "https://github.com/xenko3d/xenko")
            param("type", "GithubIssues")
            param("secure:accessToken", "")
            param("username", "")
        }
        feature {
            id = "PROJECT_EXT_7"
            type = "ReportTab"
            param("startPage", ".teamcity/BuildWarningReport.zip!BuildWarningReport.html")
            param("title", "Build Warnings")
            param("type", "BuildReportTab")
        }
    }

    subProject(Engine_Deploy.Project)
    subProject(Engine_Tests.Project)
    subProject(Engine_Samples.Project)
    subProject(Engine_Package.Project)
})
