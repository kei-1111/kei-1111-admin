package io.github.kei_1111.admin.app.core.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

class HoverState(
    val interactionSource: MutableInteractionSource,
    val hovered: Boolean,
)

@Composable
fun rememberHoverState(): HoverState {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    return HoverState(interactionSource = interactionSource, hovered = hovered)
}
