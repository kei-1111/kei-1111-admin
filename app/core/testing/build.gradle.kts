plugins {
    alias(libs.plugins.kei1111.detekt)
    alias(libs.plugins.kei1111.kmp.wasm)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
        }

        // JVM では @BeforeTest / @AfterTest が kotlin-test-junit の typealias として提供されるため、
        // main sourceSet からアノテーションを参照するにはフレームワーク付き variant が必要。
        androidMain.dependencies {
            implementation(libs.kotlin.test.junit)
        }
    }
}
