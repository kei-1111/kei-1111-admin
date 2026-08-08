package io.github.kei_1111.admin.app

import io.github.kei_1111.admin.app.feature.workbench.WorkbenchShortcuts
import kotlinx.browser.document
import org.w3c.dom.events.KeyboardEvent

/**
 * ⌘S = 下書き保存 / ⌘⏎ = 公開。ブラウザ既定(ページ保存ダイアログ等)を抑止するため
 * preventDefault を必ず呼ぶ。リスナーを解除しないのは単一ページの wasm アプリで
 * 寿命がページ自体と一致するため。
 */
internal fun installWorkbenchShortcutListener() {
    document.addEventListener("keydown", { event ->
        val keyboardEvent = event as KeyboardEvent
        val meta = keyboardEvent.metaKey || keyboardEvent.ctrlKey
        when {
            meta && keyboardEvent.key.equals("s", ignoreCase = true) -> {
                keyboardEvent.preventDefault()
                WorkbenchShortcuts.requestSave()
            }
            meta && keyboardEvent.key == "Enter" -> {
                keyboardEvent.preventDefault()
                WorkbenchShortcuts.requestPublish()
            }
        }
    })
}
