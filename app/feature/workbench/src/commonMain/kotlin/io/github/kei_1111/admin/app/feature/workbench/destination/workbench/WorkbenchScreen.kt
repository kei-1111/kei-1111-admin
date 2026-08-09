package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.layout.WindowLayout
import io.github.kei_1111.admin.app.core.designsystem.layout.windowLayoutFor
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.ConfirmDialog
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.MobileNavRow
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.PublishConfirmDialog
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.StatusBar
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.TitleBar
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.content.WorkbenchDesktopContent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.content.WorkbenchMobileContent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.deskBackground
import io.github.kei_1111.admin.shared.model.ContentDocument

@Composable
internal fun WorkbenchScreen(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isMobile = windowLayoutFor(maxWidth) == WindowLayout.Mobile
        Column(
            modifier = Modifier
                .fillMaxSize()
                .deskBackground(colors),
        ) {
            TitleBar(
                unsavedCount = state.unsavedCount,
                saving = state.saving,
                lastDeploy = state.lastDeploy,
                compact = isMobile,
                onClickSave = { onIntent(WorkbenchIntent.SaveDraft) },
                onClickPublish = { onIntent(WorkbenchIntent.RequestPublish) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = WorkbenchDimensions.DeskPadding, vertical = 6.dp),
            )
            if (isMobile) {
                MobileNavRow(
                    state = state,
                    onSelectNode = { onIntent(WorkbenchIntent.SelectNode(it)) },
                    onClickCreateWork = { onIntent(WorkbenchIntent.CreateWork) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = WorkbenchDimensions.DeskPadding, vertical = 2.dp),
                )
                WorkbenchMobileContent(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = WorkbenchDimensions.DeskPadding),
                )
            } else {
                WorkbenchDesktopContent(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = WorkbenchDimensions.DeskPadding),
                )
            }
            StatusBar(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = WorkbenchDimensions.DeskPadding + 4.dp, vertical = 6.dp),
            )
        }
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
    state.deleteConfirmWork?.let { work ->
        ConfirmDialog(
            title = "作品を削除",
            message = "「${work.name}」を一覧から削除します。次回の下書き保存で反映されます。よろしいですか?",
            confirmLabel = "削除する",
            destructive = true,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmDeleteWork) },
            onDismiss = { onIntent(WorkbenchIntent.DismissDeleteConfirm) },
        )
    }
    state.languageOutdatedWarning?.let { warning ->
        ConfirmDialog(
            title = "翻訳が未更新です",
            message = languageOutdatedMessage(warning.items),
            confirmLabel = if (warning.alsoPublish) "このまま公開" else "このまま保存",
            destructive = false,
            neutralConfirm = !warning.alsoPublish,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmLanguageOutdated) },
            onDismiss = { onIntent(WorkbenchIntent.DismissLanguageOutdated) },
        )
    }
    if (state.publishConfirmVisible) {
        PublishConfirmDialog(
            diff = state.publishDiff,
            diffFailed = state.publishDiffFailed,
            onRetry = { onIntent(WorkbenchIntent.RequestPublish) },
            onConfirm = { onIntent(WorkbenchIntent.ConfirmPublish) },
            onDismiss = { onIntent(WorkbenchIntent.DismissPublishConfirm) },
        )
    }
    state.discardConfirmDocument?.let { document ->
        ConfirmDialog(
            title = "変更を破棄",
            message = "「${document.displayName()}」の保存済み下書きを直前の公開内容(未公開の場合は初期状態)に戻し、" +
                "未保存の変更もすべて破棄します。よろしいですか?",
            confirmLabel = "破棄する",
            destructive = true,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmDiscardDraft) },
            onDismiss = { onIntent(WorkbenchIntent.DismissDiscardConfirm) },
        )
    }
    state.revertConfirmWork?.let { work ->
        ConfirmDialog(
            title = "変更を破棄",
            message = "「${work.name}」の未保存の変更を破棄して、保存済みの内容に戻します。よろしいですか?",
            confirmLabel = "破棄する",
            destructive = true,
            onConfirm = { onIntent(WorkbenchIntent.ConfirmRevertWork) },
            onDismiss = { onIntent(WorkbenchIntent.DismissRevertConfirm) },
        )
    }
}

/** 片言語のみ変更された項目を言語別にまとめた警告文。 */
private fun languageOutdatedMessage(items: List<WorkbenchViewModelState.OutdatedItem>): String = buildString {
    val staleEn = items.filter { it.staleEnglish }
    val staleJa = items.filterNot { it.staleEnglish }
    if (staleEn.isNotEmpty()) {
        appendLine("日本語だけが変更され、英語が未更新です: " + staleEn.joinToString("、") { it.name })
    }
    if (staleJa.isNotEmpty()) {
        appendLine("英語だけが変更され、日本語が未更新です: " + staleJa.joinToString("、") { it.name })
    }
    append("未更新の言語には反映されないまま配信されます。このまま続けますか?")
}

@Preview
@Composable
private fun WorkbenchScreenPreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1280.dp, 800.dp)) {
            WorkbenchScreen(state = PreviewWorkbenchState, onIntent = {})
        }
    }
}

private fun ContentDocument.displayName(): String = when (this) {
    ContentDocument.Works -> "作品"
    ContentDocument.Profile -> "プロフィール"
    ContentDocument.Terminal -> "ターミナルコマンド"
    ContentDocument.Readme -> "README"
}
