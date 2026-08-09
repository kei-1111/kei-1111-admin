package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.WorksContent
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

/**
 * 管理サーバーのコンテンツ API。読み書きとも suspend(失敗は例外) — 呼び出し側の
 * ViewModel が `recoverOrElse` で境界処理する(`.claude/rules/error-handling.md`)。
 */
interface AdminContentRepository {
    suspend fun fetchWorksDraft(): WorksContent
    suspend fun saveWorksDraft(content: WorksContent): WorksContent
    suspend fun fetchProfileDraft(): AdminProfile
    suspend fun saveProfileDraft(profile: AdminProfile): AdminProfile
    suspend fun fetchMeta(): ContentMeta
    suspend fun publish(): ContentMeta

    /** Preview カード用: 本体サイト API のプロキシから GitHub 由来データを取り寄せる。 */
    suspend fun fetchPortfolioProfile(): GitHubProfile
    suspend fun fetchPortfolioContributions(): ContributionCalendar

    /** 作品スクリーンショットをアップロードし、配信パス(images/works/...)を返す。 */
    suspend fun uploadWorkImage(workId: String, fileName: String, mimeType: String, bytes: ByteArray): String
}
