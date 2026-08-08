import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget

plugins {
    alias(libs.plugins.kei1111.detekt)
    alias(libs.plugins.kei1111.kmp.wasm)
    alias(libs.plugins.kei1111.cmp)
}

kotlin {
    // MviViewModel 自体のユニットテスト (commonTest) をローカル JVM で実行するためのホストテスト。
    // feature モジュールには kei_1111.kmp.feature が同じ設定を適用する。
    extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
        withHostTestBuilder {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.core.common)
            implementation(libs.compose.runtime)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(projects.app.core.testing)

            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
