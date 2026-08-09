package io.github.kei_1111.admin.server.service

import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import java.time.Instant

private const val WORKS_PUBLISHED_PATH = "content/published/works.json"
private const val PROFILE_PUBLISHED_PATH = "content/published/profile.json"
private const val TERMINAL_PUBLISHED_PATH = "content/published/terminal-commands.json"
private const val README_PUBLISHED_PATH = "content/published/readme.json"
private const val META_PATH = "content/meta.json"

/**
 * 公開(published/)側を所有するサービス。公開は「下書きのうち status=Published の作品だけを
 * published/ へ写す」コピー操作で、破棄はその逆コピー。ポートフォリオ側サーバーは published/ のみを読む。
 */
class PublishService(
    private val storage: ContentStorage,
    private val contentService: ContentService,
    private val now: () -> String = { Instant.now().toString() },
) {
    private val json = contentService.json

    suspend fun meta(): ContentMeta =
        storage.read(META_PATH)?.let { json.decodeFromString(ContentMeta.serializer(), it) }
            ?: ContentMeta()

    /** 公開済みの現在内容。差分表示用に4ドキュメントまとめて返す(未公開は null)。 */
    suspend fun publishedSnapshot(): PublishedSnapshot = PublishedSnapshot(
        works = storage.read(WORKS_PUBLISHED_PATH)
            ?.let { json.decodeFromString(WorksContent.serializer(), it) },
        profile = storage.read(PROFILE_PUBLISHED_PATH)
            ?.let { json.decodeFromString(AdminProfile.serializer(), it) },
        terminal = storage.read(TERMINAL_PUBLISHED_PATH)
            ?.let { json.decodeFromString(TerminalCommandsContent.serializer(), it) },
        readme = storage.read(README_PUBLISHED_PATH)
            ?.let { json.decodeFromString(ReadmeContent.serializer(), it) },
    )

    /**
     * 保存済み下書きを破棄し、直前の公開内容(未公開ならシード)へ戻す。
     * draft/ を published/ の内容で上書きするコピー操作。
     */
    suspend fun discardDraft(document: ContentDocument) {
        val published = publishedSnapshot()
        when (document) {
            ContentDocument.Works ->
                contentService.saveWorksDraft(published.works ?: ContentSeeds.works(json))
            ContentDocument.Profile ->
                contentService.saveProfileDraft(published.profile ?: ContentSeeds.profile(json))
            ContentDocument.Terminal ->
                contentService.saveTerminalDraft(published.terminal ?: ContentSeeds.terminal(json))
            ContentDocument.Readme ->
                contentService.saveReadmeDraft(published.readme ?: ContentSeeds.readme(json))
        }
    }

    suspend fun publish(): ContentMeta {
        val draft = contentService.worksDraft()
        val published = WorksContent(works = draft.works.filter { it.status == ContentStatus.Published })
        storage.write(WORKS_PUBLISHED_PATH, json.encodeToString(WorksContent.serializer(), published))
        storage.write(
            PROFILE_PUBLISHED_PATH,
            json.encodeToString(AdminProfile.serializer(), contentService.profileDraft()),
        )
        storage.write(
            TERMINAL_PUBLISHED_PATH,
            json.encodeToString(TerminalCommandsContent.serializer(), contentService.terminalDraft()),
        )
        storage.write(
            README_PUBLISHED_PATH,
            json.encodeToString(ReadmeContent.serializer(), contentService.readmeDraft()),
        )
        val meta = ContentMeta(lastPublishedAt = now())
        storage.write(META_PATH, json.encodeToString(ContentMeta.serializer(), meta))
        return meta
    }
}
