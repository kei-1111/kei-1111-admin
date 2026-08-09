package io.github.kei_1111.admin.app.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.events.Event

private const val VISIBILITY_CHANGE_EVENT = "visibilitychange"

@Composable
actual fun rememberIsPageVisible(): State<Boolean> {
    val isVisible = remember { mutableStateOf(isDocumentVisible()) }
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { isVisible.value = isDocumentVisible() }
        document.addEventListener(VISIBILITY_CHANGE_EVENT, listener)
        onDispose { document.removeEventListener(VISIBILITY_CHANGE_EVENT, listener) }
    }
    return isVisible
}

// kotlinx-browser の Document には visibilityState プロパティが無いため js 相互運用で読む
private fun isDocumentVisible(): Boolean = js("document.visibilityState === 'visible'")
