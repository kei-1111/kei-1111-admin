package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

/** Preview カード用に本体サイトの実データを読む(/api/preview プロキシ経由)。 */
interface AdminPreviewRepository {
    suspend fun fetchPortfolioProfile(): GitHubProfile
    suspend fun fetchPortfolioContributions(): ContributionCalendar
}
