@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.ChipsEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.KeiTextField
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.RowListEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.SegmentedStatus
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.dashedBorder
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.shared.model.Work

@Composable
internal fun WorkEditorPage(
    workId: String,
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val work = state.works.firstOrNull { it.id == workId }
    if (work == null) {
        MissingWork(workId = workId, modifier = modifier)
        return
    }
    val update: (Work) -> Unit = { onIntent(WorkbenchIntent.UpdateWorkDraft(it)) }

    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        WorkForm(
            work = work,
            update = update,
            modifier = Modifier
                .weight(WorkbenchDimensions.FormWeight)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = WorkbenchDimensions.IslandGap),
        )
        PreviewPane(
            componentName = "WorksPreview",
            contentFingerprint = work,
            modifier = Modifier
                .width(WorkbenchDimensions.PreviewWidth)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun MissingWork(workId: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "作品が見つかりません: $workId",
            style = KeiTheme.typography.cardJp.copy(color = KeiTheme.colors.muted),
        )
    }
}

@Composable
private fun WorkForm(
    work: Work,
    update: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(WorkbenchDimensions.SectionGap),
    ) {
        HeaderSection(work = work, update = update)
        TypePeriodSection(work = work, update = update)
        KeiTextField(
            label = "ABOUT",
            value = work.about,
            onValueChange = { update(work.copy(about = it)) },
            singleLine = false,
            minLines = 4,
        )
        ChipsEditor(
            label = "TECH STACK",
            chips = work.techStack,
            onChipsChange = { update(work.copy(techStack = it)) },
        )
        RowListEditor(
            label = "MY ROLE",
            rows = work.roles,
            onRowsChange = { update(work.copy(roles = it)) },
        )
        ScreenshotsSection()
        UrlsSection(work = work, update = update)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HeaderSection(
    work: Work,
    update: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            SectionLabel(text = "ICON")
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(44.dp)
                    .clip(KeiTheme.shapes.card)
                    .background(KeiTheme.colors.chip),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = work.name.take(1).uppercase(),
                    style = KeiTheme.typography.cardJp.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeiTheme.colors.textPrimary,
                    ),
                )
            }
        }
        KeiTextField(
            label = "NAME",
            value = work.name,
            onValueChange = { update(work.copy(name = it)) },
            modifier = Modifier.weight(1f),
        )
        SegmentedStatus(
            status = work.status,
            onStatusChange = { update(work.copy(status = it)) },
        )
    }
}

@Composable
private fun TypePeriodSection(
    work: Work,
    update: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        KeiTextField(
            label = "TYPE",
            value = work.type,
            onValueChange = { update(work.copy(type = it)) },
            modifier = Modifier.weight(1f),
            placeholder = "Android アプリ",
        )
        KeiTextField(
            label = "PERIOD",
            value = work.period,
            onValueChange = { update(work.copy(period = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            placeholder = "2024.01 - ",
        )
    }
}

@Composable
private fun ScreenshotsSection(modifier: Modifier = Modifier) {
    val colors = KeiTheme.colors
    Column(modifier = modifier) {
        SectionLabel(text = "SCREENSHOTS")
        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 画像アップロードは次段階。カバー(★)ルールとサムネイル枠のみ先行して示す
            Box(
                modifier = Modifier
                    .size(WorkbenchDimensions.ScreenshotWidth, WorkbenchDimensions.ScreenshotHeight)
                    .clip(KeiTheme.shapes.chip)
                    .dashedBorder(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+",
                    style = KeiTheme.typography.cardJp.copy(fontSize = 20.sp, color = colors.mutedHigh),
                )
            }
        }
        Text(
            text = "画像アップロードは次段階で対応(先頭が ★ カバー)",
            style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.muted),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun UrlsSection(
    work: Work,
    update: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        KeiTextField(
            label = "GOOGLE PLAY URL",
            value = work.googlePlayUrl,
            onValueChange = { update(work.copy(googlePlayUrl = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            textColor = colors.syntaxLink,
        )
        KeiTextField(
            label = "SOURCE URL",
            value = work.sourceUrl,
            onValueChange = { update(work.copy(sourceUrl = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            textColor = colors.syntaxLink,
        )
    }
}
