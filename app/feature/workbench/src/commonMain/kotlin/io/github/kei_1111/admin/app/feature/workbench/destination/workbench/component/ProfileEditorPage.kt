@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.KeiTextField
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.SocialLink

@Composable
internal fun ProfileEditorPage(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val profile = state.profile
    val update: (AdminProfile) -> Unit = { onIntent(WorkbenchIntent.UpdateProfileDraft(it)) }

    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        ProfileForm(
            profile = profile,
            update = update,
            modifier = Modifier
                .weight(WorkbenchDimensions.FormWeight)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = WorkbenchDimensions.IslandGap),
        )
        PreviewPane(
            componentName = "ProfilePreview",
            contentFingerprint = profile,
            modifier = Modifier
                .width(WorkbenchDimensions.PreviewWidth)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun ProfileForm(
    profile: AdminProfile,
    update: (AdminProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(WorkbenchDimensions.SectionGap),
    ) {
        GitHubSyncCard(profile = profile)
        IdentitySection(profile = profile, update = update)
        LocationSection(profile = profile, update = update)
        PinnedReposSection(profile = profile, update = update)
        SocialLinksSection(profile = profile, update = update)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

/** GitHub 由来データは編集不可(取得値の表示と Sync だけ)。 */
@Composable
private fun GitHubSyncCard(
    profile: AdminProfile,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KeiTheme.shapes.card)
            .background(colors.popup)
            .border(1.dp, colors.popupBorder, KeiTheme.shapes.card)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "GitHub 連携 — @${profile.gitHubLogin}",
                style = KeiTheme.typography.cardJp.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
            )
            Text(
                text = "followers / repos / contributions / pinned を自動取得",
                style = KeiTheme.typography.cardJp.copy(fontSize = 11.sp, color = colors.muted),
            )
        }
        if (profile.lastSyncedAt.isNotEmpty()) {
            Text(
                text = "✓ synced ${profile.lastSyncedAt}",
                style = KeiTheme.typography.chrome.copy(fontSize = 10.sp, color = colors.androidGreen),
            )
        }
        // Sync 実行はデータ層接続後に配線する
        PillButton(label = "⟳ Sync", onClick = {})
    }
}

@Composable
private fun IdentitySection(
    profile: AdminProfile,
    update: (AdminProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            SectionLabel(text = "AVATAR")
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(KeiTheme.colors.chip),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.displayName.take(1).ifEmpty { "?" },
                    style = KeiTheme.typography.cardJp.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = KeiTheme.colors.textPrimary,
                    ),
                )
            }
        }
        KeiTextField(
            label = "DISPLAY NAME",
            value = profile.displayName,
            onValueChange = { update(profile.copy(displayName = it)) },
            modifier = Modifier.weight(1f),
        )
        KeiTextField(
            label = "ROLE",
            value = profile.role,
            onValueChange = { update(profile.copy(role = it)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LocationSection(
    profile: AdminProfile,
    update: (AdminProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        KeiTextField(
            label = "LOCATION",
            value = profile.location,
            onValueChange = { update(profile.copy(location = it)) },
            modifier = Modifier.weight(1f),
        )
        KeiTextField(
            label = "X (TWITTER)",
            value = profile.xUrl,
            onValueChange = { update(profile.copy(xUrl = it)) },
            modifier = Modifier.weight(1f),
            mono = true,
            textColor = KeiTheme.colors.syntaxLink,
        )
    }
}

/** GitHub から取得した一覧。追加・編集は不可、表示 ON/OFF と並びのみ。 */
@Composable
private fun PinnedReposSection(
    profile: AdminProfile,
    update: (AdminProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(modifier = modifier) {
        SectionLabel(text = "PINNED REPOSITORIES")
        Column(
            modifier = Modifier.padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            profile.pinnedRepos.forEachIndexed { index, repo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(KeiTheme.shapes.row)
                        .background(colors.islandDark)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .alpha(if (repo.visible) 1f else 0.6f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = repo.name,
                        style = KeiTheme.typography.chrome.copy(
                            fontSize = 12.sp,
                            color = colors.syntaxLink,
                        ),
                    )
                    Text(
                        text = repo.description,
                        style = KeiTheme.typography.cardJp.copy(fontSize = 11.sp, color = colors.muted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    VisibilityToggle(
                        checked = repo.visible,
                        onCheckedChange = { visible ->
                            val updated = profile.pinnedRepos.toMutableList()
                                .apply { set(index, repo.copy(visible = visible)) }
                            update(profile.copy(pinnedRepos = updated))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun VisibilityToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Box(
        modifier = modifier
            .width(30.dp)
            .height(16.dp)
            .clip(KeiTheme.shapes.pill)
            .background(if (checked) colors.androidGreen else colors.chip)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (checked) colors.desk else colors.mutedHigh),
        )
    }
}

@Composable
private fun SocialLinksSection(
    profile: AdminProfile,
    update: (AdminProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionLabel(text = "SOCIAL LINKS")
        Column(
            modifier = Modifier.padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            profile.socialLinks.forEachIndexed { index, link ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KeiTextField(
                        label = "SERVICE",
                        value = link.service,
                        onValueChange = { value ->
                            val updated = profile.socialLinks.toMutableList()
                                .apply { set(index, link.copy(service = value)) }
                            update(profile.copy(socialLinks = updated))
                        },
                        modifier = Modifier.width(140.dp),
                    )
                    KeiTextField(
                        label = "URL",
                        value = link.url,
                        onValueChange = { value ->
                            val updated = profile.socialLinks.toMutableList()
                                .apply { set(index, link.copy(url = value)) }
                            update(profile.copy(socialLinks = updated))
                        },
                        modifier = Modifier.weight(1f),
                        mono = true,
                        textColor = KeiTheme.colors.syntaxLink,
                    )
                }
            }
            Text(
                text = "+ リンクを追加",
                style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = KeiTheme.colors.mutedHigh),
                modifier = Modifier
                    .clip(KeiTheme.shapes.chip)
                    .clickable { update(profile.copy(socialLinks = profile.socialLinks + SocialLink("", ""))) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}
