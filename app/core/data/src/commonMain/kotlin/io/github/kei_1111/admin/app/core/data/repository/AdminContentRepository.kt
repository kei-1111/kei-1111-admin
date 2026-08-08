package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.WorksContent

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
}
