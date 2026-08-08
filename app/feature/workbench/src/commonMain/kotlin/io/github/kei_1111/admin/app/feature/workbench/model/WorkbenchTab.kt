package io.github.kei_1111.admin.app.feature.workbench.model

/** メインエリアのピル型タブ。複数コンテンツを同時に開ける。 */
internal sealed interface WorkbenchTab {
    data object WorksList : WorkbenchTab
    data class WorkEditor(val workId: String) : WorkbenchTab
    data object ProfileEditor : WorkbenchTab
}
