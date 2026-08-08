@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.SectionLabel
import io.github.kei_1111.admin.shared.model.ContentStatus

/** STATUS の 公開|下書き セグメント切替。選択状態はグレーピル(緑は公開系バッジ専用)。 */
@Composable
internal fun SegmentedStatus(
    status: ContentStatus,
    onStatusChange: (ContentStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(modifier = modifier) {
        SectionLabel(text = "STATUS")
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .clip(KeiTheme.shapes.pill)
                .background(colors.islandDark)
                .padding(2.dp),
        ) {
            Segment(
                label = "公開",
                selected = status == ContentStatus.Published,
                onClick = { onStatusChange(ContentStatus.Published) },
            )
            Segment(
                label = "下書き",
                selected = status == ContentStatus.Draft,
                onClick = { onStatusChange(ContentStatus.Draft) },
            )
        }
    }
}

@Composable
private fun Segment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Text(
        text = label,
        style = KeiTheme.typography.cardJp.copy(
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) colors.textPrimary else colors.mutedHigh,
        ),
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(if (selected) colors.selectionPill else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    )
}
