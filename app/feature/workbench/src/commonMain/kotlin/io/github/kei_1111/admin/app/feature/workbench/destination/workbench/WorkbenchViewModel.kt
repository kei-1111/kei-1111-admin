package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.kei_1111.admin.app.core.mvi.MviViewModel
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewProfile
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewWorks
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.Work

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
internal class WorkbenchViewModel : MviViewModel<WorkbenchViewModelState, WorkbenchState, WorkbenchIntent>() {

    override fun createInitialViewModelState() = WorkbenchViewModelState(
        savedWorks = PreviewWorks,
        savedProfile = PreviewProfile,
        lastDeploy = "2h ago",
    )

    override fun createInitialState() = createInitialViewModelState().toState()

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    override fun onIntent(intent: WorkbenchIntent) {
        when (intent) {
            is WorkbenchIntent.SelectNode -> updateViewModelState { selectNode(intent.node) }

            is WorkbenchIntent.ActivateTab -> updateViewModelState {
                copy(activeTab = intent.tab, selectedNode = intent.tab.correspondingNode())
            }

            is WorkbenchIntent.CloseTab -> updateViewModelState {
                if (intent.tab.isDirty(this)) {
                    copy(closeConfirmTab = intent.tab)
                } else {
                    closeTab(intent.tab)
                }
            }

            is WorkbenchIntent.ConfirmCloseTab -> updateViewModelState {
                val tab = closeConfirmTab ?: return@updateViewModelState this
                discardDraft(tab).closeTab(tab).copy(closeConfirmTab = null)
            }

            is WorkbenchIntent.DismissCloseConfirm -> updateViewModelState { copy(closeConfirmTab = null) }

            is WorkbenchIntent.CreateWork -> updateViewModelState {
                val newWork = Work(
                    id = "work-${savedWorks.size + workDrafts.size + 1}",
                    name = "新規作品",
                    status = ContentStatus.Draft,
                    updatedAt = "now",
                )
                copy(workDrafts = workDrafts + (newWork.id to newWork))
                    .selectNode(AdminNode.WorkItem(newWork.id))
            }

            is WorkbenchIntent.UpdateWorkDraft -> updateViewModelState {
                copy(workDrafts = workDrafts + (intent.work.id to intent.work))
            }

            is WorkbenchIntent.UpdateProfileDraft -> updateViewModelState {
                copy(profileDraft = intent.profile)
            }

            // サーバー接続前のローカル動作: 編集バッファを保存済み扱いに昇格する
            is WorkbenchIntent.SaveDraft -> updateViewModelState { applyDraftsToSaved() }

            is WorkbenchIntent.RequestPublish -> updateViewModelState { copy(publishConfirmVisible = true) }

            is WorkbenchIntent.ConfirmPublish -> updateViewModelState {
                val applied = applyDraftsToSaved()
                applied.copy(
                    savedWorks = applied.savedWorks.map { it.copy(status = ContentStatus.Published) },
                    publishConfirmVisible = false,
                    lastDeploy = "just now",
                )
            }

            is WorkbenchIntent.DismissPublishConfirm -> updateViewModelState { copy(publishConfirmVisible = false) }
        }
    }
}

private fun WorkbenchViewModelState.selectNode(node: AdminNode): WorkbenchViewModelState {
    val tab = when (node) {
        is AdminNode.Works -> WorkbenchTab.WorksList
        is AdminNode.WorkItem -> WorkbenchTab.WorkEditor(node.workId)
        is AdminNode.Profile -> WorkbenchTab.ProfileEditor
        // 未実装ノードはタブを開かず選択だけ反映する
        is AdminNode.Licence, is AdminNode.DeployHistory, is AdminNode.Settings -> null
    }
    return if (tab == null) {
        copy(selectedNode = node)
    } else {
        copy(
            selectedNode = node,
            openTabs = if (openTabs.contains(tab)) openTabs else openTabs + tab,
            activeTab = tab,
        )
    }
}

private fun WorkbenchTab.correspondingNode(): AdminNode = when (this) {
    is WorkbenchTab.WorksList -> AdminNode.Works
    is WorkbenchTab.WorkEditor -> AdminNode.WorkItem(workId)
    is WorkbenchTab.ProfileEditor -> AdminNode.Profile
}

private fun WorkbenchTab.isDirty(state: WorkbenchViewModelState): Boolean = when (this) {
    is WorkbenchTab.WorksList -> false
    is WorkbenchTab.WorkEditor ->
        state.workDrafts[workId] != null && state.savedWorks.none { it == state.workDrafts[workId] }
    is WorkbenchTab.ProfileEditor ->
        state.profileDraft != null && state.profileDraft != state.savedProfile
}

private fun WorkbenchViewModelState.discardDraft(tab: WorkbenchTab): WorkbenchViewModelState = when (tab) {
    is WorkbenchTab.WorksList -> this
    is WorkbenchTab.WorkEditor -> copy(workDrafts = workDrafts - tab.workId)
    is WorkbenchTab.ProfileEditor -> copy(profileDraft = null)
}

private fun WorkbenchViewModelState.closeTab(tab: WorkbenchTab): WorkbenchViewModelState {
    val remaining = openTabs - tab
    val nextActive = if (activeTab == tab) remaining.lastOrNull() ?: WorkbenchTab.WorksList else activeTab
    return copy(
        openTabs = remaining.ifEmpty { listOf(WorkbenchTab.WorksList) },
        activeTab = nextActive,
        selectedNode = nextActive.correspondingNode(),
    )
}

private fun WorkbenchViewModelState.applyDraftsToSaved(): WorkbenchViewModelState {
    val updated = savedWorks.map { workDrafts[it.id] ?: it } +
        workDrafts.values.filter { draft -> savedWorks.none { it.id == draft.id } }
    return copy(
        savedWorks = updated,
        savedProfile = profileDraft ?: savedProfile,
        workDrafts = emptyMap(),
        profileDraft = null,
    )
}
