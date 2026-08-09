package io.github.kei_1111.admin.app.core.utils

import androidx.compose.ui.input.pointer.PointerIcon

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// 静的プレビューでホバーは発生しないため既定カーソルのままとする。
actual val VerticalResizeCursor: PointerIcon = PointerIcon.Default
actual val HorizontalResizeCursor: PointerIcon = PointerIcon.Default
