@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.SyncErrorKind
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab

@Composable
internal fun StatusBar(
    state: WorkbenchState,
    onClickRetryLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = state.breadcrumb(),
            style = KeiTheme.typography.chrome.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                color = colors.textSecondary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        state.syncError?.let { kind ->
            Text(
                text = when (kind) {
                    SyncErrorKind.Load -> "⚠ 読み込み失敗"
                    SyncErrorKind.Save -> "⚠ 保存失敗"
                    SyncErrorKind.Publish -> "⚠ 公開失敗"
                    SyncErrorKind.Upload -> "⚠ アップロード失敗"
                    SyncErrorKind.Discard -> "⚠ 破棄失敗"
                },
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = colors.logcatError,
                ),
            )
            if (kind == SyncErrorKind.Load) {
                Text(
                    text = "再試行",
                    style = KeiTheme.typography.cardJp.copy(
                        fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                        color = colors.mutedHigh,
                        textDecoration = TextDecoration.Underline,
                    ),
                    modifier = Modifier.clickable { onClickRetryLoad() },
                )
            }
        }
        if (state.saving) {
            Text(
                text = if (state.publishing) "公開中..." else "保存中...",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = colors.mutedHigh,
                ),
            )
        } else if (state.unsavedCount > 0) {
            Text(
                text = "● ${state.unsavedCount} unsaved changes",
                style = KeiTheme.typography.chrome.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = colors.logcatWarning,
                ),
            )
        } else {
            Text(
                text = "✓ すべて保存済み",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = colors.androidGreen,
                ),
            )
        }
    }
}

private fun WorkbenchState.breadcrumb(): String = when (val tab = activeTab) {
    is WorkbenchTab.WorksList -> "admin › works"
    is WorkbenchTab.WorkEditor -> "admin › works › ${works.firstOrNull { it.id == tab.workId }?.name ?: tab.workId}"
    is WorkbenchTab.ProfileEditor -> "admin › profile"
    is WorkbenchTab.ReadmeEditor -> "admin › README.md"
    is WorkbenchTab.TerminalEditor -> "admin › terminal"
    is WorkbenchTab.TodoViewer -> "admin › TODO"
}

@Preview
@Composable
private fun StatusBarPreview() {
    KeiTheme {
        Box(modifier = Modifier.width(800.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            StatusBar(state = PreviewWorkbenchState, onClickRetryLoad = {})
        }
    }
}
