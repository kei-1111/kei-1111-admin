@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.kei_1111.admin.app.core.common.logging

import kotlin.js.ExperimentalWasmJsInterop

// ローカルタイムゾーンでの整形が必要なため JS の Date を使う（kotlinx-browser に Date の束縛は無い）
private fun jsLogTimestamp(): String = js(
    """((d, p) =>
        d.getFullYear() + '-' + p(d.getMonth() + 1, 2) + '-' + p(d.getDate(), 2) +
        ' ' + p(d.getHours(), 2) + ':' + p(d.getMinutes(), 2) + ':' + p(d.getSeconds(), 2) +
        '.' + p(d.getMilliseconds(), 3)
    )(new Date(), (n, w) => String(n).padStart(w, '0'))""",
)

internal actual fun currentLogTimestamp(): String = jsLogTimestamp()
