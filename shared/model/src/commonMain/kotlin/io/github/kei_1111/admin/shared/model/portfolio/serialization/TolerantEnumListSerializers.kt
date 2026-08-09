package io.github.kei_1111.admin.shared.model.portfolio.serialization

import io.github.kei_1111.admin.shared.model.portfolio.LanguageShare
import io.github.kei_1111.admin.shared.model.portfolio.LinkService
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.PinnedRepo
import io.github.kei_1111.admin.shared.model.portfolio.RepoLanguage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * 新しい enum 定数の配信後も、旧 client が未知値以外の profile を描画し続けられるようにする。
 * "language" / "type" は GitHubProfile.kt の @SerialName と対応する。
 */
internal object TolerantPinnedRepoListSerializer :
    JsonTransformingSerializer<ImmutableList<PinnedRepo>>(
        ImmutableListSerializer(PinnedRepo.serializer()),
    ) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonArray) return element

        return JsonArray(
            element.map { repo ->
                if (repo.hasUnknownEnumString("language", knownRepoLanguages)) {
                    JsonObject((repo as JsonObject) - "language")
                } else {
                    repo
                }
            },
        )
    }
}

internal object TolerantLanguageShareListSerializer :
    JsonTransformingSerializer<ImmutableList<LanguageShare>>(
        ImmutableListSerializer(LanguageShare.serializer()),
    ) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonArray) return element

        return JsonArray(
            element.filterNot { it.hasUnknownEnumString("language", knownRepoLanguages) },
        )
    }
}

internal object TolerantLinkServiceListSerializer :
    JsonTransformingSerializer<ImmutableList<LinkService>>(
        ImmutableListSerializer(LinkService.serializer()),
    ) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonArray) return element

        return JsonArray(
            element.filterNot { it.hasUnknownEnumString("type", knownLinkServiceTypes) },
        )
    }
}

private val knownRepoLanguages = RepoLanguage.serializer().descriptor.wireNames()
private val knownLinkServiceTypes = LinkServiceType.serializer().descriptor.wireNames()

private fun SerialDescriptor.wireNames(): Set<String> =
    (0 until elementsCount).mapTo(mutableSetOf()) { getElementName(it) }

private fun JsonElement.hasUnknownEnumString(key: String, known: Set<String>): Boolean {
    val value = (this as? JsonObject)?.get(key)
    return value is JsonPrimitive && value.isString && value.content !in known
}
