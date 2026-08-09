package io.github.kei_1111.admin.shared.model.portfolio

import io.github.kei_1111.admin.shared.model.portfolio.serialization.ImmutableListSerializer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * client / server 間で共有する JSON 契約。互換性ルールは [GitHubProfile] の KDoc を参照。
 * [iconUrl] / [screenshots] の非 http(s) 値はクライアント配信オリジン基準の相対パス。
 */
@Serializable
data class Work(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    /** 例 "Android Launcher App"。 */
    @SerialName("kind")
    val kind: String,
    /** 例 "2024–"。 */
    @SerialName("period")
    val period: String,
    @SerialName("description")
    val description: LocalizedText,
    @SerialName("tags")
    @Serializable(with = ImmutableListSerializer::class)
    val tags: ImmutableList<WorkTag> = persistentListOf(),
    /** 担当領域。 */
    @SerialName("roles")
    @Serializable(with = ImmutableListSerializer::class)
    val roles: ImmutableList<LocalizedText> = persistentListOf(),
    /** 40dp タイル用アイコン。null はクライアント側の既定アイコンを使う。 */
    @SerialName("iconUrl")
    val iconUrl: String? = null,
    @SerialName("screenshots")
    @Serializable(with = ImmutableListSerializer::class)
    val screenshots: ImmutableList<String> = persistentListOf(),
    @SerialName("storeUrl")
    val storeUrl: String? = null,
    @SerialName("sourceUrl")
    val sourceUrl: String? = null,
)

/** `GET /api/works` のレスポンス全体。他エンドポイント同様、将来のフィールド追加に備えて配列をオブジェクトで包む。 */
@Serializable
data class Works(
    @SerialName("items")
    @Serializable(with = ImmutableListSerializer::class)
    val items: ImmutableList<Work>,
)

/** タグ1件。[accent] は言語・UI系タグを示し、カード/シートで緑表示する。 */
@Serializable
data class WorkTag(
    @SerialName("name")
    val name: String,
    @SerialName("accent")
    val accent: Boolean = false,
)
