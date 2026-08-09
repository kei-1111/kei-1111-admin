@file:OptIn(ExperimentalComposeUiApi::class)

package io.github.kei_1111.admin.app.core.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.fromKeyword

// CMP 1.8+ の web 公式 API。CSS cursor キーワードをそのまま PointerIcon にする
actual val VerticalResizeCursor: PointerIcon = PointerIcon.fromKeyword("ns-resize")
actual val HorizontalResizeCursor: PointerIcon = PointerIcon.fromKeyword("ew-resize")
