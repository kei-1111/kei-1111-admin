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
    val techStack: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
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
