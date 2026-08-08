plugins {
    alias(libs.plugins.kei1111.detekt)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
}

application {
    mainClass.set("io.github.kei_1111.admin.server.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("server-all.jar")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// デプロイビルドのみ UI の wasm 配信物を jar に同梱する(-PbundleWebApp)。
// 常時依存にすると :server:test まで wasm ビルドを待つことになるためオプトイン。
if (providers.gradleProperty("bundleWebApp").isPresent) {
    tasks.processResources {
        dependsOn(":app:webApp:wasmJsBrowserDistribution")
        from(project(":app:webApp").layout.buildDirectory.dir("dist/wasmJs/productionExecutable")) {
            into("static")
        }
    }
}

dependencies {
    implementation(projects.shared.model)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.server.test.host)
}
