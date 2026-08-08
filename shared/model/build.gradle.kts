plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()
    wasmJs {
        browser()
    }
    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
