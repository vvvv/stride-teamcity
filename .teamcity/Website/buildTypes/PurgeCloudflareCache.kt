package Website.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell

object PurgeCloudflareCache : BuildType({
    name = "Purge Cloudflare Cache"
    description = "Build configuration created from meta-runner PurgeCloudflareCache"

    params {
        password("CloudflareApiKey", "credentialsJSON:0101593a-611c-4279-aea9-67abeaed298a", display = ParameterDisplay.PROMPT)
    }

    steps {
        powerShell {
            name = "Purge Cloudflare Cache"
            formatStderrAsError = true
            scriptMode = script {
                content = """
                    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                    Invoke-WebRequest -Method DELETE "https://api.cloudflare.com/client/v4/zones/acf2ee8ca9f51870f86510ac6fe5570e/purge_cache" -Headers @{ "X-Auth-Email" = "lead@xenko.com"; "X-Auth-Key" = "%CloudflareApiKey%" } -ContentType application/json -Body '{"purge_everything":true}'
                """.trimIndent()
            }
        }
    }

    failureConditions {
        errorMessage = true
    }
})
