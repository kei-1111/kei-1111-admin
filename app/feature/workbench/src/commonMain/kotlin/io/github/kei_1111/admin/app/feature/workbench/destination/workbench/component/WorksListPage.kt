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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.shared.model.Work

@Composable
internal fun WorksListPage(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val visibleWorks = state.works.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }

    Column(
        modifier = modifier.padding(WorkbenchDimensions.IslandPadding),
    ) {
        ListHeader(
            total = state.works.size,
            query = query,
            isMobile = isMobile,
            onChangeQuery = { query = it },
            onClickCreate = { onIntent(WorkbenchIntent.CreateWork) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.loading) {
            Text(
                text = "読み込み中...",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = KeiTheme.colors.muted),
                modifier = Modifier.padding(10.dp),
            )
            return@Column
        }
        ColumnHeaders(isMobile = isMobile, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            visibleWorks.forEach { work ->
                WorkRow(
                    work = work,
                    isMobile = isMobile,
                    onClick = { onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(work.id))) },
                    onClickDelete = { onIntent(WorkbenchIntent.RequestDeleteWork(work.id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun ListHeader(
    total: Int,
    query: String,
    isMobile: Boolean,
    onChangeQuery: (String) -> Unit,
    onClickCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Works",
            style = KeiTheme.typography.cardJp.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            ),
        )
        Text(
            text = "$total items",
            style = KeiTheme.typography.chrome.copy(fontSize = 11.sp, color = colors.muted),
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!isMobile) {
            SearchField(query = query, onChangeQuery = onChangeQuery)
        }
        PillButton(
            label = if (isMobile) "+ 新規" else "+ 新規作品",
            onClick = onClickCreate,
            container = colors.androidGreen,
            contentColor = colors.desk,
            bold = true,
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onChangeQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.chip)
            .background(colors.chip)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KeiIcon(
            icon = KeiTheme.icons.search,
            contentDescription = "検索",
            tint = colors.mutedHigh,
            modifier = Modifier.size(WorkbenchDimensions.ChromeIconSize),
        )
        BasicTextField(
            value = query,
            onValueChange = onChangeQuery,
            singleLine = true,
            textStyle = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                color = colors.textPrimary,
            ),
            cursorBrush = SolidColor(colors.focusBorder),
            modifier = Modifier.width(140.dp),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "検索",
                        style = KeiTheme.typography.cardJp.copy(
                            fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                            color = colors.muted,
                        ),
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun ColumnHeaders(
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(28.dp))
        SectionLabel("NAME", modifier = Modifier.weight(0.32f))
        if (!isMobile) {
            SectionLabel("STACK", modifier = Modifier.weight(0.36f))
            SectionLabel("UPDATED", modifier = Modifier.width(84.dp))
        }
        SectionLabel("STATUS", modifier = Modifier.width(76.dp))
        if (!isMobile) {
            Spacer(modifier = Modifier.width(52.dp))
        }
    }
}

@Composable
private fun WorkRow(
    work: Work,
    isMobile: Boolean,
    onClick: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val (interactionSource, hovered) = hoverInteraction()
    Row(
        modifier = modifier
            .height(WorkbenchDimensions.TableRowHeight)
            .clip(KeiTheme.shapes.row)
            .background(if (hovered) colors.chip else Color.Transparent)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KeiIcon(
            icon = KeiTheme.icons.classKotlin,
            contentDescription = null,
            modifier = Modifier.size(WorkbenchDimensions.TreeIconSize),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(0.32f)) {
            Text(
                text = work.name,
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = work.type,
                style = KeiTheme.typography.cardJp.copy(fontSize = 11.sp, color = colors.muted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!isMobile) {
            Text(
                text = work.techStack.take(3).joinToString(", "),
                style = KeiTheme.typography.chrome.copy(fontSize = 11.sp, color = colors.androidGreen),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.36f),
            )
            Text(
                text = work.updatedAt,
                style = KeiTheme.typography.chrome.copy(fontSize = 11.sp, color = colors.muted),
                modifier = Modifier.width(84.dp),
            )
        }
        androidx.compose.foundation.layout.Box(modifier = Modifier.width(76.dp)) {
            StatusBadge(status = work.status)
        }
        if (!isMobile) {
            Text(
                text = "編集 ›",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.syntaxLink),
                modifier = Modifier.width(52.dp),
            )
        }
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(20.dp)
                .clip(KeiTheme.shapes.chip)
                .clickable(onClick = onClickDelete),
            contentAlignment = Alignment.Center,
        ) {
            KeiIcon(
                icon = KeiTheme.icons.closeSmall,
                contentDescription = "${work.name} を削除",
            )
        }
    }
}

@Preview
@Composable
private fun WorksListPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1000.dp, 560.dp).background(KeiTheme.colors.island)) {
            WorksListPage(state = PreviewWorkbenchState, onIntent = {}, isMobile = false)
        }
    }
}
