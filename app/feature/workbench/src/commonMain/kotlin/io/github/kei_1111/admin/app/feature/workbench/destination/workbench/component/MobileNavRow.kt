@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.shared.model.ContentStatus

/** Mobile 幅ではナビツリー島の代わりに横スクロールのチップ列でノードを選ぶ。 */
@Composable
internal fun MobileNavRow(
    state: WorkbenchState,
    onSelectNode: (AdminNode) -> Unit,
    onClickCreateWork: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavChip(
            label = "Works",
            selected = state.selectedNode == AdminNode.Works,
            onClick = { onSelectNode(AdminNode.Works) },
        )
        state.works.forEach { work ->
            NavChip(
                label = work.name,
                selected = state.selectedNode == AdminNode.WorkItem(work.id),
                showDraftDot = work.status == ContentStatus.Draft || work.id in state.unsavedWorkIds,
                onClick = { onSelectNode(AdminNode.WorkItem(work.id)) },
            )
        }
        NavChip(
            label = "+ 新規",
            selected = false,
            onClick = onClickCreateWork,
        )
        NavChip(
            label = "README",
            selected = state.selectedNode == AdminNode.Readme,
            onClick = { onSelectNode(AdminNode.Readme) },
        )
        NavChip(
            label = "Terminal",
            selected = state.selectedNode == AdminNode.Terminal,
            onClick = { onSelectNode(AdminNode.Terminal) },
        )
        NavChip(
            label = "Profile",
            selected = state.selectedNode == AdminNode.Profile,
            showDraftDot = state.profileUnsaved,
            onClick = { onSelectNode(AdminNode.Profile) },
        )
    }
}

@Composable
private fun NavChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDraftDot: Boolean = false,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(if (selected) colors.selectionPill else colors.deskChip)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = colors.textPrimary,
            ),
        )
        if (showDraftDot) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(colors.logcatWarning),
            )
        }
    }
}

@Preview
@Composable
private fun MobileNavRowPreview() {
    KeiTheme {
        Box(modifier = Modifier.width(360.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            MobileNavRow(state = PreviewWorkbenchState, onSelectNode = {}, onClickCreateWork = {})
        }
    }
}
