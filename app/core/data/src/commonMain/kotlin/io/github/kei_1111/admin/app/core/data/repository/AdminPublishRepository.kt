package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.PublishedSnapshot

/** 公開まわり: published/ の読み出し・公開実行・下書き破棄・メタ情報。 */
interface AdminPublishRepository {
    suspend fun fetchMeta(): ContentMeta
    suspend fun publish(): ContentMeta
    suspend fun fetchPublishedSnapshot(): PublishedSnapshot
    suspend fun discardDraft(document: ContentDocument)
}
