@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguage
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.utils.openUrl
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.ChipsEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.KeiTextField
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.RowListEditor
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.SegmentedStatus
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.dashedBorder
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview.works.WorksAsyncImage
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview.works.WorksPreviewCard
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview.works.resolveWorksAssetUrl
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.toPortfolioWork
import io.github.kei_1111.admin.shared.model.Work
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.roundToInt

@Composable
@Suppress("LongParameterList")
internal fun WorkEditorPage(
    workId: String,
    state: WorkbenchState,
    isMobile: Boolean,
    onChangeWork: (Work) -> Unit,
    onClickAddScreenshot: (String) -> Unit,
    onClickAddIcon: (String) -> Unit,
    onClickRevertWork: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val work = state.works.firstOrNull { it.id == workId }
    if (work == null) {
        MissingWork(workId = workId, modifier = modifier)
        return
    }

    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        WorkForm(
            work = work,
            uploadingScreenshot = state.uploadingScreenshot,
            onChangeWork = onChangeWork,
            dirty = work.id in state.unsavedWorkIds,
            onClickAddScreenshot = { onClickAddScreenshot(work.id) },
            onClickAddIcon = { onClickAddIcon(work.id) },
            onClickRevertWork = { onClickRevertWork(work.id) },
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
@Suppress("LongParameterList")
private fun WorkForm(
    work: Work,
    uploadingScreenshot: Boolean,
    dirty: Boolean,
    onChangeWork: (Work) -> Unit,
    onClickAddScreenshot: () -> Unit,
    onClickAddIcon: () -> Unit,
    onClickRevertWork: () -> Unit,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (dirty) {
                    DiscardDraftTextButton(onClick = onClickRevertWork, label = "このアイテムの変更を破棄")
                    Spacer(modifier = Modifier.width(8.dp))
                }
                LanguageSegment()
            }
        }
        HeaderSection(work = work, onChangeWork = onChangeWork, onClickAddIcon = onClickAddIcon)
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
                onMoveRow = { from, to ->
                    val pairedEn = work.rolesEn.paddedTo(work.roles.size)
                    onChangeWork(work.copy(roles = work.roles.movedTo(from, to), rolesEn = pairedEn.movedTo(from, to)))
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
        ScreenshotsSection(
            work = work,
            uploading = uploadingScreenshot,
            onChangeWork = onChangeWork,
            onClickAddScreenshot = onClickAddScreenshot,
        )
        UrlsSection(work = work, onChangeWork = onChangeWork)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HeaderSection(
    work: Work,
    onChangeWork: (Work) -> Unit,
    onClickAddIcon: () -> Unit,
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
                    .background(KeiTheme.colors.chip)
                    .clickable(onClick = onClickAddIcon),
                contentAlignment = Alignment.Center,
            ) {
                if (work.iconUrl.isNotEmpty()) {
                    WorksAsyncImage(
                        url = resolveWorksAssetUrl(work.iconUrl),
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
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
            Text(
                text = "クリックで変更",
                style = KeiTheme.typography.cardJp.copy(fontSize = 9.sp, color = KeiTheme.colors.muted),
                modifier = Modifier.padding(top = 2.dp),
            )
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
private fun ScreenshotsSection(
    work: Work,
    uploading: Boolean,
    onChangeWork: (Work) -> Unit,
    onClickAddScreenshot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    // タイルのドラッグ中は表示オフセットのみ動かし、確定はドラッグ終了時に一度だけ行う
    var dragIndex by remember { mutableStateOf(-1) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    val tileStridePx = with(LocalDensity.current) { (WorkbenchDimensions.ScreenshotWidth + 8.dp).toPx() }
    Column(modifier = modifier) {
        SectionLabel(text = "SCREENSHOTS")
        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            work.screenshots.forEachIndexed { index, path ->
                key(path) {
                    ScreenshotThumbnail(
                        path = path,
                        cover = index == 0,
                        dragging = dragIndex == index,
                        dragOffsetX = if (dragIndex == index) dragOffsetX else 0f,
                        onDragStart = { dragIndex = index },
                        onDragDelta = { dragOffsetX += it },
                        onDragEnd = { cancelled ->
                            if (!cancelled && dragIndex == index) {
                                val shift = (dragOffsetX / tileStridePx).roundToInt()
                                val target = (index + shift).coerceIn(0, work.screenshots.lastIndex)
                                if (target != index) {
                                    onChangeWork(
                                        work.copy(
                                            screenshots = work.screenshots.toMutableList()
                                                .apply { add(target, removeAt(index)) },
                                        ),
                                    )
                                }
                            }
                            dragIndex = -1
                            dragOffsetX = 0f
                        },
                        onRemove = {
                            onChangeWork(
                                work.copy(screenshots = work.screenshots.toMutableList().apply { removeAt(index) }),
                            )
                        },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(WorkbenchDimensions.ScreenshotWidth, WorkbenchDimensions.ScreenshotHeight)
                    .clip(KeiTheme.shapes.chip)
                    .dashedBorder()
                    .clickable(enabled = !uploading, onClick = onClickAddScreenshot),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uploading) "…" else "+",
                    style = KeiTheme.typography.cardJp.copy(fontSize = 20.sp, color = colors.mutedHigh),
                )
            }
        }
        Text(
            text = if (uploading) "アップロード中..." else "先頭が ★ カバー(png / jpeg / webp、5MBまで)",
            style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.muted),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun ScreenshotThumbnail(
    path: String,
    cover: Boolean,
    dragging: Boolean,
    dragOffsetX: Float,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Unit,
    onDragEnd: (cancelled: Boolean) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Box(
        modifier = modifier
            .size(WorkbenchDimensions.ScreenshotWidth, WorkbenchDimensions.ScreenshotHeight)
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer { translationX = dragOffsetX }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragDelta(dragAmount.x)
                    },
                    onDragEnd = { onDragEnd(false) },
                    onDragCancel = { onDragEnd(true) },
                )
            },
    ) {
        WorksAsyncImage(
            url = resolveWorksAssetUrl(path),
            modifier = Modifier
                .fillMaxSize()
                .clip(KeiTheme.shapes.chip)
                .background(colors.screenshotWell),
        )
        if (cover) {
            Text(
                text = "★",
                style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.logcatWarning),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(16.dp)
                .clip(KeiTheme.shapes.chip)
                .background(colors.scrim)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            KeiIcon(
                icon = KeiTheme.icons.closeSmall,
                contentDescription = "スクリーンショットを削除",
            )
        }
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

private fun List<String>.movedTo(from: Int, to: Int): List<String> =
    toMutableList().apply { add(to, removeAt(from)) }

@Preview
@Composable
private fun WorkEditorPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1000.dp, 700.dp).background(KeiTheme.colors.island)) {
            WorkEditorPage(
                workId = "withmo",
                state = PreviewWorkbenchState,
                isMobile = false,
                onChangeWork = {},
                onClickAddScreenshot = {},
                onClickAddIcon = {},
                onClickRevertWork = {},
            )
        }
    }
}
