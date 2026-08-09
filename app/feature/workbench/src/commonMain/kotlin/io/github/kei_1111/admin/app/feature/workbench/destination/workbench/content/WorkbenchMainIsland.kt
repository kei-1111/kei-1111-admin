package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.MainIsland
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode

/** MainIsland へのコールバック変換(Desktop / Mobile 共通)。 */
@Composable
internal fun WorkbenchMainIsland(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    MainIsland(
        state = state,
        isMobile = isMobile,
        onClickTab = { onIntent(WorkbenchIntent.ActivateTab(it)) },
        onCloseTab = { onIntent(WorkbenchIntent.CloseTab(it)) },
        onSelectWork = { onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(it))) },
        onClickCreateWork = { onIntent(WorkbenchIntent.CreateWork) },
        onClickDeleteWork = { onIntent(WorkbenchIntent.RequestDeleteWork(it)) },
        onChangeWork = { onIntent(WorkbenchIntent.UpdateWorkDraft(it)) },
        onClickAddScreenshot = { onIntent(WorkbenchIntent.AddScreenshot(it)) },
        onChangeProfile = { onIntent(WorkbenchIntent.UpdateProfileDraft(it)) },
        onClickAddAvatar = { onIntent(WorkbenchIntent.AddProfileAvatar) },
        onChangeTerminal = { onIntent(WorkbenchIntent.UpdateTerminalDraft(it)) },
        onChangeReadme = { onIntent(WorkbenchIntent.UpdateReadmeDraft(it)) },
        onClickRetryPreview = { onIntent(WorkbenchIntent.RetryPreview) },
        modifier = modifier,
    )
}
