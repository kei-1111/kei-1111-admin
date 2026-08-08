package io.github.kei_1111.admin.app.feature.workbench.model

/** 本体カードと同じ色分けルール: 言語/UI 系タグは緑、その他はグレー。 */
internal enum class TechChipKind { LanguageOrUi, Other }

private val languageOrUiKeywords = listOf(
    "kotlin",
    "java",
    "swift",
    "dart",
    "typescript",
    "javascript",
    "compose",
    "jetpack compose",
    "compose multiplatform",
    "swiftui",
    "flutter",
    "react",
    "wasm",
)

internal fun techChipKindOf(tag: String): TechChipKind {
    val normalized = tag.trim().lowercase()
    return if (languageOrUiKeywords.any { normalized == it || normalized.contains(it) }) {
        TechChipKind.LanguageOrUi
    } else {
        TechChipKind.Other
    }
}
