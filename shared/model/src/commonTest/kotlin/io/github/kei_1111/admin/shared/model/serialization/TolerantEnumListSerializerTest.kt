package io.github.kei_1111.admin.shared.model.serialization

import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.github.kei_1111.admin.shared.model.portfolio.LanguageShare
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.PinnedRepo
import io.github.kei_1111.admin.shared.model.portfolio.RepoLanguage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val tolerantJson = Json

class TolerantEnumListSerializerTest {

    @Test
    fun unknownPinnedRepoLanguageBecomesNullAndKnownLanguageIsKept() {
        val decoded = tolerantJson.decodeFromString(GitHubProfile.serializer(), MIXED_ENUM_PROFILE)

        assertEquals(listOf("rust-repo", "kotlin-repo"), decoded.pinnedRepos.map(PinnedRepo::name))
        assertNull(decoded.pinnedRepos.first().language)
        assertEquals(RepoLanguage.Kotlin, decoded.pinnedRepos.last().language)
    }

    @Test
    fun unknownLanguageShareIsDroppedAndKnownShareIsKept() {
        val decoded = tolerantJson.decodeFromString(GitHubProfile.serializer(), MIXED_ENUM_PROFILE)

        assertEquals(
            persistentListOf(LanguageShare(language = RepoLanguage.Kotlin, share = 0.75f)),
            decoded.languages,
        )
    }

    @Test
    fun unknownLinkServiceIsDroppedAndKnownServiceIsKept() {
        val decoded = tolerantJson.decodeFromString(GitHubProfile.serializer(), MIXED_ENUM_PROFILE)

        assertEquals(
            persistentListOf(
                LinkService(type = LinkServiceType.GitHub, name = "GitHub", url = "https://github.com/kei-1111"),
            ),
            decoded.links,
        )
    }

    @Test
    fun allUnknownFilteredEnumListsDecodeToEmpty() {
        val decoded = tolerantJson.decodeFromString(GitHubProfile.serializer(), ALL_UNKNOWN_ENUM_PROFILE)

        assertEquals(persistentListOf(), decoded.languages)
        assertEquals(persistentListOf(), decoded.links)
    }

    @Test
    fun encodingEmitsStandardEnumNamesForEveryTolerantList() {
        val encoded = tolerantJson.encodeToString(GitHubProfile.serializer(), profileWithKnownEnums())
        val root = tolerantJson.parseToJsonElement(encoded).jsonObject

        assertEquals("Kotlin", root.getValue("pinnedRepos").jsonArray.single().jsonObject.getValue("language").jsonPrimitive.content)
        assertEquals("Swift", root.getValue("languages").jsonArray.single().jsonObject.getValue("language").jsonPrimitive.content)
        assertEquals("Qiita", root.getValue("links").jsonArray.single().jsonObject.getValue("type").jsonPrimitive.content)
    }
}

private fun profileWithKnownEnums() = GitHubProfile(
    name = LocalizedText(ja = "けい", en = "Kei"),
    handle = "kei-1111",
    location = "Japan",
    role = "Developer",
    followers = 1,
    following = 2,
    repos = 3,
    totalStars = 4,
    pinnedRepos = persistentListOf(
        PinnedRepo(
            name = "repo",
            description = LocalizedText(ja = "説明", en = "Description"),
            url = "https://example.com/repo",
            language = RepoLanguage.Kotlin,
        ),
    ),
    languages = persistentListOf(LanguageShare(language = RepoLanguage.Swift, share = 1f)),
    links = persistentListOf(LinkService(type = LinkServiceType.Qiita, name = "Qiita", url = "https://qiita.com/kei-1111")),
)

private val MIXED_ENUM_PROFILE =
    """
    {
      "name": { "ja": "けい", "en": "Kei" },
      "handle": "kei-1111",
      "location": "Japan",
      "role": "Developer",
      "followers": 1,
      "following": 2,
      "repos": 3,
      "totalStars": 4,
      "pinnedRepos": [
        {
          "name": "rust-repo",
          "description": { "ja": "Rust", "en": "Rust" },
          "url": "https://example.com/rust",
          "language": "Rust"
        },
        {
          "name": "kotlin-repo",
          "description": { "ja": "Kotlin", "en": "Kotlin" },
          "url": "https://example.com/kotlin",
          "language": "Kotlin"
        }
      ],
      "languages": [
        { "language": "Rust", "share": 0.25 },
        { "language": "Kotlin", "share": 0.75 }
      ],
      "links": [
        { "type": "LinkedIn", "name": "LinkedIn", "url": "https://example.com/in" },
        { "type": "GitHub", "name": "GitHub", "url": "https://github.com/kei-1111" }
      ]
    }
    """.trimIndent()

private val ALL_UNKNOWN_ENUM_PROFILE =
    """
    {
      "name": { "ja": "けい", "en": "Kei" },
      "handle": "kei-1111",
      "location": "Japan",
      "role": "Developer",
      "followers": 1,
      "following": 2,
      "repos": 3,
      "totalStars": 4,
      "pinnedRepos": [],
      "languages": [{ "language": "Rust", "share": 1.0 }],
      "links": [{ "type": "LinkedIn", "name": "LinkedIn", "url": "https://example.com/in" }]
    }
    """.trimIndent()
