@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.preview

import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.ContributionDay
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LanguageShare
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.PinnedRepo
import io.github.kei_1111.admin.shared.model.portfolio.RepoLanguage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** 本体サイトの ProfilePreviewFixtures から Preview カード用に必要な 2 つだけを移植。 */
internal val PreviewGitHubProfile = GitHubProfile(
    name = LocalizedText(ja = "けい", en = "Kei"),
    handle = "kei-1111",
    location = "Japan",
    role = "Android developer",
    followers = 15,
    following = 25,
    repos = 32,
    totalStars = 41,
    pinnedRepos = persistentListOf(
        PinnedRepo(
            name = "kei-1111.github.io",
            description = LocalizedText(ja = "自己紹介Webサイトのリポジトリ", en = "My portfolio website repository"),
            url = "https://github.com/kei-1111/kei-1111.github.io",
            language = RepoLanguage.Kotlin,
        ),
        PinnedRepo(
            name = "android-template",
            description = LocalizedText(ja = "My Android Template Project", en = "My Android Template Project"),
            url = "https://github.com/kei-1111/android-template",
            stars = 2,
        ),
        PinnedRepo(
            name = "kmp-sample-library",
            description = LocalizedText(ja = "KMP Library のサンプルリポジトリ", en = "Sample repository for a KMP library"),
            url = "https://github.com/kei-1111/kmp-sample-library",
            language = RepoLanguage.Kotlin,
        ),
        PinnedRepo(
            name = "kmp-sample-ios",
            description = LocalizedText(ja = "KMPライブラリを使うiOSアプリ", en = "iOS app using the KMP library"),
            url = "https://github.com/kei-1111/kmp-sample-ios",
            language = RepoLanguage.Swift,
        ),
    ),
    languages = persistentListOf(
        LanguageShare(language = RepoLanguage.Kotlin, share = 0.78f),
        LanguageShare(language = RepoLanguage.Swift, share = 0.12f),
        LanguageShare(language = RepoLanguage.Shell, share = 0.10f),
    ),
    links = persistentListOf(
        LinkService(type = LinkServiceType.GitHub, name = "GitHub", url = "https://github.com/kei-1111"),
        LinkService(type = LinkServiceType.X, name = "X", url = "https://x.com/kei_1111_"),
        LinkService(type = LinkServiceType.Qiita, name = "Qiita", url = "https://qiita.com/kei-1111"),
        LinkService(type = LinkServiceType.Note, name = "note", url = "https://note.com/kei_1111_"),
    ),
)

internal val PreviewContributionCalendar = ContributionCalendar(
    totalLastYear = 620,
    days = List(53 * 7) { index ->
        val level = index % 5
        ContributionDay(
            date = "2026-01-${(index % 28 + 1).toString().padStart(2, '0')}",
            count = level * 3,
            level = level,
        )
    }.toImmutableList(),
)
