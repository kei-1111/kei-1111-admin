package io.github.kei_1111.admin.app.core.domain.usecase

import io.github.kei_1111.admin.app.core.data.repository.AdminContentRepository
import io.github.kei_1111.admin.app.core.data.repository.AdminPreviewRepository
import io.github.kei_1111.admin.app.core.data.repository.AdminPublishRepository
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssues
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ContentUseCasesTest {

    @Test
    fun getWorksDraftDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = WorksContent()
        val repository = FakeAdminContentRepository(works = expected)

        val actual = GetWorksDraftUseCaseImpl(repository)()

        assertEquals(listOf("fetchWorksDraft"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun saveWorksDraftPassesArgumentAndReturnsRepositoryValue() = runTest {
        val input = WorksContent()
        val expected = WorksContent(works = emptyList())
        val repository = FakeAdminContentRepository(savedWorks = expected)

        val actual = SaveWorksDraftUseCaseImpl(repository)(input)

        assertEquals(listOf("saveWorksDraft"), repository.calls)
        assertSame(input, repository.receivedWorks)
        assertSame(expected, actual)
    }

    @Test
    fun getProfileDraftDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = AdminProfile(displayName = "Kei")
        val repository = FakeAdminContentRepository(profile = expected)

        val actual = GetProfileDraftUseCaseImpl(repository)()

        assertEquals(listOf("fetchProfileDraft"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun saveProfileDraftPassesArgumentAndReturnsRepositoryValue() = runTest {
        val input = AdminProfile(displayName = "Draft")
        val expected = AdminProfile(displayName = "Saved")
        val repository = FakeAdminContentRepository(savedProfile = expected)

        val actual = SaveProfileDraftUseCaseImpl(repository)(input)

        assertEquals(listOf("saveProfileDraft"), repository.calls)
        assertSame(input, repository.receivedProfile)
        assertSame(expected, actual)
    }

    @Test
    fun getTerminalDraftDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = TerminalCommandsContent()
        val repository = FakeAdminContentRepository(terminal = expected)

        val actual = GetTerminalDraftUseCaseImpl(repository)()

        assertEquals(listOf("fetchTerminalDraft"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun saveTerminalDraftPassesArgumentAndReturnsRepositoryValue() = runTest {
        val input = TerminalCommandsContent()
        val expected = TerminalCommandsContent(commands = emptyList())
        val repository = FakeAdminContentRepository(savedTerminal = expected)

        val actual = SaveTerminalDraftUseCaseImpl(repository)(input)

        assertEquals(listOf("saveTerminalDraft"), repository.calls)
        assertSame(input, repository.receivedTerminal)
        assertSame(expected, actual)
    }

    @Test
    fun getReadmeDraftDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = ReadmeContent()
        val repository = FakeAdminContentRepository(readme = expected)

        val actual = GetReadmeDraftUseCaseImpl(repository)()

        assertEquals(listOf("fetchReadmeDraft"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun saveReadmeDraftPassesArgumentAndReturnsRepositoryValue() = runTest {
        val input = ReadmeContent()
        val expected = ReadmeContent(ja = emptyList())
        val repository = FakeAdminContentRepository(savedReadme = expected)

        val actual = SaveReadmeDraftUseCaseImpl(repository)(input)

        assertEquals(listOf("saveReadmeDraft"), repository.calls)
        assertSame(input, repository.receivedReadme)
        assertSame(expected, actual)
    }

    @Test
    fun getContentMetaDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = ContentMeta(lastPublishedAt = "published")
        val repository = FakeAdminPublishRepository(meta = expected)

        val actual = GetContentMetaUseCaseImpl(repository)()

        assertEquals(listOf("fetchMeta"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun publishContentDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = ContentMeta(lastPublishedAt = "published")
        val repository = FakeAdminPublishRepository(publishedMeta = expected)

        val actual = PublishContentUseCaseImpl(repository)()

        assertEquals(listOf("publish"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun getPublishedSnapshotDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = PublishedSnapshot(profile = AdminProfile(displayName = "Published"))
        val repository = FakeAdminPublishRepository(snapshot = expected)

        val actual = GetPublishedSnapshotUseCaseImpl(repository)()

        assertEquals(listOf("fetchPublishedSnapshot"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun discardDraftPassesArgumentToRepository() = runTest {
        val repository = FakeAdminPublishRepository()

        DiscardDraftUseCaseImpl(repository)(ContentDocument.Readme)

        assertEquals(listOf("discardDraft"), repository.calls)
        assertEquals(ContentDocument.Readme, repository.receivedDocument)
    }

    @Test
    fun getPortfolioProfileDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = portfolioProfile()
        val repository = FakeAdminPreviewRepository(profile = expected)

        val actual = GetPortfolioProfileUseCaseImpl(repository)()

        assertEquals(listOf("fetchPortfolioProfile"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun getPortfolioContributionsDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = ContributionCalendar(totalLastYear = 0, days = persistentListOf())
        val repository = FakeAdminPreviewRepository(contributions = expected)

        val actual = GetPortfolioContributionsUseCaseImpl(repository)()

        assertEquals(listOf("fetchPortfolioContributions"), repository.calls)
        assertSame(expected, actual)
    }

    @Test
    fun getPortfolioIssuesDelegatesAndReturnsRepositoryValue() = runTest {
        val expected = GitHubIssues(totalCount = 0)
        val repository = FakeAdminPreviewRepository(issues = expected)

        val actual = GetPortfolioIssuesUseCaseImpl(repository)()

        assertEquals(listOf("fetchPortfolioIssues"), repository.calls)
        assertSame(expected, actual)
    }
}

private class FakeAdminContentRepository(
    private val works: WorksContent = WorksContent(),
    private val savedWorks: WorksContent = WorksContent(),
    private val profile: AdminProfile = AdminProfile(),
    private val savedProfile: AdminProfile = AdminProfile(),
    private val terminal: TerminalCommandsContent = TerminalCommandsContent(),
    private val savedTerminal: TerminalCommandsContent = TerminalCommandsContent(),
    private val readme: ReadmeContent = ReadmeContent(),
    private val savedReadme: ReadmeContent = ReadmeContent(),
) : AdminContentRepository {
    val calls = mutableListOf<String>()
    var receivedWorks: WorksContent? = null
    var receivedProfile: AdminProfile? = null
    var receivedTerminal: TerminalCommandsContent? = null
    var receivedReadme: ReadmeContent? = null

    override suspend fun fetchWorksDraft(): WorksContent {
        calls += "fetchWorksDraft"
        return works
    }

    override suspend fun saveWorksDraft(content: WorksContent): WorksContent {
        calls += "saveWorksDraft"
        receivedWorks = content
        return savedWorks
    }

    override suspend fun fetchProfileDraft(): AdminProfile {
        calls += "fetchProfileDraft"
        return profile
    }

    override suspend fun saveProfileDraft(profile: AdminProfile): AdminProfile {
        calls += "saveProfileDraft"
        receivedProfile = profile
        return savedProfile
    }

    override suspend fun fetchTerminalDraft(): TerminalCommandsContent {
        calls += "fetchTerminalDraft"
        return terminal
    }

    override suspend fun saveTerminalDraft(content: TerminalCommandsContent): TerminalCommandsContent {
        calls += "saveTerminalDraft"
        receivedTerminal = content
        return savedTerminal
    }

    override suspend fun fetchReadmeDraft(): ReadmeContent {
        calls += "fetchReadmeDraft"
        return readme
    }

    override suspend fun saveReadmeDraft(content: ReadmeContent): ReadmeContent {
        calls += "saveReadmeDraft"
        receivedReadme = content
        return savedReadme
    }
}

private class FakeAdminPublishRepository(
    private val meta: ContentMeta = ContentMeta(),
    private val publishedMeta: ContentMeta = ContentMeta(),
    private val snapshot: PublishedSnapshot = PublishedSnapshot(),
) : AdminPublishRepository {
    val calls = mutableListOf<String>()
    var receivedDocument: ContentDocument? = null

    override suspend fun fetchMeta(): ContentMeta {
        calls += "fetchMeta"
        return meta
    }

    override suspend fun publish(): ContentMeta {
        calls += "publish"
        return publishedMeta
    }

    override suspend fun fetchPublishedSnapshot(): PublishedSnapshot {
        calls += "fetchPublishedSnapshot"
        return snapshot
    }

    override suspend fun discardDraft(document: ContentDocument) {
        calls += "discardDraft"
        receivedDocument = document
    }
}

private class FakeAdminPreviewRepository(
    private val profile: GitHubProfile = portfolioProfile(),
    private val contributions: ContributionCalendar = ContributionCalendar(0, persistentListOf()),
    private val issues: GitHubIssues = GitHubIssues(0),
) : AdminPreviewRepository {
    val calls = mutableListOf<String>()

    override suspend fun fetchPortfolioProfile(): GitHubProfile {
        calls += "fetchPortfolioProfile"
        return profile
    }

    override suspend fun fetchPortfolioContributions(): ContributionCalendar {
        calls += "fetchPortfolioContributions"
        return contributions
    }

    override suspend fun fetchPortfolioIssues(): GitHubIssues {
        calls += "fetchPortfolioIssues"
        return issues
    }
}

private fun portfolioProfile() = GitHubProfile(
    name = LocalizedText(ja = "けい", en = "Kei"),
    handle = "kei-1111",
    location = "Japan",
    role = "Developer",
    followers = 0,
    following = 0,
    repos = 0,
    totalStars = 0,
    pinnedRepos = persistentListOf(),
    languages = persistentListOf(),
    links = persistentListOf(),
)
