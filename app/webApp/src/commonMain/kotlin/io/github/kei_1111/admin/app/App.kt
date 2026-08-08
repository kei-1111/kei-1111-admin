package io.github.kei_1111.admin.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import io.github.kei_1111.admin.app.core.designsystem.AdminTheme
import io.github.kei_1111.admin.app.di.AppGraph
import io.github.kei_1111.admin.app.navigation.AppNavDisplay

@Suppress("ModifierMissing")
@Composable
fun App(appGraph: AppGraph) {
    CompositionLocalProvider(
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
    ) {
        AdminTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                AppNavDisplay(
                    interactionLog = appGraph.interactionLog,
                    navKeySerializers = appGraph.navKeySerializers,
                )
            }
        }
    }
}
