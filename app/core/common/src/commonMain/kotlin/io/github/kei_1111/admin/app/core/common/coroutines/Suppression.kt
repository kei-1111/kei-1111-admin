package io.github.kei_1111.admin.app.core.common.coroutines

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** [block] の失敗を [onFailure] の値で回復する。コルーチンの cancellation は回復せず必ず伝播する。 */
suspend inline fun <T> recoverOrElse(block: () -> T, onFailure: (Exception) -> T): T =
    try {
        block()
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        onFailure(e)
    }

/** [block] の失敗を握り潰す（cancellation は伝播）。best-effort な処理の唯一の書き方。 */
suspend inline fun runBestEffort(block: () -> Unit) {
    recoverOrElse(block) { }
}
