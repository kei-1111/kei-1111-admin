package io.github.kei_1111.admin.app.core.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Copied from nav3-recipes' results/event pattern.
 *
 * Changes:
 * - Changed channelMap from MutableMap to SnapshotStateMap
 *   for compatibility with DialogSceneStrategy (Issue #111 fix)
 *
 * @see <a href="https://github.com/android/nav3-recipes">nav3-recipes</a>
 * @see <a href="https://github.com/android/nav3-recipes/issues/111">Issue #111</a>
 */
class ResultEventBus {
    private val channelMap: SnapshotStateMap<KType, Channel<*>> = mutableStateMapOf()

    private fun <T> getOrCreateChannel(type: KType): Channel<T> {
        @Suppress("UNCHECKED_CAST")
        return channelMap.getOrPut(type) { Channel<T>(Channel.BUFFERED) } as Channel<T>
    }

    inline fun <reified T : Any> sendResult(result: T) {
        sendResult(typeOf<T>(), result)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun <T : Any> sendResult(type: KType, result: T) {
        getOrCreateChannel<T>(type).trySend(result)
    }

    @PublishedApi
    internal fun <T : Any> getResultFlow(type: KType) = getOrCreateChannel<T>(type).receiveAsFlow()
}

val LocalResultEventBus = compositionLocalOf<ResultEventBus> {
    error("ResultEventBus not provided. Wrap content with CompositionLocalProvider.")
}
