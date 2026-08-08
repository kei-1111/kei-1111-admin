package io.github.kei_1111

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private const val CompileSdk = 36
private const val MinSdk = 24

internal fun Project.configureKmpWasm(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        wasmJs {
            browser()
        }

        // IDE で commonMain の Compose Preview を描画するための Android ターゲット。
        // プレビューは Android の描画基盤 (layoutlib) に依存しており、
        // wasm 単独では表示できない。配布物はあくまで wasmJs（webApp は wasm のみ）。
        // ユニットテストを持つモジュールはこのターゲットのホストテスト（ローカル JVM）で
        // テストを実行する — 有効化は各モジュール側（.claude/rules/app-testing.md）。
        extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
            namespace = "io.github.kei_1111.admin" +
                project.path.replace(":", ".").replace("-", "_")
            compileSdk = CompileSdk
            minSdk = MinSdk
        }
    }
}
