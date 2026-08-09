package io.github.kei_1111.admin.shared.model.portfolio

import io.github.kei_1111.admin.shared.model.portfolio.serialization.TolerantLanguageShareListSerializer
import io.github.kei_1111.admin.shared.model.portfolio.serialization.TolerantLinkServiceListSerializer
import io.github.kei_1111.admin.shared.model.portfolio.serialization.TolerantPinnedRepoListSerializer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubProfile(
    @SerialName("name")
    val name: LocalizedText,
    @SerialName("handle")
    val handle: String,
    @SerialName("location")
    val location: String,
    @SerialName("role")
    val role: String,
    @SerialName("followers")
    val followers: Int,
    @SerialName("following")
    val following: Int,
    @SerialName("repos")
    val repos: Int,
    @SerialName("totalStars")
    val totalStars: Int,
    @SerialName("pinnedRepos")
    @Serializable(with = TolerantPinnedRepoListSerializer::class)
    val pinnedRepos: ImmutableList<PinnedRepo>,
    @SerialName("languages")
    @Serializable(with = TolerantLanguageShareListSerializer::class)
    val languages: ImmutableList<LanguageShare>,
    @SerialName("links")
    @Serializable(with = TolerantLinkServiceListSerializer::class)
    val links: ImmutableList<LinkService>,
)

@Serializable
data class PinnedRepo(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: LocalizedText,
    @SerialName("url")
    val url: String,
    @SerialName("language")
    val language: RepoLanguage? = null,
    @SerialName("stars")
    val stars: Int? = null,
)

@Serializable
enum class RepoLanguage(val displayName: String) {
    @SerialName("Kotlin")
    Kotlin("Kotlin"),

    @SerialName("Swift")
    Swift("Swift"),

    @SerialName("Shell")
    Shell("Shell"),
}

@Serializable
data class LanguageShare(
    @SerialName("language")
    val language: RepoLanguage,
    @SerialName("share")
    val share: Float,
)

@Serializable
data class LinkService(
    @SerialName("type")
    val type: LinkServiceType,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
)

@Serializable
enum class LinkServiceType {
    @SerialName("GitHub")
    GitHub,

    @SerialName("X")
    X,

    @SerialName("Qiita")
    Qiita,

    @SerialName("Note")
    Note,
}
