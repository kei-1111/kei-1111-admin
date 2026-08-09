@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguage
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.utils.openUrl
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.ChipsEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.KeiTextField
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.RowListEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.SegmentedStatus
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.dashedBorder
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview.works.WorksPreviewCard
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.toPortfolioWork
import io.github.kei_1111.admin.shared.model.Work
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun WorkEditorPage(
    workId: String,
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
) {
    val work = state.works.firstOrNull { it.id == workId }
    if (work == null) {
        MissingWork(workId = workId, modifier = modifier)
        return
    }
    val onChangeWork: (Work) -> Unit = { onIntent(WorkbenchIntent.UpdateWorkDraft(it)) }

    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        WorkForm(
            work = work,
            onChangeWork = onChangeWork,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = if (isMobile) 0.dp else WorkbenchDimensions.IslandGap),
        )
        // Mobile では本体サイトと逆に入力フォームを優先し、Preview を畳む
        if (!isMobile) {
            PreviewPane(
                componentName = "WorksPreview",
                contentFingerprint = work,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(
                        ratio = WorkbenchDimensions.PreviewCardAspectRatio,
                        matchHeightConstraintsFirst = true,
                    ),
            ) {
                var sheetOpen by remember(workId) { mutableStateOf(false) }
                ScaledCard(
                    cardWidth = WorkbenchDimensions.WorksCardWidth,
                    cardHeight = WorkbenchDimensions.WorksCardHeight,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    WorksPreviewCard(
                        works = persistentListOf(work.toPortfolioWork()),
                        sheetOpen = sheetOpen,
                        onChangeSheetVisible = { sheetOpen = it },
                        onClickUrl = { openUrl(it) },
                    )
                }
            }
        }
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
    onChangeWork: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    val editingJa = KeiLanguageController.language == KeiLanguage.Ja
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(WorkbenchDimensions.SectionGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(text = "EDIT LANGUAGE")
            LanguageSegment()
        }
        HeaderSection(work = work, onChangeWork = onChangeWork)
        TypePeriodSection(work = work, onChangeWork = onChangeWork)
        if (editingJa) {
            KeiTextField(
                label = "ABOUT (日本語)",
                value = work.about,
                onValueChange = { onChangeWork(work.copy(about = it)) },
                singleLine = false,
                minLines = 4,
            )
        } else {
            KeiTextField(
                label = "ABOUT (ENGLISH)",
                value = work.aboutEn,
                onValueChange = { onChangeWork(work.copy(aboutEn = it)) },
                singleLine = false,
                minLines = 4,
                placeholder = "未入力の場合は日本語をそのまま配信",
            )
        }
        ChipsEditor(
            label = "TECH STACK",
            chips = work.techStack,
            onChipsChange = { onChangeWork(work.copy(techStack = it)) },
        )
        if (editingJa) {
            // 構造変更(入替・削除)は index 対の英語リストにも同じ操作をミラーする
            RowListEditor(
                label = "MY ROLE (日本語)",
                rows = work.roles,
                onEditRow = { index, value -> onChangeWork(work.copy(roles = work.roles.editedAt(index, value))) },
                onSwapRows = { a, b ->
                    val pairedEn = work.rolesEn.paddedTo(work.roles.size)
                    onChangeWork(work.copy(roles = work.roles.swappedAt(a, b), rolesEn = pairedEn.swappedAt(a, b)))
                },
                onRemoveRow = { index ->
                    val pairedEn = work.rolesEn.paddedTo(work.roles.size)
                    onChangeWork(work.copy(roles = work.roles.removedAt(index), rolesEn = pairedEn.removedAt(index)))
                },
                onAddRow = { onChangeWork(work.copy(roles = work.roles + "")) },
            )
        } else {
            RowListEditor(
                label = "MY ROLE (ENGLISH)",
                rows = work.roles.indices.map { work.rolesEn.getOrNull(it) ?: "" },
                structural = false,
                onEditRow = { index, value ->
                    val pairedEn = work.rolesEn.paddedTo(work.roles.size)
                    onChangeWork(work.copy(rolesEn = pairedEn.editedAt(index, value)))
                },
            )
        }
        ScreenshotsSection()
        UrlsSection(work = work, onChangeWork = onChangeWork)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HeaderSection(
    work: Work,
    onChangeWork: (Work) -> Unit,
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
            onValueChange = { onChangeWork(work.copy(name = it)) },
            modifier = Modifier.weight(1f),
        )
        SegmentedStatus(
            status = work.status,
            onStatusChange = { onChangeWork(work.copy(status = it)) },
        )
    }
}

@Composable
private fun TypePeriodSection(
    work: Work,
    onChangeWork: (Work) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        KeiTextField(
            label = "TYPE",
            value = work.type,
            onValueChange = { onChangeWork(work.copy(type = it)) },
            modifier = Modifier.weight(1f),
            placeholder = "Android アプリ",
        )
        KeiTextField(
            label = "PERIOD",
            value = work.period,
            onValueChange = { onChangeWork(work.copy(period = it)) },
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
    onChangeWork: (Work) -> Unit,
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
            onValueChange = { onChangeWork(work.copy(googlePlayUrl = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            textColor = colors.syntaxLink,
        )
        KeiTextField(
            label = "SOURCE URL",
            value = work.sourceUrl,
            onValueChange = { onChangeWork(work.copy(sourceUrl = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            textColor = colors.syntaxLink,
        )
    }
}

private fun List<String>.editedAt(index: Int, value: String): List<String> =
    toMutableList().apply { set(index, value) }

private fun List<String>.swappedAt(a: Int, b: Int): List<String> =
    toMutableList().apply {
        val tmp = this[a]
        this[a] = this[b]
        this[b] = tmp
    }

private fun List<String>.removedAt(index: Int): List<String> =
    toMutableList().apply { removeAt(index) }

private fun List<String>.paddedTo(size: Int): List<String> =
    if (this.size >= size) this else this + List(size - this.size) { "" }

@Preview
@Composable
private fun WorkEditorPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1000.dp, 700.dp).background(KeiTheme.colors.island)) {
            WorkEditorPage(workId = "withmo", state = PreviewWorkbenchState, onIntent = {}, isMobile = false)
        }
    }
}
