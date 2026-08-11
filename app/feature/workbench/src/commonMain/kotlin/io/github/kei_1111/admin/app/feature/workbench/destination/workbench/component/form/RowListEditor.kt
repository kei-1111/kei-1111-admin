@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.SectionLabel
import kotlin.math.roundToInt

/**
 * MY ROLE などの行リスト編集。並び替えはハンドルのドラッグまたは ∧/∨ クリックで行う。
 */
/**
 * 行編集を操作単位(編集/入替/削除/追加)で通知する。呼び出し側が対になる別リスト
 * (英語訳など)へ同じ構造変更をミラーできるようにするため、変更後リストは渡さない。
 */
@Composable
internal fun RowListEditor(
    label: String,
    rows: List<String>,
    onEditRow: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    addLabel: String = "+ 行を追加",
    /** false のとき行の増減・並び替えを隠す(EN 編集時は構造を JA と揃えるため)。 */
    structural: Boolean = true,
    onSwapRows: (Int, Int) -> Unit = { _, _ -> },
    onMoveRow: (Int, Int) -> Unit = { _, _ -> },
    onRemoveRow: (Int) -> Unit = {},
    onAddRow: () -> Unit = {},
) {
    // ドラッグ中は表示オフセットだけ動かし、確定(リスト変更)はドラッグ終了時に一度だけ行う
    var dragIndex by remember { mutableStateOf(-1) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var rowHeightPx by remember { mutableStateOf(0f) }
    val rowGapPx = with(LocalDensity.current) { 6.dp.toPx() }
    Column(modifier = modifier) {
        SectionLabel(text = label)
        Column(
            modifier = Modifier.padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            rows.forEachIndexed { index, row ->
                EditorRow(
                    value = row,
                    structural = structural,
                    canMoveUp = index > 0,
                    canMoveDown = index < rows.lastIndex,
                    onValueChange = { onEditRow(index, it) },
                    onMoveUp = { onSwapRows(index, index - 1) },
                    onMoveDown = { onSwapRows(index, index + 1) },
                    onRemove = { onRemoveRow(index) },
                    dragging = dragIndex == index,
                    dragOffsetY = if (dragIndex == index) dragOffsetY else 0f,
                    onDragStart = { dragIndex = index },
                    onDragDelta = { dragOffsetY += it },
                    onDragEnd = { cancelled ->
                        if (!cancelled && dragIndex == index && rowHeightPx > 0f) {
                            val shift = (dragOffsetY / rowHeightPx).roundToInt()
                            val target = (index + shift).coerceIn(0, rows.lastIndex)
                            if (target != index) onMoveRow(index, target)
                        }
                        dragIndex = -1
                        dragOffsetY = 0f
                    },
                    modifier = Modifier.onGloballyPositioned {
                        // 行間 6dp を含めた 1 行分の移動量
                        if (index == 0) rowHeightPx = it.size.height + rowGapPx
                    },
                )
            }
            if (structural) {
                Text(
                    text = addLabel,
                    style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = KeiTheme.colors.mutedHigh),
                    modifier = Modifier
                        .clip(KeiTheme.shapes.chip)
                        .clickable(onClick = onAddRow)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun EditorRow(
    value: String,
    structural: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onValueChange: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    dragging: Boolean,
    dragOffsetY: Float,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Unit,
    onDragEnd: (cancelled: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer { translationY = dragOffsetY },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (structural) {
            MoveHandle(
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDragDelta(dragAmount.y)
                        },
                        onDragEnd = { onDragEnd(false) },
                        onDragCancel = { onDragEnd(true) },
                    )
                },
            )
        }
        FieldBox(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        if (structural) {
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
}

// ▲▼ のグリフは wasm の canvas 描画でフォントに載らず不可視になるため、chevron アイコンで描く
@Composable
private fun MoveHandle(
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        MoveHandleArrow(
            enabled = canMoveUp,
            upward = true,
            contentDescription = "上へ移動",
            onClick = onMoveUp,
        )
        MoveHandleArrow(
            enabled = canMoveDown,
            upward = false,
            contentDescription = "下へ移動",
            onClick = onMoveDown,
        )
    }
}

@Composable
private fun MoveHandleArrow(
    enabled: Boolean,
    upward: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .clip(KeiTheme.shapes.chip)
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else DISABLED_ARROW_ALPHA),
        contentAlignment = Alignment.Center,
    ) {
        KeiIcon(
            icon = KeiTheme.icons.chevronDown,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(12.dp)
                .rotate(if (upward) HALF_TURN_DEGREES else 0f),
        )
    }
}

private const val DISABLED_ARROW_ALPHA = 0.3f
private const val HALF_TURN_DEGREES = 180f

@Preview
@Composable
private fun RowListEditorPreview() {
    KeiTheme {
        Box(modifier = Modifier.width(520.dp).background(KeiTheme.colors.island).padding(8.dp)) {
            RowListEditor(
                label = "MY ROLE (日本語)",
                rows = listOf("Android 側の実装を担当", "Unity 連携のブリッジ設計"),
                onEditRow = { _, _ -> },
            )
        }
    }
}
