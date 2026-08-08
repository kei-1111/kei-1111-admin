@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.core.designsystem.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowLayout {
    Desktop,
    Mobile,
}

private val CompactWidthBreakpoint = 900.dp

fun windowLayoutFor(width: Dp): WindowLayout =
    if (width < CompactWidthBreakpoint) WindowLayout.Mobile else WindowLayout.Desktop
