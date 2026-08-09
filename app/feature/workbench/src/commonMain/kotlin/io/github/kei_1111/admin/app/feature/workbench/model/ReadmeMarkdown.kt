package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeInline

/**
 * README エディタが扱う制限付き Markdown とブロック構造の相互変換。
 * 対応記法は見出し(#〜###)・箇条書き(- )・段落・`code`・[text](url) のみで、
 * [parseReadmeMarkdown] と [readmeMarkdownSource] は往復で安定する。
 */
internal fun parseReadmeMarkdown(source: String): List<ReadmeBlock> {
    val blocks = mutableListOf<ReadmeBlock>()
    val bulletItems = mutableListOf<List<ReadmeInline>>()

    fun flushBullets() {
        if (bulletItems.isNotEmpty()) {
            blocks.add(ReadmeBlock.BulletList(items = bulletItems.toList()))
            bulletItems.clear()
        }
    }

    source.lines().forEach { line ->
        val trimmed = line.trim()
        val headingLevel = trimmed.takeWhile { it == '#' }.length
        when {
            trimmed.isEmpty() -> flushBullets()

            trimmed.startsWith("- ") -> bulletItems.add(parseInlines(trimmed.removePrefix("- ")))

            headingLevel in 1..MAX_HEADING_LEVEL && trimmed.getOrNull(headingLevel) == ' ' -> {
                flushBullets()
                blocks.add(
                    ReadmeBlock.Heading(
                        level = headingLevel,
                        inlines = parseInlines(trimmed.drop(headingLevel + 1)),
                    ),
                )
            }

            else -> {
                flushBullets()
                blocks.add(ReadmeBlock.Paragraph(inlines = parseInlines(trimmed)))
            }
        }
    }
    flushBullets()
    return blocks
}

// URL 中の括弧は 1 段までネスト可(Wikipedia 型 URL 対応)
private val linkPattern = Regex("""\[([^\]]+)]\(((?:[^()\s]|\([^()\s]*\))+)\)""")

private fun parseInlines(text: String): List<ReadmeInline> {
    val inlines = mutableListOf<ReadmeInline>()
    var rest = text
    while (rest.isNotEmpty()) {
        val code = findCodeSpan(rest)
        val link = linkPattern.find(rest)
        val next = listOfNotNull(
            code?.let { it.first to "code" },
            link?.let { it.range.first to "link" },
        ).minByOrNull { it.first }
        if (next == null) {
            inlines.addText(rest)
            break
        }
        when (next.second) {
            "code" -> {
                val (start, end) = checkNotNull(code)
                inlines.addText(rest.substring(0, start))
                inlines.add(ReadmeInline.InlineCode(rest.substring(start + 1, end)))
                rest = rest.substring(end + 1)
            }

            else -> {
                val match = checkNotNull(link)
                inlines.addText(rest.substring(0, match.range.first))
                inlines.add(ReadmeInline.Link(text = match.groupValues[1], url = match.groupValues[2]))
                rest = rest.substring(match.range.last + 1)
            }
        }
    }
    return inlines
}

/** 閉じバッククォートが無い場合はコード扱いしない(リテラルのまま残す)。 */
private fun findCodeSpan(text: String): Pair<Int, Int>? {
    val start = text.indexOf('`')
    if (start < 0) return null
    val end = text.indexOf('`', start + 1)
    return if (end < 0) null else start to end
}

private fun MutableList<ReadmeInline>.addText(text: String) {
    if (text.isNotEmpty()) add(ReadmeInline.PlainText(text))
}

internal fun readmeMarkdownSource(blocks: List<ReadmeBlock>): String =
    blocks.joinToString("\n\n") { block ->
        when (block) {
            is ReadmeBlock.Heading -> "${"#".repeat(block.level)} ${inlineSource(block.inlines)}"
            is ReadmeBlock.Paragraph -> inlineSource(block.inlines)
            is ReadmeBlock.BulletList -> block.items.joinToString("\n") { "- ${inlineSource(it)}" }
        }
    }

private fun inlineSource(inlines: List<ReadmeInline>): String = inlines.joinToString("") { inline ->
    when (inline) {
        is ReadmeInline.PlainText -> inline.text
        is ReadmeInline.InlineCode -> "`${inline.text}`"
        is ReadmeInline.Link -> "[${inline.text}](${inline.url})"
    }
}

private const val MAX_HEADING_LEVEL = 3
