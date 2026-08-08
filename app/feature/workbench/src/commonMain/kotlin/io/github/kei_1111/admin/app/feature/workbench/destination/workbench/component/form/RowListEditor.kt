@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.SectionLabel

/**
 * MY ROLE などの行リスト編集。並び替えは ↑/↓ で行う(ドラッグ並び替えは
 * ポインタ精度の検証込みで後続対応、ハンドル表示のみ先行)。
 */
@Composable
internal fun RowListEditor(
    label: String,
    rows: List<String>,
    onRowsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    addLabel: String = "+ 行を追加",
) {
    Column(modifier = modifier) {
        SectionLabel(text = label)
        Column(
            modifier = Modifier.padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            rows.forEachIndexed { index, row ->
                EditorRow(
                    value = row,
                    canMoveUp = index > 0,
                    canMoveDown = index < rows.lastIndex,
                    onValueChange = { onRowsChange(rows.toMutableList().apply { set(index, it) }) },
                    onMoveUp = { onRowsChange(rows.swapped(index, index - 1)) },
                    onMoveDown = { onRowsChange(rows.swapped(index, index + 1)) },
                    onRemove = { onRowsChange(rows.toMutableList().apply { removeAt(index) }) },
                )
            }
            Text(
                text = addLabel,
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = KeiTheme.colors.mutedHigh),
                modifier = Modifier
                    .clip(KeiTheme.shapes.chip)
                    .clickable { onRowsChange(rows + "") }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}

private fun List<String>.swapped(a: Int, b: Int): List<String> =
    toMutableList().apply {
        val tmp = this[a]
        this[a] = this[b]
        this[b] = tmp
    }

@Suppress("LongParameterList")
@Composable
private fun EditorRow(
    value: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onValueChange: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoveHandle(
            canMoveUp = canMoveUp,
            canMoveDown = canMoveDown,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
        )
        FieldBox(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(KeiTheme.shapes.chip)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            KeiIcon(
                icon = KeiTheme.icons.closeSmall,
                contentDescription = "行を削除",
            )
        }
    }
}

@Composable
private fun MoveHandle(
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "▲",
            style = KeiTheme.typography.chrome.copy(
                fontSize = 8.sp,
                color = if (canMoveUp) colors.mutedHigh else colors.muted.copy(alpha = 0.3f),
            ),
            modifier = Modifier
                .clip(KeiTheme.shapes.chip)
                .clickable(enabled = canMoveUp, onClick = onMoveUp)
                .padding(horizontal = 4.dp),
        )
        Text(
            text = "▼",
            style = KeiTheme.typography.chrome.copy(
                fontSize = 8.sp,
                color = if (canMoveDown) colors.mutedHigh else colors.muted.copy(alpha = 0.3f),
            ),
            modifier = Modifier
                .clip(KeiTheme.shapes.chip)
                .clickable(enabled = canMoveDown, onClick = onMoveDown)
                .padding(horizontal = 4.dp),
        )
    }
}
