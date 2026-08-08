package io.github.kei_1111.admin.app.feature.workbench.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchScreenRoot
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchViewModel

fun EntryProviderScope<NavKey>.workbenchEntries() {
    entry<Workbench> {
        val viewModel: WorkbenchViewModel = metroViewModel()
        WorkbenchScreenRoot(viewModel = viewModel)
    }
}
