package io.github.kei_1111.admin.app.core.utils

import kotlinx.browser.document

actual fun setDocumentLanguage(languageTag: String) {
    document.documentElement?.setAttribute("lang", languageTag)
}
