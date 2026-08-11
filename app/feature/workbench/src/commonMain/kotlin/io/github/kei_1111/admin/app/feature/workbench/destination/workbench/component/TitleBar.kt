@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ProfileIconImage
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun TitleBar(
    unsavedCount: Int,
    lastDeploy: String,
    onClickSave: () -> Unit,
    onClickPublish: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    saving: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProjectPill()
        Spacer(modifier = Modifier.weight(1f))
        if (lastDeploy.isNotEmpty() && !compact) {
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
                text = if (compact) "未保存" else "未保存の変更",
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
        // 保存中・読み込み未完了時は両ボタンを減光して無効化する
        val actionAlpha = if (saving || !enabled) KeiTheme.colors.nonClickableAlpha else 1f
        PillButton(
            label = if (compact) "保存" else "下書き保存",
            onClick = { if (!saving && enabled) onClickSave() },
            icon = KeiTheme.icons.save,
            modifier = Modifier.alpha(actionAlpha),
        )
        // 実 AS の Run ボタン同様、緑はアイコン側だけに乗せてボタン自体はチュール色に留める
        PillButton(
            label = if (compact) "公開" else "公開する",
            onClick = { if (!saving && enabled) onClickPublish() },
            icon = KeiTheme.icons.run,
            modifier = Modifier.alpha(actionAlpha),
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
        Image(
            painter = painterResource(ProfileIconImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(WorkbenchDimensions.TitleBarIconSize)
                .clip(KeiTheme.shapes.chip),
        )
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

@Preview
@Composable
private fun TitleBarPreview() {
    KeiTheme {
        Box(modifier = Modifier.width(900.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            TitleBar(
                unsavedCount = 1,
                lastDeploy = "2026-08-08 12:34",
                onClickSave = {},
                onClickPublish = {},
            )
        }
    }
}
