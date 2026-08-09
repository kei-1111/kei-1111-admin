package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// ネットワーク画像は読み込まれないため、解決結果は使われない。
actual fun appOrigin(): String = ""
