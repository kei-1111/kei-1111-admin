package io.github.kei_1111.admin.app.core.utils

import kotlinx.browser.window

actual fun openUrl(url: String) {
    window.open(url)
}
