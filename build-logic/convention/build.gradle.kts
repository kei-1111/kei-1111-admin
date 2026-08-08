plugins {
    `kotlin-dsl`
}

group = "io.github.kei_1111.admin.build_logic.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.compose.jb.gradle)
    compileOnly(libs.detekt.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.metro.gradle)
}

gradlePlugin {
    plugins {
        register("cmp") {
            id = libs.plugins.kei1111.cmp.get().pluginId
            implementationClass = "CmpPlugin"
        }

        register("detekt") {
            id = libs.plugins.kei1111.detekt.get().pluginId
            implementationClass = "DetektPlugin"
        }

        register("kmpFeature") {
            id = libs.plugins.kei1111.kmp.feature.get().pluginId
            implementationClass = "KmpFeaturePlugin"
        }

        register("kmpShared") {
            id = libs.plugins.kei1111.kmp.shared.get().pluginId
            implementationClass = "KmpSharedPlugin"
        }

        register("kmpWasm") {
            id = libs.plugins.kei1111.kmp.wasm.get().pluginId
            implementationClass = "KmpWasmPlugin"
        }

        register("metro") {
            id = libs.plugins.kei1111.metro.get().pluginId
            implementationClass = "MetroPlugin"
        }
    }
}
