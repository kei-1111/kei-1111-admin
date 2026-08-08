package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalKeiColorScheme = staticCompositionLocalOf<KeiColorScheme> { error("KeiTheme を経由せず KeiColorScheme が参照されました") }
val LocalKeiTypography = staticCompositionLocalOf<KeiTypography> { error("KeiTheme を経由せず KeiTypography が参照されました") }
val LocalKeiShapes = staticCompositionLocalOf<KeiShapes> { error("KeiTheme を経由せず KeiShapes が参照されました") }
val LocalKeiIcons = staticCompositionLocalOf<KeiIcons> { error("KeiTheme を経由せず KeiIcons が参照されました") }

@Composable
fun KeiTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isDark) KeiDarkColorScheme else KeiLightColorScheme
    CompositionLocalProvider(
        LocalKeiColorScheme provides colorScheme,
        LocalKeiTypography provides keiTypography(colorScheme),
        LocalKeiShapes provides keiShapes,
        LocalKeiIcons provides keiIcons,
        content = content,
    )
}

object KeiTheme {
    val colors: KeiColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalKeiColorScheme.current
    val typography: KeiTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalKeiTypography.current
    val shapes: KeiShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalKeiShapes.current
    val icons: KeiIcons
        @Composable
        @ReadOnlyComposable
        get() = LocalKeiIcons.current
}
