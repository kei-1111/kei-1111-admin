package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

@Serializable
enum class ContentStatus { Published, Draft }

@Serializable
data class Work(
    val id: String,
    val name: String,
    val type: String = "",
    val period: String = "",
    val about: String = "",
    /** 英語版。空なら配信時に [about] へフォールバックする。 */
    val aboutEn: String = "",
    val techStack: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    /** [roles] と index で対になる英語版。不足分・空文字は ja へフォールバックする。 */
    val rolesEn: List<String> = emptyList(),
    /** 40dp タイル用アイコン。admin アップロードの配信パスまたは本体同梱の相対パス。空は既定アイコン。 */
    val iconUrl: String = "",
    /** 先頭がカバー画像。値は GCS オブジェクトパス。 */
    val screenshots: List<String> = emptyList(),
    val googlePlayUrl: String = "",
    val sourceUrl: String = "",
    val status: ContentStatus = ContentStatus.Draft,
    val updatedAt: String = "",
)

@Serializable
data class WorksContent(
    val works: List<Work> = emptyList(),
)
