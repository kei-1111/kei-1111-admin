import io.github.kei_1111.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class CmpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.compose")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            // commonMain の @Preview を IDE (layoutlib) で描画するためのツーリング依存。
            // preview 用 Android target を持つモジュールにのみ配線する。
            pluginManager.withPlugin("com.android.kotlin.multiplatform.library") {
                dependencies.addProvider("androidRuntimeClasspath", libs.findLibrary("compose.ui.tooling").get())
            }
        }
    }
}
