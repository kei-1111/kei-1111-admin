package io.github.kei_1111.admin.shared.model.portfolio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 本体サイト `GET /api/issues` の契約モデルの写し(TODO ウィンドウの表示データ)。 */
@Serializable
data class GitHubIssue(
    @SerialName("number")
    val number: Int,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String,
    /** Issue タイトルの `[Type]:` プレフィックスから本体サーバーが抽出した種別。無ければ null。 */
    @SerialName("type")
    val type: String? = null,
)

@Serializable
data class GitHubIssues(
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("issues")
    val issues: List<GitHubIssue> = emptyList(),
)
