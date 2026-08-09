@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssue

/**
 * 本体サイトの TODO ウィンドウに出ている GitHub Issue の閲覧専用ビュー。
 * 正本は GitHub のため編集はできない(起票・クローズは GitHub 側で行う)。
 */
@Composable
internal fun TodoViewerPage(
    state: WorkbenchState,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TODO",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            state.portfolioIssues?.let { issues ->
                Text(
                    text = "${issues.totalCount} open issues",
                    style = KeiTheme.typography.chrome.copy(fontSize = 11.sp, color = colors.muted),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "閲覧専用 — 起票・編集は GitHub で",
                style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.muted),
            )
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        val issues = state.portfolioIssues
        when {
            issues == null -> Text(
                text = "Issue を取得できませんでした(本体サイト API 未接続)",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.muted),
                modifier = Modifier.padding(top = 12.dp),
            )

            issues.issues.isEmpty() -> Text(
                text = "オープンな Issue はありません",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.muted),
                modifier = Modifier.padding(top = 12.dp),
            )

            else -> Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                issues.issues.forEach { issue ->
                    IssueRow(issue = issue)
                }
            }
        }
    }
}

@Composable
private fun IssueRow(
    issue: GitHubIssue,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KeiTheme.shapes.row)
            .background(colors.islandDark)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "#${issue.number}",
            style = KeiTheme.typography.chrome.copy(fontSize = 11.sp, color = colors.muted),
        )
        issue.type?.let { type ->
            Box(
                modifier = Modifier
                    .clip(KeiTheme.shapes.pill)
                    .background(colors.chip)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = type,
                    style = KeiTheme.typography.chrome.copy(fontSize = 10.sp, color = colors.textSecondary),
                )
            }
        }
        Text(
            text = issue.title,
            style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.textPrimary),
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
private fun TodoViewerPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(900.dp, 500.dp).background(KeiTheme.colors.island)) {
            TodoViewerPage(state = PreviewWorkbenchState)
        }
    }
}
