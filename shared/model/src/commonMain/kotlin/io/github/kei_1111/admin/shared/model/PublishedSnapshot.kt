package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

/** draft/publish 二段構成の対象ドキュメント。 */
@Serializable
enum class ContentDocument { Works, Profile, Terminal, Readme }

/** 公開済み(published/)の現在内容。null はそのドキュメントが未公開。 */
@Serializable
data class PublishedSnapshot(
    val works: WorksContent? = null,
    val profile: AdminProfile? = null,
    val terminal: TerminalCommandsContent? = null,
    val readme: ReadmeContent? = null,
)

@Serializable
data class DiscardDraftRequest(
    val document: ContentDocument,
)
