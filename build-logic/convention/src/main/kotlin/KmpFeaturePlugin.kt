import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import io.github.kei_1111.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalWasmDsl::class)
class KmpFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "kei_1111.kmp.wasm")
            apply(plugin = "kei_1111.cmp")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
            apply(plugin = "kei_1111.metro")

            extensions.configure<KotlinMultiplatformExtension> {
                // feature モジュールのテストバンドルは Compose UI 経由で skiko (ブラウザ専用) をリンクするため
                // Node では起動できない — browser 実行を足して Node 実行を無効化する
                // (kei_1111.kmp.wasm 側は nodejs のみ)。
                wasmJs {
                    browser()
                    nodejs {
                        testTask {
                            enabled = false
                        }
                    }
                }

                // ViewModel ユニットテスト (commonTest) をローカル JVM で実行するためのホストテスト。
                // 全モジュール共通の kei_1111.kmp.wasm 側には置かない — テストを持たないモジュールにまで
                // テスト用コンパイルが波及するため。テストを持つ非 feature モジュールは各 build.gradle.kts で同じ設定を行う。
                extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
                    withHostTestBuilder {}
                }

                with(sourceSets) {
                    getByName("commonMain").apply {
                        dependencies {
                            implementation(project(":app:core:common"))
                            implementation(project(":app:core:designsystem"))
                            implementation(project(":app:core:domain"))
                            implementation(project(":shared:model"))
                            implementation(project(":app:core:mvi"))
                            implementation(project(":app:core:navigation"))
                            implementation(project(":app:core:ui"))
                            implementation(project(":app:core:utils"))

                            implementation(libs.findLibrary("coil.compose").get())
                            implementation(libs.findLibrary("coil.network.ktor3").get())
                            implementation(libs.findLibrary("compose.runtime").get())
                            implementation(libs.findLibrary("compose.foundation").get())
                            implementation(libs.findLibrary("compose.material3").get())
                            implementation(libs.findLibrary("compose.ui").get())
                            implementation(libs.findLibrary("compose.components.resources").get())
                            implementation(libs.findLibrary("compose.ui.tooling.preview").get())
                            implementation(libs.findLibrary("kotlinx.serialization.json").get())
                            implementation(libs.findLibrary("navigation3.runtime").get())
                            implementation(libs.findLibrary("androidx.lifecycle.viewmodel").get())
                            implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                            implementation(libs.findLibrary("metrox.viewmodel.compose").get())
                            implementation(libs.findLibrary("kotlinx.collections.immutable").get())
                            implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                        }
                    }

                    getByName("commonTest").apply {
                        dependencies {
                            implementation(project(":app:core:testing"))

                            implementation(libs.findLibrary("kotlin.test").get())
                            implementation(libs.findLibrary("kotlinx.coroutines.test").get())
                        }
                    }
                }
            }
        }
    }
}
