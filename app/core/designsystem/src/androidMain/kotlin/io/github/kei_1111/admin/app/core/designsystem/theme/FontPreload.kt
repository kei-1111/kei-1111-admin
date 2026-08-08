package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Composable

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// Android の Font(resource) は同期ロードのため、常にロード済みとして扱う。
@Composable
actual fun rememberJetBrainsMonoFontsLoaded(): Boolean = true

@Composable
actual fun rememberNotoSansJpFontsLoaded(): Boolean = true

@Composable
actual fun rememberZenKakuGothicNewFontsLoaded(): Boolean = true
