package io.github.kei_1111.admin.app.core.designsystem.theme

import kei_1111_admin.app.core.designsystem.generated.resources.Res
import kei_1111_admin.app.core.designsystem.generated.resources.img_profile_icon
import org.jetbrains.compose.resources.DrawableResource

/**
 * `publicResClass = true` で Res 全体を公開する代わりに、
 * 複数 feature が参照する公開面をこのプロパティ1つに絞る。
 */
val ProfileIconImage: DrawableResource
    get() = Res.drawable.img_profile_icon
