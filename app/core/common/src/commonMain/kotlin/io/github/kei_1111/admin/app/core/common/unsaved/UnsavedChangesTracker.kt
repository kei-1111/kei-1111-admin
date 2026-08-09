package io.github.kei_1111.admin.app.core.common.unsaved

/**
 * ブラウザの beforeunload 警告用に未保存有無を UI 層の外へ共有する。
 * wasm 側のイベントリスナーは Compose の状態を直接読めないため、ここを経由する。
 */
object UnsavedChangesTracker {
    var hasUnsavedChanges: Boolean = false
}
