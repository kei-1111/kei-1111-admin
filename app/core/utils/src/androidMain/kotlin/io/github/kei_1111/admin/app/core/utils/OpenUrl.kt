package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// 静的プレビューでクリックは発生しないため、実装は持たない。
actual fun openUrl(url: String) = Unit
