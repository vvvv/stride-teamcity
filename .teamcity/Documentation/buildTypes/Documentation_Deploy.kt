package Documentation.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object Documentation_Deploy : BuildType({
    name = "Deploy"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        text("deployment_folder", "3.1", allowEmpty = true)
        password("deployment_password", "credentialsJSON:67959a47-9615-403f-958c-247d9c8adfb8", display = ParameterDisplay.PROMPT)
        text("deployment_profile", "build/xenko-doc.azurewebsites.net.PublishSettings", display = ParameterDisplay.PROMPT, allowEmpty = true)
    }

    vcs {
        root(Documentation.vcsRoots.Documentation_XenkoDocs)
    }

    steps {
        powerShell {
            name = "Deploy documentation to Azure"
            scriptMode = script {
                content = """
                    # Set the deployment folder 
                    ${'$'}folder = "%deployment_folder%"
                    if(!${'$'}folder)
                    {
                      ${'$'}branch = "%teamcity.build.branch%"
                      if (${'$'}branch -notmatch "^master-\d\.\d+")
                      {
                          Write-Host "You should specify the deployment folder or select a master branch."
                          Write-Host "Cannot determine deployment folder for branch [${'$'}branch]."
                          Write-Host "Branch name should be master-x.x for automatic deployment folder detection."
                          exit 1;
                      }
                      ${'$'}folder = ${'$'}branch -replace "^master-(\d)\.(\d+)",'${'$'}1.${'$'}2'
                    }
                    
                    
                    # Deploy doc on the target
                    Write-Host Executing `'build/WAWSDeploy.exe xenko_doc.zip "%deployment_profile%" /v /t ${'$'}folder /d /p %deployment_password%`'
                    build/WAWSDeploy.exe xenko_doc.zip "%deployment_profile%" /v /t ${'$'}folder /d /p %deployment_password%
                """.trimIndent()
            }
        }
        step {
            type = "PurgeCloudflareCache"
            param("CloudflareApiKey", "zxx06124ba943b8026eace74fc24dea0c32de62ad1f265464c7990a3aab55fa744973b0722275d5f162")
        }
    }

    dependencies {
        dependency(Documentation_Build) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = "*.zip => ."
            }
        }
    }
})
