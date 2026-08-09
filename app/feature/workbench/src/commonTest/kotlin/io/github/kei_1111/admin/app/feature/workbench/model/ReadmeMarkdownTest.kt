package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeInline
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadmeMarkdownTest {

    @Test
    fun parsesHeadingsBulletsAndParagraphs() {
        val source = """
            # Title

            intro paragraph

            ## Section

            - first item
            - second item
        """.trimIndent()

        val blocks = parseReadmeMarkdown(source)

        assertEquals(
            listOf(
                ReadmeBlock.Heading(level = 1, inlines = listOf(ReadmeInline.PlainText("Title"))),
                ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText("intro paragraph"))),
                ReadmeBlock.Heading(level = 2, inlines = listOf(ReadmeInline.PlainText("Section"))),
                ReadmeBlock.BulletList(
                    items = listOf(
                        listOf(ReadmeInline.PlainText("first item")),
                        listOf(ReadmeInline.PlainText("second item")),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun parsesInlineCodeAndLinksInsideText() {
        val blocks = parseReadmeMarkdown("open `ProfileScreen.kt` at [repo](https://example.com) now")

        assertEquals(
            listOf(
                ReadmeBlock.Paragraph(
                    inlines = listOf(
                        ReadmeInline.PlainText("open "),
                        ReadmeInline.InlineCode("ProfileScreen.kt"),
                        ReadmeInline.PlainText(" at "),
                        ReadmeInline.Link(text = "repo", url = "https://example.com"),
                        ReadmeInline.PlainText(" now"),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun unclosedBacktickStaysLiteral() {
        val blocks = parseReadmeMarkdown("a `broken code")

        assertEquals(
            listOf(ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText("a `broken code")))),
            blocks,
        )
    }

    @Test
    fun sourceAndBlocksRoundTrip() {
        val source = """
            # kei-1111.github.io

            Android Studio を模したサイト

            ## 歩き方

            - `ProfileScreen.kt` を開く
            - [repo](https://github.com/kei-1111) を見る
        """.trimIndent()

        val roundTripped = readmeMarkdownSource(parseReadmeMarkdown(source))

        assertEquals(source, roundTripped)
    }

    @Test
    fun parsesLinksWhoseUrlContainsParentheses() {
        val blocks = parseReadmeMarkdown("[wiki](https://en.wikipedia.org/wiki/Foo_(bar))")

        assertEquals(
            listOf(
                ReadmeBlock.Paragraph(
                    inlines = listOf(
                        ReadmeInline.Link(text = "wiki", url = "https://en.wikipedia.org/wiki/Foo_(bar)"),
                    ),
                ),
            ),
            blocks,
        )
    }
}
