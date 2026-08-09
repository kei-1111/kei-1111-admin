package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import kei_1111_admin.app.core.designsystem.generated.resources.Res
import kei_1111_admin.app.core.designsystem.generated.resources.ic_link_github
import kei_1111_admin.app.core.designsystem.generated.resources.ic_link_note
import kei_1111_admin.app.core.designsystem.generated.resources.ic_link_note_light
import kei_1111_admin.app.core.designsystem.generated.resources.ic_link_qiita
import kei_1111_admin.app.core.designsystem.generated.resources.ic_link_x
import org.jetbrains.compose.resources.DrawableResource

fun LinkServiceType.icon(colors: KeiColorScheme): DrawableResource = when (this) {
    LinkServiceType.GitHub -> Res.drawable.ic_link_github
    LinkServiceType.X -> Res.drawable.ic_link_x
    LinkServiceType.Qiita -> Res.drawable.ic_link_qiita
    // ダークは note 公式ロゴ（角丸スクエア）。白抜きスクエアのロゴは明るい背景では浮いて見えるため、
    // ライトは "n" グリフのみのシンプル版にする
    LinkServiceType.Note ->
        if (colors.isDark) Res.drawable.ic_link_note else Res.drawable.ic_link_note_light
}

fun LinkServiceType.brandColor(colors: KeiColorScheme): Color = when (this) {
    LinkServiceType.GitHub, LinkServiceType.X, LinkServiceType.Note -> colors.textPrimary
    LinkServiceType.Qiita -> colors.brandQiita
}
