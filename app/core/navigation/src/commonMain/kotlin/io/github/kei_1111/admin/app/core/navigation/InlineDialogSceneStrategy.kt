package io.github.kei_1111.admin.app.core.navigation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * Dialog metadataを持つentryを同じCompose scene内のoverlayとして描画する。
 *
 * Compose Web 1.11.1のa11y同期はSemanticsOwnerを1つしか保持できないため、別scene layerを
 * 作る標準のDialogSceneStrategyを閉じるとルートのa11yミラーまで失われる。
 */
class InlineDialogSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): OverlayScene<T>? {
        val entry = entries.lastOrNull()
        return if (entry != null && entry.metadata[DialogTransitionKey] != null) {
            InlineDialogScene(
                entry = entry,
                previousEntries = entries.dropLast(1),
                onBack = onBack,
            )
        } else {
            null
        }
    }
}

private class InlineDialogScene<T : Any>(
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val key: Any = entry.contentKey
    override val entries: List<NavEntry<T>> = listOf(entry)
    override val overlaidEntries: List<NavEntry<T>> = previousEntries

    override val content: @Composable () -> Unit = {
        var panelBounds by remember { mutableStateOf<Rect?>(null) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(panelBounds, onBack) {
                    detectTapGestures { position ->
                        if (panelBounds?.contains(position) == false) onBack()
                    }
                }
                .onPreviewKeyEvent { event ->
                    if (
                        event.type == KeyEventType.KeyDown &&
                        event.key == Key.Escape
                    ) {
                        onBack()
                        true
                    } else {
                        false
                    }
                }
                .semantics { dialog() },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.onGloballyPositioned {
                    panelBounds = it.boundsInParent()
                },
            ) {
                entry.Content()
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        other is InlineDialogScene<*> &&
            key == other.key &&
            entry == other.entry &&
            previousEntries == other.previousEntries

    override fun hashCode(): Int =
        key.hashCode() * HASH_MULTIPLIER +
            entry.hashCode() * HASH_MULTIPLIER +
            previousEntries.hashCode()

    private companion object {
        const val HASH_MULTIPLIER = 31
    }
}
