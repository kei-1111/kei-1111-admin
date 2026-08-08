package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Composable

/**
 * wasm では Font(resource) が非同期ロードのため、キャッシュ未投入のまま画面を描画すると
 * 日本語グリフを持たない既定フォントで確定してしまう。画面遷移前に本 API でロードを完了させる。
 * ロード対象の (resource, weight) の組は各ファミリー定義（[JetBrainsMonoFamily] ほか）と一致させること。
 */
@Composable
expect fun rememberJetBrainsMonoFontsLoaded(): Boolean

@Composable
expect fun rememberNotoSansJpFontsLoaded(): Boolean

@Composable
expect fun rememberZenKakuGothicNewFontsLoaded(): Boolean
