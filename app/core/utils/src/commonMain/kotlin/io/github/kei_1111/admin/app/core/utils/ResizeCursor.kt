package io.github.kei_1111.admin.app.core.utils

import androidx.compose.ui.input.pointer.PointerIcon

/** 上下リサイズ境界用のマウスカーソル（web では ns-resize）。`Modifier.pointerHoverIcon` に渡す。 */
expect val VerticalResizeCursor: PointerIcon

/** 左右リサイズ境界用のマウスカーソル（web では ew-resize）。`Modifier.pointerHoverIcon` に渡す。 */
expect val HorizontalResizeCursor: PointerIcon
