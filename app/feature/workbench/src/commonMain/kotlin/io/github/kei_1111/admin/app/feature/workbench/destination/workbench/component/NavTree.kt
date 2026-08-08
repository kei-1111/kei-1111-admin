@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ThemedIcon
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.shared.model.ContentStatus

@Composable
internal fun NavTree(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var worksExpanded by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.island)
            .background(KeiTheme.colors.islandDark)
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        TreeSectionLabel("CONTENT")
        TreeRow(
            label = "Works (${state.works.size})",
            icon = KeiTheme.icons.folder,
            selected = state.selectedNode == AdminNode.Works,
            expandable = true,
            expanded = worksExpanded,
            onToggleExpand = { worksExpanded = !worksExpanded },
            onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Works)) },
        )
        if (worksExpanded) {
            state.works.forEach { work ->
                TreeRow(
                    label = work.name,
                    icon = KeiTheme.icons.classKotlin,
                    selected = state.selectedNode == AdminNode.WorkItem(work.id),
                    indent = 1,
                    showDraftDot = work.status == ContentStatus.Draft || work.id in state.unsavedWorkIds,
                    onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(work.id))) },
                )
            }
            NewWorkRow(
                indent = 1,
                onClick = { onIntent(WorkbenchIntent.CreateWork) },
            )
        }
        TreeRow(
            label = "Profile",
            icon = KeiTheme.icons.properties,
            selected = state.selectedNode == AdminNode.Profile,
            showDraftDot = state.profileUnsaved,
            onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Profile)) },
        )
        TreeRow(
            label = "Licence",
            icon = KeiTheme.icons.markdown,
            selected = state.selectedNode == AdminNode.Licence,
            enabled = false,
            onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Licence)) },
        )
        Spacer(modifier = Modifier.height(10.dp))
        TreeSectionLabel("SYSTEM")
        TreeRow(
            label = "Deploy履歴",
            icon = null,
            selected = state.selectedNode == AdminNode.DeployHistory,
            enabled = false,
            onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.DeployHistory)) },
        )
        TreeRow(
            label = "設定",
            icon = null,
            selected = state.selectedNode == AdminNode.Settings,
            enabled = false,
            onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Settings)) },
        )
    }
}

@Composable
private fun TreeSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(start = 6.dp, top = 4.dp, bottom = 2.dp),
        style = KeiTheme.typography.chrome.copy(
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = KeiTheme.colors.muted,
            fontWeight = FontWeight.Medium,
        ),
    )
}

@Suppress("LongParameterList")
@Composable
private fun TreeRow(
    label: String,
    icon: ThemedIcon?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indent: Int = 0,
    expandable: Boolean = false,
    expanded: Boolean = false,
    onToggleExpand: (() -> Unit)? = null,
    showDraftDot: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = KeiTheme.colors
    val (interactionSource, hovered) = hoverInteraction()
    val background = when {
        selected -> colors.selectionPill
        hovered && enabled -> colors.chip
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WorkbenchDimensions.TreeRowHeight)
            .clip(KeiTheme.shapes.row)
            .background(background)
            .hoverable(interactionSource, enabled = enabled)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(start = 6.dp + WorkbenchDimensions.TreeIndentStep * indent, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (expandable) {
            Box(
                modifier = Modifier
                    .size(WorkbenchDimensions.TreeChevronSize)
                    .clickable(onClick = { onToggleExpand?.invoke() }),
                contentAlignment = Alignment.Center,
            ) {
                KeiIcon(
                    icon = if (expanded) KeiTheme.icons.chevronDown else KeiTheme.icons.chevronRight,
                    contentDescription = if (expanded) "折りたたむ" else "展開する",
                )
            }
            Spacer(modifier = Modifier.width(3.dp))
        } else if (indent == 0) {
            Spacer(modifier = Modifier.width(WorkbenchDimensions.TreeChevronSize + 3.dp))
        }
        if (icon != null) {
            KeiIcon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier.size(WorkbenchDimensions.TreeIconSize),
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                color = if (enabled) colors.textPrimary else colors.muted,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
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

@Composable
private fun NewWorkRow(
    indent: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val (interactionSource, hovered) = hoverInteraction()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WorkbenchDimensions.TreeRowHeight)
            .clip(KeiTheme.shapes.row)
            .background(if (hovered) colors.chip else androidx.compose.ui.graphics.Color.Transparent)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(start = 6.dp + WorkbenchDimensions.TreeIndentStep * indent, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "+ 新規作品",
            style = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                color = colors.mutedHigh,
            ),
        )
    }
}
