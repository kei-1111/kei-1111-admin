package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.State
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work

internal data class WorkbenchState(
    /** 保存済み(下書き)の内容に編集バッファを重ねた、画面表示用の Works。 */
    val works: List<Work> = emptyList(),
    val profile: AdminProfile = AdminProfile(),
    val selectedNode: AdminNode = AdminNode.Works,
    val openTabs: List<WorkbenchTab> = listOf(WorkbenchTab.WorksList),
    val activeTab: WorkbenchTab = WorkbenchTab.WorksList,
    /** 未保存の変更があるタブ数(タイトルバー・ステータスバー表示用)。 */
    val unsavedCount: Int = 0,
    /** 未保存の変更を持つ Work id 集合(ツリーの黄ドット用)。 */
    val unsavedWorkIds: Set<String> = emptySet(),
    val profileUnsaved: Boolean = false,
    val saving: Boolean = false,
    val publishing: Boolean = false,
    val lastDeploy: String = "",
    /** 未保存タブを閉じようとして確認待ちのタブ。 */
    val closeConfirmTab: WorkbenchTab? = null,
    val publishConfirmVisible: Boolean = false,
    val syncError: Boolean = false,
    val loading: Boolean = true,
) : State
