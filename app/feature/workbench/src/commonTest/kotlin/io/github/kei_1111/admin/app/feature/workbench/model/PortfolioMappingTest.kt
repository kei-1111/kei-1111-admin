package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.PinnedRepoSetting
import io.github.kei_1111.admin.shared.model.SocialLink
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LanguageShare
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.PinnedRepo
import io.github.kei_1111.admin.shared.model.portfolio.RepoLanguage
import io.github.kei_1111.admin.shared.model.portfolio.WorkTag
import kotlinx.collections.immutable.persistentListOf
import kotlin.test.Test
import kotlin.test.assertEquals

class PortfolioMappingTest {

    @Test
    fun mapsWorkFieldsAndFallsBackMissingEnglishValues() {
        val work = Work(
            id = "portfolio",
            name = "Portfolio",
            type = "Website",
            period = "2025–",
            about = "日本語の説明",
            aboutEn = "",
            techStack = listOf("Kotlin", "Ktor"),
            roles = listOf("設計", "実装"),
            rolesEn = listOf("Design", ""),
            screenshots = listOf("images/portfolio/hero.webp"),
        )

        val actual = work.toPortfolioWork()

        assertEquals("portfolio", actual.id)
        assertEquals("Portfolio", actual.name)
        assertEquals("Website", actual.kind)
        assertEquals("2025–", actual.period)
        assertEquals(LocalizedText(ja = "日本語の説明", en = "日本語の説明"), actual.description)
        assertEquals(
            persistentListOf(
                WorkTag(name = "Kotlin", accent = true),
                WorkTag(name = "Ktor", accent = false),
            ),
            actual.tags,
        )
        assertEquals(
            persistentListOf(
                LocalizedText(ja = "設計", en = "Design"),
                LocalizedText(ja = "実装", en = "実装"),
            ),
            actual.roles,
        )
        assertEquals(persistentListOf("images/portfolio/hero.webp"), actual.screenshots)
        assertEquals(null, actual.iconUrl)
        assertEquals(null, actual.storeUrl)
        assertEquals(null, actual.sourceUrl)
    }

    @Test
    fun mapsEmptyWorkCollectionsAndPresentOptionalUrls() {
        val work = Work(
            id = "app",
            name = "App",
            about = "About",
            aboutEn = "About in English",
            iconUrl = "images/app/icon.webp",
            googlePlayUrl = "https://play.google.com/store/apps/details?id=app",
            sourceUrl = "https://github.com/kei-1111/app",
        )

        val actual = work.toPortfolioWork()

        assertEquals(persistentListOf(), actual.tags)
        assertEquals(persistentListOf(), actual.roles)
        assertEquals(persistentListOf(), actual.screenshots)
        assertEquals("images/app/icon.webp", actual.iconUrl)
        assertEquals("https://play.google.com/store/apps/details?id=app", actual.storeUrl)
        assertEquals("https://github.com/kei-1111/app", actual.sourceUrl)
    }

    @Test
    fun overlayPreservesStatisticsFiltersReposAndMapsSupportedLinks() {
        val base = baseProfile()
        val profile = AdminProfile(
            displayName = "けい",
            displayNameEn = "",
            role = "Android Developer",
            location = "Tokyo",
            xUrl = "https://x.com/kei_1111",
            avatarUrl = "",
            pinnedRepos = listOf(PinnedRepoSetting(name = "hidden", visible = false)),
            socialLinks = listOf(
                SocialLink(service = " GitHub ", url = "https://github.com/kei-1111"),
                SocialLink(service = "LinkedIn", url = "https://linkedin.com/in/kei-1111"),
            ),
        )

        val actual = profile.overlayOn(base)

        assertEquals(LocalizedText(ja = "けい", en = "けい"), actual.name)
        assertEquals(base.iconUrl, actual.iconUrl)
        assertEquals("Android Developer", actual.role)
        assertEquals("Tokyo", actual.location)
        assertEquals(base.followers, actual.followers)
        assertEquals(base.following, actual.following)
        assertEquals(base.repos, actual.repos)
        assertEquals(base.totalStars, actual.totalStars)
        assertEquals(persistentListOf(base.pinnedRepos.first()), actual.pinnedRepos)
        assertEquals(
            persistentListOf(
                LinkService(
                    type = LinkServiceType.GitHub,
                    name = " GitHub ",
                    url = "https://github.com/kei-1111",
                ),
                LinkService(type = LinkServiceType.X, name = "X", url = "https://x.com/kei_1111"),
            ),
            actual.links,
        )
    }

    @Test
    fun overlayUsesExplicitAvatarAndDoesNotDuplicateLegacyXLink() {
        val profile = AdminProfile(
            displayName = "Kei",
            displayNameEn = "Kei",
            xUrl = "https://x.com/legacy",
            avatarUrl = "images/profile/avatar.webp",
            socialLinks = listOf(SocialLink(service = "twitter", url = "https://x.com/current")),
        )

        val actual = profile.overlayOn(baseProfile())

        assertEquals("images/profile/avatar.webp", actual.iconUrl)
        assertEquals(
            persistentListOf(LinkService(type = LinkServiceType.X, name = "twitter", url = "https://x.com/current")),
            actual.links,
        )
    }

    @Test
    fun emptyProfileKeepsBaseAvatarAndReposButProducesNoLinks() {
        val base = baseProfile()

        val actual = AdminProfile().overlayOn(base)

        assertEquals(LocalizedText(ja = "", en = ""), actual.name)
        assertEquals(base.iconUrl, actual.iconUrl)
        assertEquals(base.pinnedRepos, actual.pinnedRepos)
        assertEquals(persistentListOf(), actual.links)
    }
}

private fun baseProfile() = GitHubProfile(
    name = LocalizedText(ja = "Base", en = "Base"),
    handle = "kei-1111",
    iconUrl = "https://example.com/avatar.png",
    location = "Japan",
    role = "Developer",
    followers = 10,
    following = 20,
    repos = 30,
    totalStars = 40,
    pinnedRepos = persistentListOf(
        PinnedRepo(
            name = "visible",
            description = LocalizedText(ja = "表示", en = "Visible"),
            url = "https://example.com/visible",
            language = RepoLanguage.Kotlin,
        ),
        PinnedRepo(
            name = "hidden",
            description = LocalizedText(ja = "非表示", en = "Hidden"),
            url = "https://example.com/hidden",
        ),
    ),
    languages = persistentListOf(LanguageShare(language = RepoLanguage.Kotlin, share = 1f)),
    links = persistentListOf(LinkService(type = LinkServiceType.GitHub, name = "Old", url = "https://example.com/old")),
)
