package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.ConfirmDialog
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.MainIsland
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.NavTree
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.StatusBar
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.TitleBar
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.deskBackground

@Composable
internal fun WorkbenchScreen(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .deskBackground(colors),
    ) {
        TitleBar(
            unsavedCount = state.unsavedCount,
            lastDeploy = state.lastDeploy,
            onClickSave = { onIntent(WorkbenchIntent.SaveDraft) },
            onClickPublish = { onIntent(WorkbenchIntent.RequestPublish) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = WorkbenchDimensions.DeskPadding, vertical = 6.dp),
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = WorkbenchDimensions.DeskPadding),
        ) {
            NavTree(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .width(WorkbenchDimensions.TreeWidth)
                    .fillMaxSize(),
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.width(WorkbenchDimensions.IslandGap),
            )
            MainIsland(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            )
        }
        StatusBar(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = WorkbenchDimensions.DeskPadding + 4.dp, vertical = 6.dp),
        )
    }

    state.closeConfirmTab?.let {
        ConfirmDialog(
            title = "未保存の変更",
            message = "このタブには未保存の変更があります。変更を破棄して閉じますか?",
            confirmLabel = "破棄して閉じる",
            destructive = true,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmCloseTab) },
            onDismiss = { onIntent(WorkbenchIntent.DismissCloseConfirm) },
        )
    }
    if (state.publishConfirmVisible) {
        ConfirmDialog(
            title = "公開する",
            message = "現在の下書きを本番へ公開します。よろしいですか?",
            confirmLabel = "公開する",
            destructive = false,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmPublish) },
            onDismiss = { onIntent(WorkbenchIntent.DismissPublishConfirm) },
        )
    }
}
