import dev.zacsweers.metro.gradle.MetroPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class MetroPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "dev.zacsweers.metro")

            // Allow `internal`-qualified @ContributesBinding implementations to be referenced
            // from other modules (e.g. webApp) as well. Metro generates a top-level @Provides.
            // For details, see the upstream Metro docs:
            //   https://zacsweers.github.io/metro/latest/aggregation/#generatecontributionproviders
            extensions.configure<MetroPluginExtension> {
                @Suppress("OPT_IN_USAGE")
                generateContributionProviders.set(true)
                @Suppress("OPT_IN_USAGE")
                generateContributionHintsInFir.set(true)
            }
        }
    }
}
