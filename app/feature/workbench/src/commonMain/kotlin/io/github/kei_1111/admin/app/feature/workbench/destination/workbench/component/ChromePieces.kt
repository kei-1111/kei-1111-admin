@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguage
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ThemedIcon
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.shared.model.ContentStatus

/** グレー(deskChip / chip)地のピルボタン。IDE チュール上の汎用アクション。 */
@Suppress("LongParameterList")
@Composable
internal fun PillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = KeiTheme.colors.deskChip,
    contentColor: Color = KeiTheme.colors.textPrimary,
    bold: Boolean = false,
    icon: ThemedIcon? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(if (hovered) container.copy(alpha = (container.alpha + 0.15f).coerceAtMost(1f)) else container)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            KeiIcon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier.size(WorkbenchDimensions.ChromeIconSize),
            )
        }
        Text(
            text = label,
            style = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
            ),
        )
    }
}

/** 公開状態バッジ。緑は公開系の意味に限定するルールの本丸。 */
@Composable
internal fun StatusBadge(
    status: ContentStatus,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val (label, color) = when (status) {
        ContentStatus.Published -> "公開中" to colors.androidGreen
        ContentStatus.Draft -> "下書き" to colors.logcatWarning
    }
    Text(
        text = label,
        modifier = modifier
            .clip(KeiTheme.shapes.badge)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = KeiTheme.typography.cardJp.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        ),
    )
}

/** 列ヘッダ・フォームラベル共通の小型モノスペースラベル(本体と共通様式)。 */
@Composable
internal fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = KeiTheme.typography.chrome.copy(
            fontSize = WorkbenchDimensions.LabelFontSize,
            letterSpacing = 1.2.sp,
            color = KeiTheme.colors.muted,
            fontWeight = FontWeight.Medium,
        ),
    )
}

/** ホバーで一段明るくなる行ベース。ツリー行・テーブル行共通(120ms 相当の軽い遷移は既定のまま)。 */
@Composable
internal fun hoverInteraction(): Pair<MutableInteractionSource, Boolean> {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    return interactionSource to hovered
}

/**
 * 編集対象言語の切替(JA/EN)。フォームの多言語フィールドと Preview カードの両方が
 * KeiLanguageController に追従する(本体サイトと同じ機構)。
 */
@Composable
internal fun LanguageSegment(modifier: Modifier = Modifier) {
    val colors = KeiTheme.colors
    val current = KeiLanguageController.language
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(colors.islandDark)
            .padding(2.dp),
    ) {
        listOf(KeiLanguage.Ja to "JA", KeiLanguage.En to "EN").forEach { (language, label) ->
            val selected = current == language
            Text(
                text = label,
                style = KeiTheme.typography.chrome.copy(
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    color = if (selected) colors.textPrimary else colors.mutedHigh,
                ),
                modifier = Modifier
                    .clip(KeiTheme.shapes.pill)
                    .background(if (selected) colors.selectionPill else Color.Transparent)
                    .clickable { KeiLanguageController.initialize(language) }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ChromePiecesPreview() {
    KeiTheme {
        Column(
            modifier = Modifier.background(KeiTheme.colors.desk).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PillButton(label = "下書き保存", onClick = {}, icon = KeiTheme.icons.save)
            StatusBadge(status = ContentStatus.Published)
            SectionLabel(text = "SECTION")
            LanguageSegment()
        }
    }
}
