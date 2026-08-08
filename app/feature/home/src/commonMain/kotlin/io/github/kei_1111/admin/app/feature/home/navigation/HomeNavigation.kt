package io.github.kei_1111.admin.app.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import io.github.kei_1111.admin.app.feature.home.destination.home.HomeScreenRoot
import io.github.kei_1111.admin.app.feature.home.destination.home.HomeViewModel

fun EntryProviderScope<NavKey>.homeEntries() {
    entry<Home> {
        val viewModel: HomeViewModel = metroViewModel()
        HomeScreenRoot(viewModel = viewModel)
    }
}
