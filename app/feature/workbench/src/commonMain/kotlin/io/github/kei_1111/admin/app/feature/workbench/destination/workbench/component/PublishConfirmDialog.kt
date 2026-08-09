@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.model.PublishDiff
import io.github.kei_1111.admin.app.feature.workbench.model.WorkChange
import io.github.kei_1111.admin.app.feature.workbench.model.WorkChangeKind

/**
 * 公開確認ダイアログ。公開予定と公開済みの差分(項目単位)を提示し、
 * 差分がロードできるまでは確定できない(UX レビューの MUST 事項)。
 */
@Suppress("LongParameterList")
@Composable
internal fun PublishConfirmDialog(
    diff: PublishDiff?,
    diffFailed: Boolean,
    onRetry: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.scrim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .clip(KeiTheme.shapes.card)
                .background(colors.popup)
                .border(1.dp, colors.popupBorder, KeiTheme.shapes.card)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .semantics { dialog() }
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "公開する",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
            )
            when {
                diffFailed -> DiffLoadFailed(onRetry = onRetry)
                diff == null -> DiffLoading()
                else -> DiffSummary(diff = diff)
            }
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PillButton(
                    label = "キャンセル",
                    onClick = onDismiss,
                    container = colors.chip,
                )
                val enabled = diff != null
                PillButton(
                    label = "公開する",
                    onClick = { if (enabled) onConfirm() },
                    container = colors.androidGreen,
                    contentColor = colors.desk,
                    bold = true,
                    modifier = Modifier.alpha(if (enabled) 1f else colors.nonClickableAlpha),
                )
            }
        }
    }
}

@Composable
private fun DiffLoading(modifier: Modifier = Modifier) {
    Text(
        text = "公開済みとの差分を読み込み中...",
        style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = KeiTheme.colors.textSecondary),
        modifier = modifier,
    )
}

@Composable
private fun DiffLoadFailed(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "差分を読み込めませんでした。差分を確認できるまで公開できません。",
            style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.logcatError),
        )
        PillButton(
            label = "リトライ",
            onClick = onRetry,
            container = colors.selectionPill,
        )
    }
}

@Composable
private fun DiffSummary(
    diff: PublishDiff,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (diff.isFirstPublish) {
            Text(
                text = "初回公開です。すべての内容が新規に公開されます。",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.logcatWarning),
            )
        }
        SummaryHeader(diff = diff)
        if (!diff.hasChanges && !diff.isFirstPublish) {
            Text(
                text = "公開済みの内容から変更はありません。",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.muted),
            )
        }
        WorksChangesSection(changes = diff.works)
        if (diff.profileChangedFields.isNotEmpty()) {
            DiffSection(label = "PROFILE") {
                DiffRow(text = "変更: " + diff.profileChangedFields.joinToString("・"), color = colors.syntaxLink)
            }
        }
        if (diff.readmeJaChanged || diff.readmeEnChanged) {
            DiffSection(label = "README") {
                val languages = listOfNotNull(
                    "日本語".takeIf { diff.readmeJaChanged },
                    "英語".takeIf { diff.readmeEnChanged },
                )
                DiffRow(text = "変更: " + languages.joinToString("・"), color = colors.syntaxLink)
            }
        }
        TerminalChangesSection(diff = diff)
    }
}

@Composable
private fun SummaryHeader(
    diff: PublishDiff,
    modifier: Modifier = Modifier,
) {
    val counts = diff.works.groupingBy { it.kind }.eachCount()
    val parts = listOfNotNull(
        counts[WorkChangeKind.Added]?.let { "追加 $it" },
        counts[WorkChangeKind.Changed]?.let { "変更 $it" },
        counts[WorkChangeKind.Removed]?.let { "削除 $it" },
        counts[WorkChangeKind.Excluded]?.let { "除外 $it" },
    )
    if (parts.isNotEmpty()) {
        Text(
            text = "作品: " + parts.joinToString(" · "),
            style = KeiTheme.typography.cardJp.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = KeiTheme.colors.textPrimary,
            ),
            modifier = modifier,
        )
    }
}

@Composable
private fun WorksChangesSection(
    changes: List<WorkChange>,
    modifier: Modifier = Modifier,
) {
    if (changes.isEmpty()) return
    val colors = KeiTheme.colors
    DiffSection(label = "WORKS", modifier = modifier) {
        changes.forEach { change ->
            val (label, color) = when (change.kind) {
                WorkChangeKind.Added -> "追加" to colors.androidGreen
                WorkChangeKind.Changed -> "変更" to colors.syntaxLink
                WorkChangeKind.Removed -> "削除" to colors.logcatError
                WorkChangeKind.Excluded -> "除外(下書き)" to colors.muted
            }
            DiffRow(text = "${change.name} — $label", color = color)
        }
    }
}

@Composable
private fun TerminalChangesSection(
    diff: PublishDiff,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val rows = buildList {
        if (diff.terminalAdded.isNotEmpty()) add("追加: " + diff.terminalAdded.joinToString(", ") to colors.androidGreen)
        if (diff.terminalChanged.isNotEmpty()) {
            add("変更: " + diff.terminalChanged.joinToString(", ") to colors.syntaxLink)
        }
        if (diff.terminalRemoved.isNotEmpty()) {
            add("削除: " + diff.terminalRemoved.joinToString(", ") to colors.logcatError)
        }
    }
    if (rows.isEmpty()) return
    DiffSection(label = "TERMINAL", modifier = modifier) {
        rows.forEach { (text, color) -> DiffRow(text = text, color = color) }
    }
}

@Composable
private fun DiffSection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        SectionLabel(text = label)
        content()
    }
}

@Composable
private fun DiffRow(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = color),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PublishConfirmDialogPreview() {
    KeiTheme {
        Box(modifier = Modifier.size(700.dp, 560.dp)) {
            PublishConfirmDialog(
                diff = PublishDiff(
                    isFirstPublish = false,
                    works = listOf(
                        WorkChange(name = "withmo", kind = WorkChangeKind.Changed),
                        WorkChange(name = "新作アプリ", kind = WorkChangeKind.Added),
                        WorkChange(name = "旧作品", kind = WorkChangeKind.Removed),
                        WorkChange(name = "TimeLog", kind = WorkChangeKind.Excluded),
                    ),
                    profileChangedFields = listOf("表示名", "リンク"),
                    readmeJaChanged = true,
                    readmeEnChanged = false,
                    terminalAdded = listOf("coffee"),
                    terminalChanged = emptyList(),
                    terminalRemoved = emptyList(),
                ),
                diffFailed = false,
                onRetry = {},
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
