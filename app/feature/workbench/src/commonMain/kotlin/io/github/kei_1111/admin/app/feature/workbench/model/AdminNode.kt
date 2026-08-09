package io.github.kei_1111.admin.app.feature.workbench.model

/** ナビツリーの選択対象。 */
internal sealed interface AdminNode {
    data object Works : AdminNode
    data class WorkItem(val workId: String) : AdminNode
    data object Profile : AdminNode
    data object Readme : AdminNode
    data object Terminal : AdminNode
    data object DeployHistory : AdminNode
    data object Settings : AdminNode
}
