@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.KeiTextField
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.RowListEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand

/**
 * サーバー定義ターミナルコマンド(keyword → 出力行)の編集。本体サイトの組み込みコマンドは
 * ここでは扱わず、テキスト出力コマンドの追加だけを担う。出力は IDE クロームのため英語固定。
 */
@Composable
internal fun TerminalEditorPage(
    state: WorkbenchState,
    isMobile: Boolean,
    onChangeTerminal: (TerminalCommandsContent) -> Unit,
    onClickDiscardDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        TerminalCommandsForm(
            content = state.terminal,
            onChangeTerminal = onChangeTerminal,
            onClickDiscardDraft = onClickDiscardDraft,
            modifier = Modifier.weight(1f).fillMaxSize(),
        )
        if (!isMobile) {
            Spacer(modifier = Modifier.width(WorkbenchDimensions.IslandGap))
            PreviewPane(
                componentName = "Terminal",
                contentFingerprint = state.terminal,
                modifier = Modifier.weight(1f).fillMaxSize(),
            ) {
                TerminalCommandsPreview(
                    content = state.terminal,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TerminalCommandsForm(
    content: TerminalCommandsContent,
    onChangeTerminal: (TerminalCommandsContent) -> Unit,
    onClickDiscardDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(WorkbenchDimensions.SectionGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(text = "TEXT COMMANDS")
            Spacer(modifier = Modifier.weight(1f))
            DiscardDraftTextButton(onClick = onClickDiscardDraft)
        }
        content.commands.forEachIndexed { index, command ->
            CommandCard(
                command = command,
                onChangeCommand = { updated ->
                    onChangeTerminal(
                        content.copy(
                            commands = content.commands.toMutableList().apply { set(index, updated) },
                        ),
                    )
                },
                onRemove = {
                    onChangeTerminal(
                        content.copy(commands = content.commands.toMutableList().apply { removeAt(index) }),
                    )
                },
            )
        }
        Text(
            text = "+ コマンドを追加",
            style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.mutedHigh),
            modifier = Modifier
                .clip(KeiTheme.shapes.chip)
                .clickable {
                    onChangeTerminal(
                        content.copy(commands = content.commands + TerminalTextCommand(keyword = "")),
                    )
                }
                .padding(horizontal = 4.dp, vertical = 2.dp),
        )
        Text(
            text = "help や open などの組み込みコマンドは上書きできません(本体側で予約)",
            style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.muted),
        )
    }
}

@Composable
private fun CommandCard(
    command: TerminalTextCommand,
    onChangeCommand: (TerminalTextCommand) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(KeiTheme.shapes.card)
            .background(colors.islandDark)
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            KeiTextField(
                label = "KEYWORD",
                value = command.keyword,
                onValueChange = { onChangeCommand(command.copy(keyword = it)) },
                modifier = Modifier.weight(1f),
                mono = true,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .size(18.dp)
                    .clip(KeiTheme.shapes.chip)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                KeiIcon(
                    icon = KeiTheme.icons.closeSmall,
                    contentDescription = "${command.keyword.ifEmpty { "コマンド" }} を削除",
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        KeiTextField(
            label = "DESCRIPTION (help 一覧用・英語)",
            value = command.description,
            onValueChange = { onChangeCommand(command.copy(description = it)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        RowListEditor(
            label = "OUTPUT LINES",
            rows = command.lines,
            onEditRow = { index, value ->
                onChangeCommand(
                    command.copy(lines = command.lines.toMutableList().apply { set(index, value) }),
                )
            },
            onSwapRows = { a, b ->
                onChangeCommand(
                    command.copy(
                        lines = command.lines.toMutableList().apply {
                            val tmp = this[a]
                            this[a] = this[b]
                            this[b] = tmp
                        },
                    ),
                )
            },
            onMoveRow = { from, to ->
                onChangeCommand(
                    command.copy(lines = command.lines.toMutableList().apply { add(to, removeAt(from)) }),
                )
            },
            onRemoveRow = { index ->
                onChangeCommand(command.copy(lines = command.lines.toMutableList().apply { removeAt(index) }))
            },
            onAddRow = { onChangeCommand(command.copy(lines = command.lines + "")) },
            addLabel = "+ 出力行を追加",
        )
    }
}

/** 本体ターミナルの見た目に寄せた実行例プレビュー。 */
@Composable
private fun TerminalCommandsPreview(
    content: TerminalCommandsContent,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.chip)
            .background(colors.screenshotWell)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        content.commands.forEach { command ->
            Text(
                text = "$ ${command.keyword}" +
                    if (command.description.isNotEmpty()) "  # ${command.description}" else "",
                style = KeiTheme.typography.code.copy(fontSize = 12.sp, color = colors.androidGreen),
            )
            command.lines.forEach { line ->
                Text(
                    text = line,
                    style = KeiTheme.typography.code.copy(fontSize = 12.sp, color = colors.textSecondary),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Composable
private fun TerminalEditorPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1000.dp, 640.dp).background(KeiTheme.colors.island)) {
            TerminalEditorPage(
                state = PreviewWorkbenchState,
                isMobile = false,
                onChangeTerminal = {},
                onClickDiscardDraft = {},
            )
        }
    }
}
