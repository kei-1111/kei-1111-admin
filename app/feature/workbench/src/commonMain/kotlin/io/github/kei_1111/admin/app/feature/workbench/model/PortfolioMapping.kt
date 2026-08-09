package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.WorkTag
import kotlinx.collections.immutable.toImmutableList

/**
 * 編集モデル → 本体サイトの JSON 契約モデルへの変換。Preview カード(本家実装の移植)は
 * 契約モデルを描画するため、編集内容をここで重ねる。
 */
internal fun Work.toPortfolioWork(): io.github.kei_1111.admin.shared.model.portfolio.Work =
    io.github.kei_1111.admin.shared.model.portfolio.Work(
        id = id,
        name = name,
        kind = type,
        period = period,
        description = LocalizedText(ja = about, en = aboutEn.ifBlank { about }),
        tags = techStack
            .map { WorkTag(name = it, accent = techChipKindOf(it) == TechChipKind.LanguageOrUi) }
            .toImmutableList(),
        roles = roles.mapIndexed { index, ja ->
            LocalizedText(ja = ja, en = rolesEn.getOrNull(index)?.ifBlank { ja } ?: ja)
        }.toImmutableList(),
        iconUrl = null,
        screenshots = screenshots.toImmutableList(),
        storeUrl = googlePlayUrl.ifEmpty { null },
        sourceUrl = sourceUrl.ifEmpty { null },
    )

/** GitHub 由来の統計は [base] から引き継ぎ、編集可能なフィールドだけを上書きする。 */
internal fun AdminProfile.overlayOn(base: GitHubProfile): GitHubProfile = base.copy(
    name = LocalizedText(ja = displayName, en = displayNameEn.ifBlank { displayName }),
    iconUrl = avatarUrl.takeIf { it.isNotBlank() } ?: base.iconUrl,
    role = role,
    location = location,
    pinnedRepos = base.pinnedRepos
        .filter { repo -> pinnedRepos.none { it.name == repo.name && !it.visible } }
        .toImmutableList(),
    links = buildList {
        socialLinks.forEach { link ->
            linkServiceTypeOf(link.service)?.let { type ->
                add(LinkService(type = type, name = link.service, url = link.url))
            }
        }
        if (xUrl.isNotEmpty() && socialLinks.none { linkServiceTypeOf(it.service) == LinkServiceType.X }) {
            add(LinkService(type = LinkServiceType.X, name = "X", url = xUrl))
        }
    }.toImmutableList(),
)

private fun linkServiceTypeOf(service: String): LinkServiceType? = when (service.trim().lowercase()) {
    "github" -> LinkServiceType.GitHub
    "x", "twitter" -> LinkServiceType.X
    "qiita" -> LinkServiceType.Qiita
    "note" -> LinkServiceType.Note
    else -> null
}
