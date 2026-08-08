package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.app.core.common.coroutines.recoverOrElse
import io.github.kei_1111.admin.app.core.domain.usecase.GetContentMetaUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetWorksDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.PublishContentUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveWorksDraftUseCase
import io.github.kei_1111.admin.app.core.mvi.MviViewModel
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewProfile
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewWorks
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val DEPLOY_DISPLAY_LENGTH = 16

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Suppress("LongParameterList")
internal class WorkbenchViewModel(
    private val getWorksDraft: GetWorksDraftUseCase,
    private val saveWorksDraft: SaveWorksDraftUseCase,
    private val getProfileDraft: GetProfileDraftUseCase,
    private val saveProfileDraft: SaveProfileDraftUseCase,
    private val getContentMeta: GetContentMetaUseCase,
    private val publishContent: PublishContentUseCase,
) : MviViewModel<WorkbenchViewModelState, WorkbenchState, WorkbenchIntent>() {

    init {
        // トークンが届く(サインイン完了)まで待ってから draft を読む
        viewModelScope.launch {
            AdminAuthController.idToken.filterNotNull().first()
            loadContent()
        }
    }

    // フィクスチャはサインイン前・API 不通時のフォールバック表示。成功ロードで置き換わる
    override fun createInitialViewModelState() = WorkbenchViewModelState(
        savedWorks = PreviewWorks,
        savedProfile = PreviewProfile,
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

            is WorkbenchIntent.SaveDraft -> persistDrafts(alsoPublish = false)

            is WorkbenchIntent.RequestPublish -> updateViewModelState { copy(publishConfirmVisible = true) }

            is WorkbenchIntent.ConfirmPublish -> {
                updateViewModelState { copy(publishConfirmVisible = false) }
                persistDrafts(alsoPublish = true)
            }

            is WorkbenchIntent.DismissPublishConfirm -> updateViewModelState { copy(publishConfirmVisible = false) }
        }
    }

    private suspend fun loadContent() {
        val works = recoverOrElse({ getWorksDraft() }) { null }
        val profile = recoverOrElse({ getProfileDraft() }) { null }
        val meta = recoverOrElse({ getContentMeta() }) { null }
        updateViewModelState {
            copy(
                savedWorks = works?.works ?: savedWorks,
                savedProfile = profile ?: savedProfile,
                lastDeploy = meta?.lastPublishedAt?.takeIf { it.isNotEmpty() }?.toDeployDisplay() ?: lastDeploy,
                syncError = works == null || profile == null,
            )
        }
    }

    /** 下書き保存(+公開)。保存が失敗した場合は編集バッファを保持したまま syncError を立てる。 */
    private fun persistDrafts(alsoPublish: Boolean) {
        viewModelScope.launch {
            updateViewModelState { copy(saving = true, publishing = alsoPublish) }
            val snapshot = _viewModelState.value
            val mergedWorks = snapshot.savedWorks.map { snapshot.workDrafts[it.id] ?: it } +
                snapshot.workDrafts.values.filter { draft -> snapshot.savedWorks.none { it.id == draft.id } }
            val mergedProfile = snapshot.profileDraft ?: snapshot.savedProfile

            val savedWorks = recoverOrElse({ saveWorksDraft(WorksContent(works = mergedWorks)) }) { null }
            val savedProfile = recoverOrElse({ saveProfileDraft(mergedProfile) }) { null }
            val saveSucceeded = savedWorks != null && savedProfile != null
            val meta = if (alsoPublish && saveSucceeded) recoverOrElse({ publishContent() }) { null } else null

            updateViewModelState {
                if (saveSucceeded) {
                    copy(
                        savedWorks = savedWorks.works,
                        savedProfile = savedProfile,
                        workDrafts = emptyMap(),
                        profileDraft = null,
                        saving = false,
                        publishing = false,
                        syncError = alsoPublish && meta == null,
                        lastDeploy = meta?.lastPublishedAt?.toDeployDisplay() ?: lastDeploy,
                    )
                } else {
                    copy(saving = false, publishing = false, syncError = true)
                }
            }
        }
    }
}

/** ISO-8601 を `2026-08-08 12:34` 形式へ落とす表示用整形。 */
private fun String.toDeployDisplay(): String = take(DEPLOY_DISPLAY_LENGTH).replace("T", " ")

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
