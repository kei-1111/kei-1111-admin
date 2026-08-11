package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.ContributionDay
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssue
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssues
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.ktor.http.HttpMethod
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdminPreviewRepositoryImplTest {

    @Test
    fun fetchPortfolioProfileUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPreviewRepositoryImpl(
            testHttpClient(
                responseBody = PROFILE_RESPONSE,
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/preview/profile",
            ),
        )

        val actual = repository.fetchPortfolioProfile()

        assertEquals(portfolioProfile(), actual)
    }

    @Test
    fun fetchPortfolioContributionsUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPreviewRepositoryImpl(
            testHttpClient(
                responseBody = """{"totalLastYear":7,"days":[{"date":"2026-08-10","count":2,"level":1}]}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/preview/contributions",
            ),
        )

        val actual = repository.fetchPortfolioContributions()

        assertEquals(
            ContributionCalendar(
                totalLastYear = 7,
                days = persistentListOf(ContributionDay(date = "2026-08-10", count = 2, level = 1)),
            ),
            actual,
        )
    }

    @Test
    fun fetchPortfolioIssuesUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPreviewRepositoryImpl(
            testHttpClient(
                responseBody = """{"totalCount":1,"issues":[{"number":42,"title":"Test issue","url":"https://example.com/42"}]}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/preview/issues",
            ),
        )

        val actual = repository.fetchPortfolioIssues()

        assertEquals(
            GitHubIssues(
                totalCount = 1,
                issues = listOf(GitHubIssue(number = 42, title = "Test issue", url = "https://example.com/42")),
            ),
            actual,
        )
    }
}

private fun portfolioProfile() = GitHubProfile(
    name = LocalizedText(ja = "けい", en = "Kei"),
    handle = "kei-1111",
    location = "Japan",
    role = "Developer",
    followers = 1,
    following = 2,
    repos = 3,
    totalStars = 4,
    pinnedRepos = persistentListOf(),
    languages = persistentListOf(),
    links = persistentListOf(),
)

private val PROFILE_RESPONSE =
    """
    {
      "name": { "ja": "けい", "en": "Kei" },
      "handle": "kei-1111",
      "location": "Japan",
      "role": "Developer",
      "followers": 1,
      "following": 2,
      "repos": 3,
      "totalStars": 4,
      "pinnedRepos": [],
      "languages": [],
      "links": []
    }
    """.trimIndent()
