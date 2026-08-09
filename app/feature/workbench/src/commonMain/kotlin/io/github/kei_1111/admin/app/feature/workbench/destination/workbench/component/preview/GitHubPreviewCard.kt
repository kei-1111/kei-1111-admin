@file:Suppress("MagicNumber", "TooManyFunctions")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiColorScheme
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ProfileIconImage
import io.github.kei_1111.admin.app.core.designsystem.theme.brandColor
import io.github.kei_1111.admin.app.core.designsystem.theme.icon
import io.github.kei_1111.admin.app.core.ui.rememberHoverState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.forLanguage
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewContributionCalendar
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewGitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LanguageShare
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.PinnedRepo
import io.github.kei_1111.admin.shared.model.portfolio.RepoLanguage
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

/**
 * GitHub プロフィール型の縦長プレビューカード（280x600）。
 * Contributions は ViewModel から渡された結果をそのまま描画する（取得中/失敗時は null）。
 */
@Composable
internal fun GitHubPreviewCard(
    profile: GitHubProfile,
    contributions: ContributionCalendar?,
    contributionsFailed: Boolean,
    onClickUrl: (String) -> Unit,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(WorkbenchDimensions.GitHubCardWidth)
            .height(WorkbenchDimensions.GitHubCardHeight)
            .background(KeiTheme.colors.cardBackground)
            .border(1.dp, KeiTheme.colors.outline),
    ) {
        CardHeader(
            profile = profile,
            modifier = Modifier.padding(
                start = WorkbenchDimensions.GitHubCardPadding,
                top = 22.dp,
                end = WorkbenchDimensions.GitHubCardPadding,
                bottom = 12.dp,
            ),
        )
        StatsRow(profile = profile, modifier = Modifier.padding(horizontal = WorkbenchDimensions.GitHubCardPadding))
        Spacer(modifier = Modifier.height(WorkbenchDimensions.GitHubCardSectionGap))
        ContributionsSection(
            calendar = contributions,
            failed = contributionsFailed,
            onClickRetry = onClickRetry,
            modifier = Modifier.padding(horizontal = WorkbenchDimensions.GitHubCardPadding),
        )
        Spacer(modifier = Modifier.height(WorkbenchDimensions.GitHubCardSectionGap))
        PinnedSection(
            repos = profile.pinnedRepos,
            onClickUrl = onClickUrl,
            modifier = Modifier.padding(horizontal = WorkbenchDimensions.GitHubCardPadding),
        )
        Spacer(modifier = Modifier.height(WorkbenchDimensions.GitHubCardSectionGap))
        LanguagesSection(
            languages = profile.languages,
            modifier = Modifier.padding(horizontal = WorkbenchDimensions.GitHubCardPadding),
        )
        Spacer(modifier = Modifier.weight(1f))
        LinksSection(
            links = profile.links,
            onClickUrl = onClickUrl,
            modifier = Modifier.padding(
                start = WorkbenchDimensions.GitHubCardPadding,
                end = WorkbenchDimensions.GitHubCardPadding,
                bottom = WorkbenchDimensions.GitHubCardPadding,
            ),
        )
    }
}

/** 見出しラベル（8px・letter-spacing 0.14em・mono）。 */
@Composable
internal fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = KeiTheme.colors.textSecondary).copy(letterSpacing = 1.1.sp),
    )
}

@Composable
private fun CardHeader(
    profile: GitHubProfile,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAvatar(profile = profile)
        Spacer(modifier = Modifier.width(12.dp))
        ProfileIdentity(profile = profile)
    }
}

@Composable
private fun ProfileAvatar(
    profile: GitHubProfile,
    modifier: Modifier = Modifier,
) {
    val language = KeiLanguageController.language
    Image(
        painter = painterResource(ProfileIconImage),
        contentDescription = profile.name.forLanguage(language),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(1.dp, KeiTheme.colors.outline, CircleShape),
    )
}

@Composable
private fun ProfileIdentity(
    profile: GitHubProfile,
    modifier: Modifier = Modifier,
) {
    val language = KeiLanguageController.language
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = profile.name.forLanguage(language),
            style = KeiTheme.typography.githubJp.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
        )
        Text(
            text = "@${profile.handle} · ${profile.location}",
            style = KeiTheme.typography.chrome.copy(fontSize = 9.sp, color = KeiTheme.colors.textSecondary),
        )
        Text(
            text = profile.role,
            style = KeiTheme.typography.chrome.copy(fontSize = 9.sp, color = KeiTheme.colors.androidGreen),
        )
    }
}

@Composable
private fun StatsRow(
    profile: GitHubProfile,
    modifier: Modifier = Modifier,
) {
    val numberStyle = SpanStyle(color = KeiTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
    Text(
        text = buildAnnotatedString {
            withStyle(numberStyle) { append("${profile.followers}") }
            append(" followers · ")
            withStyle(numberStyle) { append("${profile.following}") }
            append(" following · ")
            withStyle(numberStyle) { append("${profile.repos}") }
            append(" repos · ★ ")
            withStyle(numberStyle) { append("${profile.totalStars}") }
        },
        modifier = modifier,
        style = KeiTheme.typography.chrome.copy(fontSize = 9.sp, color = KeiTheme.colors.textSecondary),
    )
}

@Composable
private fun PinnedSection(
    repos: ImmutableList<PinnedRepo>,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SectionLabel(text = "PINNED")
        repos.forEach { repo ->
            PinnedRepoRow(repo = repo, onClickUrl = onClickUrl)
        }
    }
}

@Composable
private fun PinnedRepoRow(
    repo: PinnedRepo,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hoverState = rememberHoverState()
    val background = if (hoverState.hovered) KeiTheme.colors.gitHubItemHover else KeiTheme.colors.gitHubItem
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KeiTheme.shapes.githubItem)
            .background(background)
            .hoverable(hoverState.interactionSource)
            .clickable { onClickUrl(repo.url) }
            .padding(horizontal = 11.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PinnedRepoInfo(repo = repo, modifier = Modifier.weight(1f))
        repo.language?.let { language ->
            LanguageBadge(language = language)
        }
        repo.stars?.let { stars ->
            RepoStars(stars = stars)
        }
    }
}

@Composable
private fun PinnedRepoInfo(
    repo: PinnedRepo,
    modifier: Modifier = Modifier,
) {
    val language = KeiLanguageController.language
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = repo.name,
            style = KeiTheme.typography.chrome.copy(fontSize = 10.sp, color = KeiTheme.colors.syntaxLink),
        )
        Text(
            text = repo.description.forLanguage(language),
            style = KeiTheme.typography.githubJp.copy(fontSize = 8.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

@Composable
private fun RepoStars(
    stars: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "★ $stars",
        modifier = modifier,
        style = KeiTheme.typography.chrome.copy(fontSize = 9.sp, color = KeiTheme.colors.textSecondary),
    )
}

@Composable
private fun LanguageBadge(
    language: RepoLanguage,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(language.dotColor(KeiTheme.colors)),
        )
        Text(
            text = language.displayName,
            style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

@Composable
private fun LanguagesSection(
    languages: ImmutableList<LanguageShare>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        SectionLabel(text = "LANGUAGES")
        LanguageShareBar(languages = languages)
        LanguageShareLabels(languages = languages)
    }
}

@Composable
private fun LanguageShareBar(
    languages: ImmutableList<LanguageShare>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
    ) {
        languages.forEach { entry ->
            Box(
                modifier = Modifier
                    .weight(entry.share)
                    .fillMaxHeight()
                    .background(entry.language.dotColor(KeiTheme.colors)),
            )
        }
    }
}

@Composable
private fun LanguageShareLabels(
    languages: ImmutableList<LanguageShare>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        languages.forEach { entry ->
            LanguageShareLabel(entry = entry)
        }
    }
}

@Composable
private fun LanguageShareLabel(
    entry: LanguageShare,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(entry.language.dotColor(KeiTheme.colors)),
        )
        Text(
            text = "${entry.language.displayName} ${(entry.share * 100).roundToInt()}%",
            style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

@Composable
private fun LinksSection(
    links: ImmutableList<LinkService>,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SectionLabel(text = "LINKS")
        LinkGrid(links = links, onClickUrl = onClickUrl)
    }
}

@Composable
private fun LinkGrid(
    links: ImmutableList<LinkService>,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        links.chunked(2).forEach { rowLinks ->
            LinkRow(links = rowLinks, onClickUrl = onClickUrl)
        }
    }
}

@Composable
private fun LinkRow(
    links: List<LinkService>,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        links.forEach { link ->
            LinkTile(link = link, onClickUrl = onClickUrl, modifier = Modifier.weight(1f))
        }
        if (links.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LinkTile(
    link: LinkService,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hoverState = rememberHoverState()
    val focused by hoverState.interactionSource.collectIsFocusedAsState()
    val brandColor = link.type.brandColor(KeiTheme.colors)
    val borderColor = if (hoverState.hovered || focused) brandColor else Color.Transparent
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.linkTile)
            .background(KeiTheme.colors.gitHubItem)
            .border(1.dp, borderColor, KeiTheme.shapes.linkTile)
            .hoverable(hoverState.interactionSource)
            .clickable(interactionSource = hoverState.interactionSource, indication = null) { onClickUrl(link.url) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Icon(
            painter = painterResource(link.type.icon(KeiTheme.colors)),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = brandColor,
        )
        Text(
            text = link.name,
            style = KeiTheme.typography.githubJp.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        )
    }
}

private fun RepoLanguage.dotColor(colors: KeiColorScheme): Color = when (this) {
    RepoLanguage.Kotlin -> colors.langKotlin
    RepoLanguage.Swift -> colors.langSwift
    RepoLanguage.Shell -> colors.langShell
}

@Preview
@Composable
private fun GitHubPreviewCardPreview() {
    KeiTheme {
        Box(modifier = Modifier.background(KeiTheme.colors.desk).padding(8.dp)) {
            GitHubPreviewCard(
                profile = PreviewGitHubProfile,
                contributions = PreviewContributionCalendar,
                contributionsFailed = false,
                onClickUrl = {},
                onClickRetry = {},
            )
        }
    }
}
