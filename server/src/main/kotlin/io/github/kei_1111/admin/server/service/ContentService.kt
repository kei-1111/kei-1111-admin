package io.github.kei_1111.admin.server.service

import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlinx.serialization.json.Json

private const val WORKS_DRAFT_PATH = "content/draft/works.json"
private const val PROFILE_DRAFT_PATH = "content/draft/profile.json"
private const val TERMINAL_DRAFT_PATH = "content/draft/terminal-commands.json"
private const val README_DRAFT_PATH = "content/draft/readme.json"

/**
 * 下書き(draft/)の読み書きを所有するサービス(公開系は [PublishService])。
 * GCS に draft が無い間は、リポジトリ同梱のシード(resources/seed/)を初期値として返す。
 */
class ContentService(
    private val storage: ContentStorage,
) {
    internal val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    suspend fun worksDraft(): WorksContent =
        storage.read(WORKS_DRAFT_PATH)?.let { json.decodeFromString(WorksContent.serializer(), it) }
            ?: ContentSeeds.works(json)

    suspend fun saveWorksDraft(content: WorksContent) {
        storage.write(WORKS_DRAFT_PATH, json.encodeToString(WorksContent.serializer(), content))
    }

    suspend fun profileDraft(): AdminProfile =
        storage.read(PROFILE_DRAFT_PATH)?.let { json.decodeFromString(AdminProfile.serializer(), it) }
            ?: ContentSeeds.profile(json)

    suspend fun saveProfileDraft(profile: AdminProfile) {
        storage.write(PROFILE_DRAFT_PATH, json.encodeToString(AdminProfile.serializer(), profile))
    }

    suspend fun terminalDraft(): TerminalCommandsContent =
        storage.read(TERMINAL_DRAFT_PATH)
            ?.let { json.decodeFromString(TerminalCommandsContent.serializer(), it) }
            ?: ContentSeeds.terminal(json)

    suspend fun saveTerminalDraft(content: TerminalCommandsContent) {
        storage.write(TERMINAL_DRAFT_PATH, json.encodeToString(TerminalCommandsContent.serializer(), content))
    }

    suspend fun readmeDraft(): ReadmeContent =
        storage.read(README_DRAFT_PATH)?.let { json.decodeFromString(ReadmeContent.serializer(), it) }
            ?: ContentSeeds.readme(json)

    suspend fun saveReadmeDraft(content: ReadmeContent) {
        storage.write(README_DRAFT_PATH, json.encodeToString(ReadmeContent.serializer(), content))
    }
}
