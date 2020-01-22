package Engine_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Engine_Tests_WindowsSamplesD3D : BuildType({
    templates(Engine_Tests_WindowsTestsTemplate)
    name = "Windows - Samples D3D"

    artifactRules = """
        build\TestResults => TestResults
        samplesGenerated\screenshots\*.png => screenshots-windows-%build.number%.zip
    """.trimIndent()

    params {
        param("TestCategories", "Samples")
    }
    
    disableSettings("vcsTrigger")
})
