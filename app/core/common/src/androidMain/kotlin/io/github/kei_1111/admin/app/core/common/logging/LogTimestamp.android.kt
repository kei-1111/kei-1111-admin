package io.github.kei_1111.admin.app.core.common.logging

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal actual fun currentLogTimestamp(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
