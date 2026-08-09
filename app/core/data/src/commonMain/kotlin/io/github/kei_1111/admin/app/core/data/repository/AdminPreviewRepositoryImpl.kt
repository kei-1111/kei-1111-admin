package io.github.kei_1111.admin.app.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssues
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
internal class AdminPreviewRepositoryImpl(
    private val httpClient: HttpClient,
) : AdminPreviewRepository {

    override suspend fun fetchPortfolioProfile(): GitHubProfile =
        httpClient.get("/api/preview/profile") { authorize() }.body()

    override suspend fun fetchPortfolioContributions(): ContributionCalendar =
        httpClient.get("/api/preview/contributions") { authorize() }.body()

    override suspend fun fetchPortfolioIssues(): GitHubIssues =
        httpClient.get("/api/preview/issues") { authorize() }.body()
}
