package io.github.kei_1111.admin.app.core.designsystem.language

/** [tag] は BCP 47 の言語タグ（document.lang / リソース解決に使用）。 */
enum class KeiLanguage(val tag: String) {
    Ja("ja"),
    En("en"),
    ;

    companion object {
        fun fromTag(tag: String?): KeiLanguage = if (tag?.startsWith("ja") == true) Ja else En
    }
}
