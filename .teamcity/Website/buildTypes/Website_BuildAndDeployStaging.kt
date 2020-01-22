package Website.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object Website_BuildAndDeployStaging : BuildType({
    name = "Build And Deploy - Staging (staging.xenko.com)"
    description = "Build and Deploy of Xenko Website from staging to http://staging.xenko.com"

    params {
        password("env.WEBCONFIG_TRANSFORM_PARAMETERS", "credentialsJSON:e1454ba7-240d-47de-bb9b-1ed477ed685f", description = "htpasswd (format: username:XXX;password:YYY)")
        param("env.WEBCONFIG_TRANSFORM", "staging")
        password("AzurePublishPassword", "credentialsJSON:47db6938-f5bf-4a9b-9177-11340d3cfbbf", display = ParameterDisplay.HIDDEN)
    }

    vcs {
        root(Website.vcsRoots.Website_XenkoWebsiteStaging)
    }

    steps {
        script {
            name = "Copy Azure Publish Settings"
            scriptContent = """echo ^<publishData^>^<publishProfile profileName="xenko-website-staging - Web Deploy" publishMethod="MSDeploy" publishUrl="xenko-website-staging.scm.azurewebsites.net:443" msdeploySite="xenko-website__staging" userName="${'$'}xenko-website__staging" userPWD="%AzurePublishPassword%" destinationAppUrl="http://xenko-website-staging.azurewebsites.net" SQLServerDBConnectionString="" mySQLDBConnectionString="" hostingProviderForumLink="" controlPanelLink="http://windows.azure.com" webSystem="WebSites"^>^<databases /^>^</publishProfile^>^<publishProfile profileName="xenko-website-staging - FTP" publishMethod="FTP" publishUrl="ftp://waws-prod-mwh-003.ftp.azurewebsites.windows.net/site/wwwroot" ftpPassiveMode="True" userName="xenko-website__staging\${'$'}xenko-website__staging" userPWD="%AzurePublishPassword%" destinationAppUrl="http://xenko-website-staging.azurewebsites.net" SQLServerDBConnectionString="" mySQLDBConnectionString="" hostingProviderForumLink="" controlPanelLink="http://windows.azure.com" webSystem="WebSites"^>^<databases /^>^</publishProfile^>^</publishData^> > _deploy\xenko-website.PublishSettings"""
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
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
})
