@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiColorScheme

/** 実 AS の RecentProject.MainToolbarGradient.width（グロー中心から desk へ戻る水平距離）。 */
private val GlowRampWidth = 600.dp

/** 実 AS の RecentProject.MainToolbarGradient.height（グローが desk へ溶け切る縦距離）。 */
private val GlowFadeHeight = 300.dp

/** グロー中心（プロジェクトチップ位置）の幅比。実 AS スクリーンショット実測値。 */
private const val GLOW_CENTER_X_RATIO = 0.07f

/**
 * ウィンドウ背景（デスク）。実 AS Islands と同様、プロジェクトチップ下を中心とするグローが
 * ライト・ダーク両テーマで左上に乗る（色味は IDE のプロジェクト識別色由来）。
 * 実 AS の IslandsGradientPainter と同じく、desk→deskGlow→desk の水平グラデーションに
 * 上端から [GlowFadeHeight] で desk へ溶ける縦フェードを重ねて再現する。
 */
// テーマ切替への追従は、呼び出し側 Composable が再コンポーズして colors を渡し直すことに依存する
internal fun Modifier.deskBackground(colors: KeiColorScheme): Modifier = drawWithCache {
    val desk = colors.desk
    val glow = colors.deskGlow
    val centerX = size.width * GLOW_CENTER_X_RATIO
    val rampEnd = centerX + GlowRampWidth.toPx()
    val fadeHeight = GlowFadeHeight.toPx()
    val horizontal = Brush.horizontalGradient(
        0f to desk,
        centerX / rampEnd to glow,
        1f to desk,
        endX = rampEnd,
    )
    val vertical = Brush.verticalGradient(
        0f to desk.copy(alpha = 0f),
        1f to desk,
        endY = fadeHeight,
    )
    onDrawBehind {
        drawRect(desk)
        drawRect(horizontal, size = Size(rampEnd, fadeHeight))
        drawRect(vertical, size = Size(rampEnd, fadeHeight))
    }
}
