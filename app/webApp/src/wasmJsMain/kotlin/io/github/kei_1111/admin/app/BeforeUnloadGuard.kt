@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.kei_1111.admin.app

import io.github.kei_1111.admin.app.core.common.unsaved.UnsavedChangesTracker
import kotlin.js.ExperimentalWasmJsInterop

@JsFun(
    """
    (hasUnsaved) => {
        window.addEventListener('beforeunload', (e) => {
            if (hasUnsaved()) {
                e.preventDefault();
                e.returnValue = '';
            }
        });
    }
    """,
)
private external fun installBeforeUnloadListener(hasUnsaved: () -> Boolean)

/** 未保存の変更があるままタブ/ウィンドウを閉じようとしたらブラウザ標準の確認を出す。 */
internal fun installBeforeUnloadGuard() {
    installBeforeUnloadListener { UnsavedChangesTracker.hasUnsavedChanges }
}
