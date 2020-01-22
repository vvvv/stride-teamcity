package Website.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Website_BuildAndDeployRelease : BuildType({
    name = "Build And Deploy - Release (xenko.com)"
    description = "Build and Deploy of Xenko Website from staging to http://staging.xenko.com"

    params {
        password("AzurePublishPassword", "credentialsJSON:652c3cbf-5827-4ff5-af4a-f0e2d7025e31", display = ParameterDisplay.HIDDEN)
    }

    vcs {
        root(Website.vcsRoots.Website_XenkoWebsiteRelease)
    }

    steps {
        script {
            name = "Copy Azure Publish Settings"
            scriptContent = """echo ^<publishData^>^<publishProfile profileName="xenko-website - Web Deploy" publishMethod="MSDeploy" publishUrl="xenko-website.scm.azurewebsites.net:443" msdeploySite="xenko-website" userName="${'$'}xenko-website" userPWD="%AzurePublishPassword%" destinationAppUrl="http://xenko-website.azurewebsites.net" SQLServerDBConnectionString="" mySQLDBConnectionString="" hostingProviderForumLink="" controlPanelLink="http://windows.azure.com" webSystem="WebSites"^>^<databases /^>^</publishProfile^>^<publishProfile profileName="xenko-website - FTP" publishMethod="FTP" publishUrl="ftp://waws-prod-mwh-003.ftp.azurewebsites.windows.net/site/wwwroot" ftpPassiveMode="True" userName="xenko-website\${'$'}xenko-website" userPWD="%AzurePublishPassword%" destinationAppUrl="http://xenko-website.azurewebsites.net" SQLServerDBConnectionString="" mySQLDBConnectionString="" hostingProviderForumLink="" controlPanelLink="http://windows.azure.com" webSystem="WebSites"^>^<databases /^>^</publishProfile^>^</publishData^> > _deploy\xenko-website.PublishSettings"""
        }
        script {
            name = "Build and Deploy"
            workingDir = "_deploy"
            scriptContent = "call build_and_deploy_website.bat"
        }
        script {
            name = "Delete publish profile"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """del _deploy\xenko-website.PublishSettings"""
        }
        step {
            type = "PurgeCloudflareCache"
            param("CloudflareApiKey", "zxx06124ba943b8026eace74fc24dea0c32de62ad1f265464c7990a3aab55fa744973b0722275d5f162")
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
})
