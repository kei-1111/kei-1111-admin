plugins {
    alias(libs.plugins.kei1111.detekt)
    alias(libs.plugins.kei1111.kmp.wasm)
    alias(libs.plugins.kei1111.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.core.data)
            implementation(projects.shared.model)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
