package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// ブラウザロケールは存在しないため、初期言語の検出値を持たない。
actual fun browserLanguageTag(): String? = null
