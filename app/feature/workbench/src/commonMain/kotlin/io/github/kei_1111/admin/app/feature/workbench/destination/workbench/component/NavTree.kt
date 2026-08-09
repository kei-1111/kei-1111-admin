@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ThemedIcon
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.shared.model.ContentStatus

/**
 * ナビツリー(島1)。本体サイトの ProjectTree と同じ様式
 * (Project ヘッダ、FolderRow/FileRow、チェブロン整列、横スクロール)で
 * 管理対象コンテンツをディレクトリ構成風に見せる。
 */
@Composable
internal fun NavTree(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var worksExpanded by remember { mutableStateOf(true) }
    var systemExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.island)
            .background(KeiTheme.colors.islandDark),
    ) {
        ProjectPaneHeader()
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .width(IntrinsicSize.Max)
                .padding(bottom = 8.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            FolderRow(depth = 0, expanded = true, label = "kei-1111-admin")
            FolderRow(
                depth = 1,
                expanded = worksExpanded,
                label = "works",
                selected = state.selectedNode == AdminNode.Works,
                onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Works)) },
                onToggleExpand = { worksExpanded = !worksExpanded },
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(WorkbenchDimensions.TreeChevronSize)
                            .clip(KeiTheme.shapes.chip)
                            .clickable { onIntent(WorkbenchIntent.CreateWork) },
                        contentAlignment = Alignment.Center,
                    ) {
                        KeiIcon(
                            icon = KeiTheme.icons.add,
                            contentDescription = "新規作品を追加",
                        )
                    }
                },
            )
            if (worksExpanded) {
                state.works.forEach { work ->
                    FileRow(
                        depth = 2,
                        label = work.name,
                        icon = KeiTheme.icons.kotlin,
                        selected = state.selectedNode == AdminNode.WorkItem(work.id),
                        showDraftDot = work.status == ContentStatus.Draft || work.id in state.unsavedWorkIds,
                        onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(work.id))) },
                    )
                }
            }
            FileRow(
                depth = 1,
                label = "profile",
                icon = KeiTheme.icons.properties,
                selected = state.selectedNode == AdminNode.Profile,
                showDraftDot = state.profileUnsaved,
                onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.Profile)) },
            )
            FolderRow(
                depth = 1,
                expanded = systemExpanded,
                label = "system",
                onToggleExpand = { systemExpanded = !systemExpanded },
            )
            if (systemExpanded) {
                FileRow(depth = 2, label = "Deploy履歴", icon = null)
                FileRow(depth = 2, label = "設定", icon = null)
            }
        }
    }
}

@Composable
private fun ProjectPaneHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 10.dp, top = 10.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Project",
            modifier = Modifier.semantics { heading() },
            style = KeiTheme.typography.chrome.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                fontWeight = FontWeight.Medium,
                color = KeiTheme.colors.textPrimary,
            ),
        )
        Spacer(modifier = Modifier.width(4.dp))
        KeiIcon(
            icon = KeiTheme.icons.chevronDown,
            contentDescription = null,
            modifier = Modifier
                .size(WorkbenchDimensions.ChromeIconSize)
                .alpha(KeiTheme.colors.nonClickableAlpha),
        )
        Spacer(modifier = Modifier.weight(1f))
        KeiIcon(
            icon = KeiTheme.icons.moreVertical,
            contentDescription = null,
            modifier = Modifier
                .size(WorkbenchDimensions.ChromeIconSize)
                .alpha(KeiTheme.colors.nonClickableAlpha),
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun FolderRow(
    depth: Int,
    expanded: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    icon: ThemedIcon = KeiTheme.icons.folder,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onToggleExpand: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    TreeRow(depth = depth, modifier = modifier, selected = selected, onClick = onClick) {
        Box(
            modifier = Modifier
                .size(WorkbenchDimensions.TreeChevronSize)
                .then(
                    if (onToggleExpand != null) {
                        Modifier
                            .clip(KeiTheme.shapes.chip)
                            .clickable(onClick = onToggleExpand)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            KeiIcon(
                icon = if (expanded) KeiTheme.icons.chevronDown else KeiTheme.icons.chevronRight,
                contentDescription = if (expanded) "折りたたむ" else "展開する",
            )
        }
        Spacer(modifier = Modifier.width(WorkbenchDimensions.TreeChevronGap))
        TreeIcon(icon)
        Spacer(modifier = Modifier.width(WorkbenchDimensions.TreeIconLabelGap))
        TreeLabel(label = label, color = KeiTheme.colors.textPrimary)
        if (trailing != null) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(WorkbenchDimensions.TreeIconLabelGap))
            trailing()
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun FileRow(
    depth: Int,
    label: String,
    icon: ThemedIcon?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showDraftDot: Boolean = false,
    muted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    TreeRow(depth = depth, modifier = modifier, selected = selected, onClick = onClick) {
        Spacer(
            modifier = Modifier.width(WorkbenchDimensions.TreeChevronSize + WorkbenchDimensions.TreeChevronGap),
        )
        if (icon != null) {
            TreeIcon(icon)
            Spacer(modifier = Modifier.width(WorkbenchDimensions.TreeIconLabelGap))
        }
        TreeLabel(
            label = label,
            color = when {
                muted -> KeiTheme.colors.mutedHigh
                selected -> KeiTheme.colors.textPrimary
                else -> KeiTheme.colors.textCode
            },
        )
        if (showDraftDot) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(KeiTheme.colors.logcatWarning),
            )
        }
    }
}

@Composable
private fun TreeRow(
    depth: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val (interactionSource, hovered) = hoverInteraction()
    val clickable = onClick != null
    val background = when {
        selected -> KeiTheme.colors.selectionPill
        hovered && clickable -> KeiTheme.colors.chip
        else -> Color.Transparent
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KeiTheme.shapes.row)
            .background(background)
            .hoverable(interactionSource)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .alpha(if (clickable) 1f else KeiTheme.colors.nonClickableAlpha)
            .padding(
                start = WorkbenchDimensions.TreeLeftInset + WorkbenchDimensions.TreeIndentStep * depth,
                end = 6.dp,
                top = 4.dp,
                bottom = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun TreeIcon(
    icon: ThemedIcon,
    modifier: Modifier = Modifier,
) {
    KeiIcon(
        icon = icon,
        contentDescription = null,
        modifier = modifier.size(WorkbenchDimensions.TreeIconSize),
    )
}

@Composable
private fun TreeLabel(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier,
        style = KeiTheme.typography.chrome.copy(
            fontSize = WorkbenchDimensions.ChromeLabelFontSize,
            color = color,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview
@Composable
private fun NavTreePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(260.dp, 560.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            NavTree(state = PreviewWorkbenchState, onIntent = {})
        }
    }
}
