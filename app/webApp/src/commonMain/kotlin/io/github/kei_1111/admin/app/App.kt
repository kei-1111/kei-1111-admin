package io.github.kei_1111.admin.app

import androidx.compose.runtime.Composable
import io.github.kei_1111.admin.app.core.designsystem.AdminTheme
import io.github.kei_1111.admin.app.feature.home.HomeScreen

@Composable
fun App() {
    AdminTheme {
        HomeScreen()
    }
}
