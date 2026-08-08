import io.github.kei_1111.detektPlugins
import io.github.kei_1111.libs
import io.github.kei_1111.setupDetekt
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class DetektPlugin() : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.gitlab.arturbosch.detekt")

            setupDetekt(extensions.getByType<DetektExtension>())

            dependencies {
                detektPlugins(libs.findLibrary("detekt.compose").get())
                detektPlugins(libs.findLibrary("detekt.formatting").get())
            }

            tasks.withType<Detekt>().configureEach {
                jvmTarget = "17"
            }
        }
    }
}
