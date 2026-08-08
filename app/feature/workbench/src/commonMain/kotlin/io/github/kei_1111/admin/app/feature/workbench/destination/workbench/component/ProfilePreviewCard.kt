@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.ProfileIconImage
import io.github.kei_1111.admin.shared.model.AdminProfile
import org.jetbrains.compose.resources.painterResource

/**
 * Profile 編集の Preview。本体サイトの GitHubPreviewCard(280x600 の縦長カード)の
 * 見た目を踏襲し、編集中の [AdminProfile] を描画する。GitHub 由来の統計・Contributions は
 * 本体サーバーが取得するため、ここでは自動取得である旨の注記に留める。
 */
@Composable
internal fun ProfilePreviewCard(
    profile: AdminProfile,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground)
            .border(1.dp, colors.outline)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
    ) {
        CardHeader(profile = profile)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "followers · repos · contributions は GitHub から自動取得",
            style = KeiTheme.typography.githubJp.copy(fontSize = 8.sp, color = colors.muted),
        )
        Spacer(modifier = Modifier.height(14.dp))
        PinnedSection(profile = profile)
        Spacer(modifier = Modifier.height(14.dp))
        LinksSection(profile = profile)
    }
}

@Composable
private fun CardHeader(
    profile: AdminProfile,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(ProfileIconImage),
            contentDescription = profile.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.dp, colors.outline, CircleShape),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = profile.displayName,
                style = KeiTheme.typography.githubJp.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                text = "@${profile.gitHubLogin} · ${profile.location}",
                style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = colors.textSecondary),
            )
            Text(
                text = profile.role,
                style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = colors.androidGreen),
            )
        }
    }
}

/** 見出しラベル(本体カードの SectionLabel 様式: 小サイズ mono + letter-spacing)。 */
@Composable
private fun CardSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = KeiTheme.typography.chrome
            .copy(fontSize = 8.sp, color = KeiTheme.colors.textSecondary, letterSpacing = 1.1.sp),
    )
}

@Composable
private fun PinnedSection(
    profile: AdminProfile,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CardSectionLabel(text = "PINNED")
        profile.pinnedRepos.filter { it.visible }.forEach { repo ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(KeiTheme.shapes.githubItem)
                    .background(colors.gitHubItem)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = repo.name,
                    style = KeiTheme.typography.chrome.copy(fontSize = 9.sp, color = colors.syntaxLink),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = repo.description,
                    style = KeiTheme.typography.githubJp.copy(fontSize = 8.sp, color = colors.textSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LinksSection(
    profile: AdminProfile,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val links = buildList {
        addAll(profile.socialLinks.map { it.service to it.url })
        if (profile.xUrl.isNotEmpty()) add("X" to profile.xUrl)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CardSectionLabel(text = "LINKS")
        links.filter { it.first.isNotEmpty() }.forEach { (service, url) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(KeiTheme.shapes.linkTile)
                    .background(colors.gitHubItem)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = service,
                    style = KeiTheme.typography.githubJp.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                    ),
                )
                Text(
                    text = url,
                    style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = colors.muted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
