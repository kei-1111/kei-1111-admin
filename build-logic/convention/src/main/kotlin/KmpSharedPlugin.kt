import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpSharedPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "kei_1111.kmp.wasm")

            extensions.configure<KotlinMultiplatformExtension> {
                // jvm ターゲットは :server とのモデル共有用
                jvm()
            }
        }
    }
}
