package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
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

    /** Preview カード用: 本体サイト API のプロキシから GitHub 由来データを取り寄せる。 */

    suspend fun fetchTerminalDraft(): TerminalCommandsContent
    suspend fun saveTerminalDraft(content: TerminalCommandsContent): TerminalCommandsContent
    suspend fun fetchReadmeDraft(): ReadmeContent
    suspend fun saveReadmeDraft(content: ReadmeContent): ReadmeContent
}
