package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * README ページのブロック構造。本体サイトの MarkdownBlock / MarkdownInline を写した公開スキーマで、
 * ポートフォリオ側は raw Markdown ではなくこの構造をそのまま描画する。
 */
@Serializable
data class ReadmeContent(
    val ja: List<ReadmeBlock> = emptyList(),
    val en: List<ReadmeBlock> = emptyList(),
)

@Serializable
sealed interface ReadmeBlock {
    @Serializable
    @SerialName("heading")
    data class Heading(val level: Int, val inlines: List<ReadmeInline>) : ReadmeBlock

    @Serializable
    @SerialName("paragraph")
    data class Paragraph(val inlines: List<ReadmeInline>) : ReadmeBlock

    @Serializable
    @SerialName("bulletList")
    data class BulletList(val items: List<List<ReadmeInline>>) : ReadmeBlock
}

@Serializable
sealed interface ReadmeInline {
    @Serializable
    @SerialName("text")
    data class PlainText(val text: String) : ReadmeInline

    @Serializable
    @SerialName("code")
    data class InlineCode(val text: String) : ReadmeInline

    @Serializable
    @SerialName("link")
    data class Link(val text: String, val url: String) : ReadmeInline
}
