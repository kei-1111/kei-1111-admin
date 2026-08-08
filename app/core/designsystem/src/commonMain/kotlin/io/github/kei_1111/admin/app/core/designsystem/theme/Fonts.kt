package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kei_1111_admin.app.core.designsystem.generated.resources.Res
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_bold
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_medium
import kei_1111_admin.app.core.designsystem.generated.resources.jetbrains_mono_regular
import kei_1111_admin.app.core.designsystem.generated.resources.noto_sans_jp_medium
import kei_1111_admin.app.core.designsystem.generated.resources.noto_sans_jp_semi_bold
import kei_1111_admin.app.core.designsystem.generated.resources.zen_kaku_gothic_new_bold
import kei_1111_admin.app.core.designsystem.generated.resources.zen_kaku_gothic_new_regular
import org.jetbrains.compose.resources.Font

/**
 * 末尾に Noto Sans JP を並べ、JetBrains Mono に無い日本語グリフをフォールバックさせる。
 * (resource, weight) の組を変更したら FontPreload.kt のプリロードも更新すること。
 */
@Composable
fun JetBrainsMonoFamily() = FontFamily(
    Font(
        resource = Res.font.jetbrains_mono_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.jetbrains_mono_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resource = Res.font.jetbrains_mono_bold,
        weight = FontWeight.Bold,
    ),
    Font(
        resource = Res.font.noto_sans_jp_medium,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.noto_sans_jp_semi_bold,
        weight = FontWeight.Bold,
    ),
)

/**
 * wasm ではフォールバック解決を伴う計測が実描画より狭くなり `softWrap = false` でも
 * 日本語行が折り返されるため、日本語区間にはこのファミリーを明示指定して計測を確定させる。
 * (resource, weight) の組は [JetBrainsMonoFamily] のフォールバック枠と一致させること
 * （FontPreload.kt が同じ組をプリロードしている）。
 */
@Composable
fun CodeJapaneseFallbackFamily() = FontFamily(
    Font(
        resource = Res.font.noto_sans_jp_medium,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.noto_sans_jp_semi_bold,
        weight = FontWeight.Bold,
    ),
)

/** (resource, weight) の組を変更したら FontPreload.kt のプリロードも更新すること。 */
@Composable
fun ZenKakuGothicNewFamily() = FontFamily(
    Font(
        resource = Res.font.zen_kaku_gothic_new_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.zen_kaku_gothic_new_bold,
        weight = FontWeight.Bold,
    ),
)

/** (resource, weight) の組を変更したら FontPreload.kt のプリロードも更新すること。 */
@Composable
fun IdeJapaneseFamily() = FontFamily(
    Font(
        resource = Res.font.noto_sans_jp_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resource = Res.font.noto_sans_jp_semi_bold,
        weight = FontWeight.SemiBold,
    ),
)
