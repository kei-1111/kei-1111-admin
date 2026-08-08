@file:OptIn(ExperimentalResourceApi::class)

package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import kei_1111_admin.app.core.designsystem.generated.resources.Res
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_bold
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_medium
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_regular
import kei_1111_admin.app.core.designsystem.generated.resources.noto_sans_jp_medium
import kei_1111_admin.app.core.designsystem.generated.resources.noto_sans_jp_semi_bold
import kei_1111_admin.app.core.designsystem.generated.resources.zen_kaku_gothic_new_bold
import kei_1111_admin.app.core.designsystem.generated.resources.zen_kaku_gothic_new_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont

@Composable
actual fun rememberJetBrainsMonoFontsLoaded(): Boolean {
    val regular by preloadFont(Res.font.jetbrains_mono_regular, FontWeight.Normal)
    val medium by preloadFont(Res.font.jetbrains_mono_medium, FontWeight.Medium)
    val bold by preloadFont(Res.font.jetbrains_mono_bold, FontWeight.Bold)

    // JetBrainsMonoFamily は日本語フォールバックとして Noto Sans JP を Normal / Bold 枠で持つ。
    // フォントキャッシュのキーには weight が含まれるため、同じ組でロードしないとキャッシュミスになる。
    val jpNormal by preloadFont(Res.font.noto_sans_jp_medium, FontWeight.Normal)
    val jpBold by preloadFont(Res.font.noto_sans_jp_semi_bold, FontWeight.Bold)

    return regular != null && medium != null && bold != null && jpNormal != null && jpBold != null
}

@Composable
actual fun rememberNotoSansJpFontsLoaded(): Boolean {
    // NotoSansJpFamily / IdeJapaneseFamily 共通の Medium / SemiBold 枠
    val medium by preloadFont(Res.font.noto_sans_jp_medium, FontWeight.Medium)
    val semiBold by preloadFont(Res.font.noto_sans_jp_semi_bold, FontWeight.SemiBold)

    return medium != null && semiBold != null
}

@Composable
actual fun rememberZenKakuGothicNewFontsLoaded(): Boolean {
    val regular by preloadFont(Res.font.zen_kaku_gothic_new_regular, FontWeight.Normal)
    val bold by preloadFont(Res.font.zen_kaku_gothic_new_bold, FontWeight.Bold)

    return regular != null && bold != null
}
