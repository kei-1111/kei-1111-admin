package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource

/** 焼き込みアートを再着色しない（tint=Color.Unspecified 固定）。 */
@Composable
fun KeiIcon(
    icon: ThemedIcon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(if (KeiTheme.colors.isDark) icon.dark else icon.light),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = Color.Unspecified,
    )
}

@Composable
fun KeiIcon(
    icon: TintedIcon,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(icon.resource),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
