@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.SectionLabel
import io.github.kei_1111.admin.app.feature.workbench.model.TechChipKind
import io.github.kei_1111.admin.app.feature.workbench.model.techChipKindOf

/** TECH STACK のチップ入力。言語/UI 系は緑、その他はグレー(本体カードと同じ色分けルール)。 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ChipsEditor(
    label: String,
    chips: List<String>,
    onChipsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var adding by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }

    fun commitDraft() {
        val value = draft.trim()
        if (value.isNotEmpty()) onChipsChange(chips + value)
        draft = ""
        adding = false
    }

    Column(modifier = modifier) {
        SectionLabel(text = label)
        FlowRow(
            modifier = Modifier.padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            chips.forEach { chip ->
                TechChip(
                    text = chip,
                    onRemove = { onChipsChange(chips - chip) },
                )
            }
            if (adding) {
                ChipDraftField(
                    value = draft,
                    onValueChange = { draft = it },
                    onCommit = ::commitDraft,
                )
            } else {
                AddChip(onClick = { adding = true })
            }
        }
    }
}

@Composable
private fun TechChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val green = techChipKindOf(text) == TechChipKind.LanguageOrUi
    val contentColor = if (green) colors.androidGreen else colors.textSecondary
    val container = if (green) colors.androidGreen.copy(alpha = 0.12f) else colors.chip
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.chip)
            .background(container)
            .padding(start = 8.dp, end = 4.dp, top = 3.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = KeiTheme.typography.cardJp.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            ),
        )
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(KeiTheme.shapes.chip)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            KeiIcon(
                icon = KeiTheme.icons.closeSmall,
                contentDescription = "$text を削除",
            )
        }
    }
}

@Composable
private fun AddChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Text(
        text = "+ 追加",
        style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.mutedHigh),
        modifier = modifier
            .clip(KeiTheme.shapes.chip)
            .dashedBorder()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun ChipDraftField(
    value: String,
    onValueChange: (String) -> Unit,
    onCommit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.textPrimary),
        cursorBrush = SolidColor(colors.focusBorder),
        modifier = modifier
            .width(120.dp)
            .clip(KeiTheme.shapes.chip)
            .background(colors.islandDark)
            .border(1.dp, colors.focusBorder, KeiTheme.shapes.chip)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                    onCommit()
                    true
                } else {
                    false
                }
            },
    )
}

/** 破線ボーダー(追加系プレースホルダー共通)。 */
@Composable
internal fun Modifier.dashedBorder(): Modifier {
    val outline = KeiTheme.colors.outline
    return border(
        width = 1.dp,
        color = outline,
        shape = KeiTheme.shapes.chip,
    )
}
