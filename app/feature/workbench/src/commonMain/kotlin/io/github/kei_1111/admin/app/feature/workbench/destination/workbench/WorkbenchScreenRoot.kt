package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.kei_1111.admin.app.feature.workbench.WorkbenchShortcuts
import kotlinx.coroutines.flow.drop

@Composable
internal fun WorkbenchScreenRoot(
    viewModel: WorkbenchViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        WorkbenchShortcuts.saveTick.drop(1).collect { viewModel.onIntent(WorkbenchIntent.SaveDraft) }
    }
    LaunchedEffect(Unit) {
        WorkbenchShortcuts.publishTick.drop(1).collect { viewModel.onIntent(WorkbenchIntent.RequestPublish) }
    }

    WorkbenchScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
