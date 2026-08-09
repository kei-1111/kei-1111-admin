package io.github.kei_1111.admin.app.core.utils

import kotlinx.browser.window

actual fun browserLanguageTag(): String? = window.navigator.language
