package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// document は存在しないため、実装は持たない。
actual fun setDocumentLanguage(languageTag: String) = Unit
