@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class KeiShapes(
    val island: Shape,
    val pill: Shape,
    val chip: Shape,
    val row: Shape,
    val card: Shape,
    val badge: Shape,

    val githubItem: Shape,
    val linkTile: Shape,

    /** ライセンスシート上角（LicenseSheetOverlay で使用）。 */
    val sheet: Shape,
)

val keiShapes = KeiShapes(
    island = RoundedCornerShape(12.dp),
    pill = RoundedCornerShape(7.dp),
    chip = RoundedCornerShape(4.dp),
    row = RoundedCornerShape(7.dp),
    card = RoundedCornerShape(10.dp),
    badge = RoundedCornerShape(3.dp),

    githubItem = RoundedCornerShape(8.dp),
    linkTile = RoundedCornerShape(10.dp),

    sheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
)
