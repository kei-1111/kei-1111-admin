package io.github.kei_1111.admin.app.core.utils

import kotlinx.browser.window

actual fun appOrigin(): String = window.location.origin
