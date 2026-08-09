package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// 静的プレビューではアニメーションが動かないため、常に false で問題ない。
actual fun prefersReducedMotion(): Boolean = false
