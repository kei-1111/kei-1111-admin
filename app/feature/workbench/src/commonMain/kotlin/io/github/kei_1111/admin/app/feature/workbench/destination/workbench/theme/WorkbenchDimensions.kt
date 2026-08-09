@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data object WorkbenchDimensions {
    val DeskPadding = 10.dp
    val IslandGap = 7.dp
    val TreeWidth = 248.dp
    val PreviewWidth = 248.dp

    val TitleBarIconSize = 18.dp
    val ChromeIconSize = 16.dp
    val ChromeLabelFontSize = 12.sp
    val ChromePillSize = 30.dp

    val TreeRowHeight = 24.dp
    val TreeLeftInset = 6.dp
    val TreeChevronSize = 16.dp
    val TreeIconSize = 16.dp
    val TreeChevronGap = 3.dp
    val TreeIconLabelGap = 6.dp
    val TreeIndentStep = TreeChevronSize + TreeChevronGap

    val TabHeight = 28.dp
    val IslandPadding = 12.dp
    val SectionGap = 18.dp
    val FieldGap = 10.dp
    val FieldHeight = 30.dp
    val LabelFontSize = 10.sp
    val FieldFontSize = 13.sp
    val TableRowHeight = 48.dp

    /** スクリーンショットサムネイル(9:19.5)。 */
    val ScreenshotWidth = 72.dp
    val ScreenshotHeight = 156.dp

    const val FormWeight = 1.25f

    /** Preview ペインの縦横比。本家カード(280x600)の比率で高さから幅を決める。 */
    const val PreviewCardAspectRatio = 280f / 600f

    // 本体サイト ProfileDimensions から移植した Preview カード寸法
    val GitHubCardWidth = 280.dp
    val GitHubCardHeight = 600.dp
    val GitHubCardPadding = 20.dp
    val GitHubCardSectionGap = 14.dp
    val WorksCardWidth = 280.dp
    val WorksCardHeight = 600.dp
    val WorksCardPadding = 18.dp

    /** Works 詳細シートの高さ(カード高さに対する割合)。 */
    const val WorksSheetHeightFraction = 0.76f
}
