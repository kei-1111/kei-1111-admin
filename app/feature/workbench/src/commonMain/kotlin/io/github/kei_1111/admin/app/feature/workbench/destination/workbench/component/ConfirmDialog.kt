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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme

/** 一時的な確認ダイアログ(ナビゲーション先ではないためインラインオーバーレイで描く)。 */
@Composable
internal fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    destructive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    /** 緑は公開系限定のため、保存など公開以外の確認はグレーピルで確定させる。 */
    neutralConfirm: Boolean = false,
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
                .widthIn(max = 360.dp)
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
                text = title,
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
            )
            Text(
                text = message,
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.textSecondary),
            )
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PillButton(
                    label = "キャンセル",
                    onClick = onDismiss,
                    container = colors.chip,
                )
                PillButton(
                    label = confirmLabel,
                    onClick = onConfirm,
                    container = when {
                        destructive -> colors.logcatError
                        neutralConfirm -> colors.selectionPill
                        else -> colors.androidGreen
                    },
                    contentColor = if (neutralConfirm) colors.textPrimary else colors.desk,
                    bold = true,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
    KeiTheme {
        Box(modifier = Modifier.size(600.dp, 360.dp)) {
            ConfirmDialog(
                title = "作品を削除",
                message = "「withmo」を一覧から削除します。よろしいですか?",
                confirmLabel = "削除する",
                destructive = true,
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
