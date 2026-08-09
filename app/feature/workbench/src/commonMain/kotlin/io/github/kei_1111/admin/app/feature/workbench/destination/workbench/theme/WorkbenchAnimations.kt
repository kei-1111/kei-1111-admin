@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme

/** 本体サイトの ProfileAnimations から、移植した Preview カードが使う値を引き継ぐ。 */
internal data object WorkbenchAnimations {
    /** RepeatMode.Reverse で1往復 2400ms */
    const val ContributionsPulseMillis = 1200

    const val SheetTransitionMillis = 300

    const val ContentCrossfadeMillis = 200
}
