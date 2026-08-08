package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.ViewModelState
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work

internal data class WorkbenchViewModelState(
    /** 保存済み(下書き保存された)内容。サーバー接続後はサーバーの draft と一致する。 */
    val savedWorks: List<Work> = emptyList(),
    val savedProfile: AdminProfile = AdminProfile(),
    /** 編集バッファ。保存済みと異なるエントリが「未保存の変更」。 */
    val workDrafts: Map<String, Work> = emptyMap(),
    val profileDraft: AdminProfile? = null,
    val selectedNode: AdminNode = AdminNode.Works,
    val openTabs: List<WorkbenchTab> = listOf(WorkbenchTab.WorksList),
    val activeTab: WorkbenchTab = WorkbenchTab.WorksList,
    val saving: Boolean = false,
    val publishing: Boolean = false,
    val lastDeploy: String = "",
    val closeConfirmTab: WorkbenchTab? = null,
    val publishConfirmVisible: Boolean = false,
    val syncError: Boolean = false,
    val loading: Boolean = true,
) : ViewModelState<WorkbenchState> {

    private fun dirtyWorkIds(): Set<String> = workDrafts
        .filterValues { draft -> savedWorks.none { it.id == draft.id && it == draft } }
        .keys

    private fun isProfileDirty(): Boolean = profileDraft != null && profileDraft != savedProfile

    override fun toState(): WorkbenchState {
        val dirtyIds = dirtyWorkIds()
        val profileDirty = isProfileDirty()
        val mergedWorks = savedWorks.map { workDrafts[it.id] ?: it } +
            workDrafts.values.filter { draft -> savedWorks.none { it.id == draft.id } }
        return WorkbenchState(
            works = mergedWorks,
            profile = profileDraft ?: savedProfile,
            selectedNode = selectedNode,
            openTabs = openTabs,
            activeTab = activeTab,
            unsavedCount = dirtyIds.size + (if (profileDirty) 1 else 0),
            unsavedWorkIds = dirtyIds,
            profileUnsaved = profileDirty,
            saving = saving,
            publishing = publishing,
            lastDeploy = lastDeploy,
            closeConfirmTab = closeConfirmTab,
            publishConfirmVisible = publishConfirmVisible,
            syncError = syncError,
            loading = loading,
        )
    }
}
