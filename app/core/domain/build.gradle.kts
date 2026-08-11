import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget

plugins {
    alias(libs.plugins.kei1111.detekt)
    alias(libs.plugins.kei1111.kmp.wasm)
    alias(libs.plugins.kei1111.metro)
}

kotlin {
    // UseCase のユニットテスト (commonTest) をローカル JVM で実行するためのホストテスト。
    extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        withHostTestBuilder {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.core.data)
            implementation(projects.app.core.utils)
            implementation(projects.shared.model)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.shared.model)
        }
    }
}
