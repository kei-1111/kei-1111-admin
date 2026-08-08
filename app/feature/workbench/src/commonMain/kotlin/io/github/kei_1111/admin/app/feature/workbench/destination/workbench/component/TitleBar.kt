@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions

@Composable
internal fun TitleBar(
    unsavedCount: Int,
    lastDeploy: String,
    onClickSave: () -> Unit,
    onClickPublish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProjectPill()
        Spacer(modifier = Modifier.weight(1f))
        if (lastDeploy.isNotEmpty()) {
            Text(
                text = "last deploy: $lastDeploy ✓",
                style = KeiTheme.typography.chrome.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = colors.muted,
                ),
            )
        }
        if (unsavedCount > 0) {
            Text(
                text = "未保存の変更",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    fontWeight = FontWeight.Medium,
                    color = colors.logcatWarning,
                ),
                modifier = Modifier
                    .clip(KeiTheme.shapes.pill)
                    .background(colors.logcatWarning.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
        PillButton(
            label = "下書き保存",
            onClick = onClickSave,
        )
        PillButton(
            label = "▶ 公開する",
            onClick = onClickPublish,
            container = colors.androidGreen,
            contentColor = colors.desk,
            bold = true,
        )
    }
}

@Composable
private fun ProjectPill(modifier: Modifier = Modifier) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(colors.deskChip)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // プロジェクト識別色のタイル(実 AS のプロジェクトアイコンに相当)
        Box(
            modifier = Modifier
                .size(WorkbenchDimensions.TitleBarIconSize)
                .clip(KeiTheme.shapes.chip)
                .background(colors.androidGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "K",
                style = KeiTheme.typography.chrome.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.desk,
                ),
            )
        }
        Text(
            text = "kei-1111-admin",
            style = KeiTheme.typography.chrome.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            ),
        )
        KeiIcon(
            icon = KeiTheme.icons.chevronDown,
            contentDescription = null,
            modifier = Modifier
                .size(WorkbenchDimensions.ChromeIconSize)
                .alpha(colors.nonClickableAlpha),
        )
    }
}
