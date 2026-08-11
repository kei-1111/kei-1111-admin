@file:Suppress("MagicNumber", "TooManyFunctions")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.portfoliopreview.works

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.utils.prefersReducedMotion
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.portfoliopreview.SectionLabel
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchAnimations
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.forLanguage
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewWorks
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.Work
import io.github.kei_1111.admin.shared.model.portfolio.WorkTag
import kotlinx.collections.immutable.ImmutableList

/**
 * ナビゲーション destination ではなく、Profile 画面が state として持つ「Works シート開閉」
 * （worksSheetOpen）に紐づく画面内コンポーネント（LicenseSheetOverlay と同じ位置付け）。
 * カード側が現在選択中の [work] を常に渡すため、License と異なり null 許容にしていない。
 */
@Composable
internal fun WorksDetailSheetOverlay(
    work: Work,
    visible: Boolean,
    onDismiss: () -> Unit,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 視覚効果を減らす設定では開閉を即時にする（ui-implementation.md のアニメーション規約）
    val isReducedMotion = remember { prefersReducedMotion() }
    val transitionMillis = if (isReducedMotion) 0 else WorkbenchAnimations.SheetTransitionMillis
    Box(modifier = modifier) {
        SheetScrim(
            visible = visible,
            transitionMillis = transitionMillis,
            onClickScrim = onDismiss,
            modifier = Modifier.fillMaxSize(),
        )
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(transitionMillis)) { it } +
                fadeIn(tween(transitionMillis)),
            exit = slideOutVertically(tween(transitionMillis)) { it } +
                fadeOut(tween(transitionMillis)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            WorksDetailSheet(work = work, onClickClose = onDismiss, onClickUrl = onClickUrl)
        }
    }
}

@Composable
private fun SheetScrim(
    visible: Boolean,
    transitionMillis: Int,
    onClickScrim: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(transitionMillis)),
        exit = fadeOut(tween(transitionMillis)),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KeiTheme.colors.scrim)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClickScrim,
                ),
        )
    }
}

@Composable
private fun WorksDetailSheet(
    work: Work,
    onClickClose: () -> Unit,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(WorkbenchDimensions.WorksSheetHeightFraction)
            .clip(KeiTheme.shapes.sheet)
            .background(KeiTheme.colors.island),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SheetDragHandle()
        SheetHeader(
            work = work,
            onClickClose = onClickClose,
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
        )
        HorizontalDivider(color = KeiTheme.colors.outline)
        SheetBody(
            work = work,
            onClickUrl = onClickUrl,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .size(width = 28.dp, height = 3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(KeiTheme.colors.selectionPill),
    )
}

@Composable
private fun SheetHeader(
    work: Work,
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SheetIcon(iconUrl = work.iconUrl)
        SheetTitleBlock(work = work, modifier = Modifier.weight(1f))
        SheetCloseButton(onClick = onClickClose)
    }
}

@Composable
private fun SheetIcon(
    iconUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(KeiTheme.shapes.card)
            .background(KeiTheme.colors.gitHubItem),
        contentAlignment = Alignment.Center,
    ) {
        KeiIcon(
            icon = KeiTheme.icons.kotlin,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        if (iconUrl != null) {
            WorksAsyncImage(
                url = iconUrl,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

@Composable
private fun SheetTitleBlock(
    work: Work,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = work.name,
            style = KeiTheme.typography.chrome.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = KeiTheme.colors.textPrimary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${work.kind} · ${work.period}",
            style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = KeiTheme.colors.androidGreen),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SheetCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(KeiTheme.colors.gitHubItem)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        KeiIcon(
            icon = KeiTheme.icons.closeSmall,
            contentDescription = "詳細を閉じる",
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun SheetBody(
    work: Work,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(WorkbenchDimensions.GitHubCardSectionGap),
    ) {
        AboutSection(description = work.description)
        if (work.tags.isNotEmpty()) {
            TechStackSection(tags = work.tags)
        }
        if (work.roles.isNotEmpty()) {
            RolesSection(roles = work.roles)
        }
        if (work.storeUrl != null || work.sourceUrl != null) {
            LinksSection(storeUrl = work.storeUrl, sourceUrl = work.sourceUrl, onClickUrl = onClickUrl)
        }
    }
}

@Composable
private fun AboutSection(
    description: LocalizedText,
    modifier: Modifier = Modifier,
) {
    val language = KeiLanguageController.language
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(text = "ABOUT")
        Text(
            text = description.forLanguage(language),
            style = KeiTheme.typography.githubJp.copy(
                fontSize = 9.5.sp,
                lineHeight = 17.sp,
                color = KeiTheme.colors.textSecondary,
            ),
        )
    }
}

@Composable
private fun TechStackSection(
    tags: ImmutableList<WorkTag>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(text = "TECH STACK")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tags.forEach { tag -> WorksTagChip(tag = tag) }
        }
    }
}

@Composable
private fun RolesSection(
    roles: ImmutableList<LocalizedText>,
    modifier: Modifier = Modifier,
) {
    val language = KeiLanguageController.language
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(text = "MY ROLE")
        roles.forEach { role -> RoleRow(text = role.forLanguage(language)) }
    }
}

@Composable
private fun RoleRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "-",
            style = KeiTheme.typography.chrome.copy(fontSize = 9.5.sp, color = KeiTheme.colors.textSecondary),
        )
        Text(
            text = text,
            style = KeiTheme.typography.githubJp.copy(fontSize = 9.5.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

@Composable
private fun LinksSection(
    storeUrl: String?,
    sourceUrl: String?,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(text = "LINKS")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (storeUrl != null) {
                WorksStoreButton(url = storeUrl, onClickUrl = onClickUrl, modifier = Modifier.weight(1f))
            }
            if (sourceUrl != null) {
                WorksSourceButton(url = sourceUrl, onClickUrl = onClickUrl, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
private fun WorksDetailSheetOverlayPreview() {
    KeiTheme {
        Box(
            modifier = Modifier
                .size(
                    width = WorkbenchDimensions.WorksCardWidth,
                    height = WorkbenchDimensions.WorksCardHeight,
                )
                .background(KeiTheme.colors.cardBackground),
        ) {
            WorksDetailSheetOverlay(
                work = PreviewWorks.first(),
                visible = true,
                onDismiss = {},
                onClickUrl = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
