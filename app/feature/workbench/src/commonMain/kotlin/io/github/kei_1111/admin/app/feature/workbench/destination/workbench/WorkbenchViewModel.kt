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
import io.github.kei_1111.admin.app.core.domain.usecase.GetPortfolioContributionsUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetPortfolioProfileUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetWorksDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.PublishContentUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveWorksDraftUseCase
import io.github.kei_1111.admin.app.core.mvi.MviViewModel
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
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
    private val getPortfolioProfile: GetPortfolioProfileUseCase,
    private val getPortfolioContributions: GetPortfolioContributionsUseCase,
) : MviViewModel<WorkbenchViewModelState, WorkbenchState, WorkbenchIntent>() {

    init {
        // トークンが届く(サインイン完了)まで待ってから draft を読む
        viewModelScope.launch {
            AdminAuthController.idToken.filterNotNull().first()
            loadContent()
            loadPreviewData()
        }
    }

    override fun createInitialViewModelState() = WorkbenchViewModelState()

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
                    id = nextWorkId(),
                    name = "新規作品",
                    status = ContentStatus.Draft,
                )
                copy(workDrafts = workDrafts + (newWork.id to newWork))
                    .selectNode(AdminNode.WorkItem(newWork.id))
            }

            is WorkbenchIntent.RequestDeleteWork -> updateViewModelState {
                copy(deleteConfirmWorkId = intent.workId)
            }

            is WorkbenchIntent.ConfirmDeleteWork -> updateViewModelState {
                val workId = deleteConfirmWorkId ?: return@updateViewModelState this
                copy(
                    deletedWorkIds = deletedWorkIds + workId,
                    workDrafts = workDrafts - workId,
                    deleteConfirmWorkId = null,
                ).closeTab(WorkbenchTab.WorkEditor(workId))
            }

            is WorkbenchIntent.DismissDeleteConfirm -> updateViewModelState { copy(deleteConfirmWorkId = null) }

            is WorkbenchIntent.UpdateWorkDraft -> updateViewModelState {
                copy(workDrafts = workDrafts + (intent.work.id to intent.work))
            }

            is WorkbenchIntent.UpdateProfileDraft -> updateViewModelState {
                copy(profileDraft = intent.profile)
            }

            is WorkbenchIntent.SaveDraft -> maybeWarnThenPersist(alsoPublish = false)

            is WorkbenchIntent.RetryPreview -> viewModelScope.launch { loadPreviewData() }

            is WorkbenchIntent.RequestPublish -> updateViewModelState {
                if (saving) this else copy(publishConfirmVisible = true)
            }

            is WorkbenchIntent.ConfirmPublish -> {
                updateViewModelState { copy(publishConfirmVisible = false) }
                maybeWarnThenPersist(alsoPublish = true)
            }

            is WorkbenchIntent.ConfirmLanguageOutdated -> {
                val warning = _viewModelState.value.languageOutdatedWarning
                updateViewModelState { copy(languageOutdatedWarning = null) }
                if (warning != null) persistDrafts(alsoPublish = warning.alsoPublish)
            }

            is WorkbenchIntent.DismissLanguageOutdated -> updateViewModelState { copy(languageOutdatedWarning = null) }

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
                syncError = if (works == null || profile == null) SyncErrorKind.Load else null,
                loading = false,
            )
        }
    }

    /** Preview カード用の GitHub 由来データ。失敗しても編集は継続できる(カード側が失敗表示を持つ)。 */
    private suspend fun loadPreviewData() {
        val fetchedProfile = recoverOrElse({ getPortfolioProfile() }) { null }
        val fetchedContributions = recoverOrElse({ getPortfolioContributions() }) { null }
        updateViewModelState {
            copy(
                portfolioProfile = fetchedProfile ?: portfolioProfile,
                contributions = fetchedContributions ?: contributions,
                contributionsFailed = fetchedContributions == null,
            )
        }
    }

    /** 片方の言語だけ変更された項目があれば、保存前に確認を挟む。 */
    private fun maybeWarnThenPersist(alsoPublish: Boolean) {
        val outdated = _viewModelState.value.languageOutdatedItems()
        if (outdated.isEmpty()) {
            persistDrafts(alsoPublish)
        } else {
            updateViewModelState {
                copy(languageOutdatedWarning = WorkbenchViewModelState.LanguageOutdatedWarning(outdated, alsoPublish))
            }
        }
    }

    /** 下書き保存(+公開)。保存が失敗した場合は編集バッファを保持したまま syncError を立てる。 */
    private fun persistDrafts(alsoPublish: Boolean) {
        // 保存中の二重発火(連打・保存と公開の同時実行)は無視する
        if (_viewModelState.value.saving) return
        viewModelScope.launch {
            updateViewModelState { copy(saving = true, publishing = alsoPublish) }
            val snapshot = _viewModelState.value
            val mergedWorks = (
                snapshot.savedWorks.map { snapshot.workDrafts[it.id] ?: it } +
                    snapshot.workDrafts.values.filter { draft -> snapshot.savedWorks.none { it.id == draft.id } }
                ).filterNot { it.id in snapshot.deletedWorkIds }
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
                        // 保存中に入力された編集(スナップショットとの差分)は消さずに残す
                        workDrafts = workDrafts.filter { (id, draft) -> draft != snapshot.workDrafts[id] },
                        profileDraft = profileDraft?.takeIf { it != snapshot.profileDraft },
                        deletedWorkIds = deletedWorkIds - snapshot.deletedWorkIds,
                        saving = false,
                        publishing = false,
                        syncError = if (alsoPublish && meta == null) SyncErrorKind.Publish else null,
                        lastDeploy = meta?.lastPublishedAt?.toDeployDisplay() ?: lastDeploy,
                    )
                } else {
                    copy(saving = false, publishing = false, syncError = SyncErrorKind.Save)
                }
            }
        }
    }
}

/** ISO-8601 を `2026-08-08 12:34` 形式へ落とす表示用整形。 */
private fun String.toDeployDisplay(): String = take(DEPLOY_DISPLAY_LENGTH).replace("T", " ")

/** 既存 ID の最大連番から採番する。破棄で件数が減っても既存 ID とは衝突しない。 */
private fun WorkbenchViewModelState.nextWorkId(): String {
    val existing = savedWorks.map { it.id } + workDrafts.keys + deletedWorkIds
    val next = existing.mapNotNull { it.removePrefix("work-").toIntOrNull() }.maxOrNull()?.plus(1) ?: 1
    return generateSequence(next) { it + 1 }.map { "work-$it" }.first { it !in existing }
}

private fun WorkbenchViewModelState.selectNode(node: AdminNode): WorkbenchViewModelState {
    val tab = when (node) {
        is AdminNode.Works -> WorkbenchTab.WorksList
        is AdminNode.WorkItem -> WorkbenchTab.WorkEditor(node.workId)
        is AdminNode.Profile -> WorkbenchTab.ProfileEditor
        // 未実装ノードはタブを開かず選択だけ反映する
        is AdminNode.DeployHistory, is AdminNode.Settings -> null
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

/** 片方の言語だけ変更された項目を列挙する(新規作品の片言語未入力も含む)。 */
private fun WorkbenchViewModelState.languageOutdatedItems(): List<WorkbenchViewModelState.OutdatedItem> = buildList {
    workDrafts.values.forEach { draft ->
        workOutdatedItem(draft, savedWorks.firstOrNull { it.id == draft.id })?.let(::add)
    }
    profileDraft?.let { draft ->
        outdatedItemOf(
            name = "Profile",
            jaChanged = draft.displayName != savedProfile.displayName,
            enChanged = draft.displayNameEn != savedProfile.displayNameEn,
        )?.let(::add)
    }
}

private fun workOutdatedItem(draft: Work, saved: Work?): WorkbenchViewModelState.OutdatedItem? {
    val jaChanged = if (saved == null) {
        draft.about.isNotBlank() || draft.roles.any { it.isNotBlank() }
    } else {
        draft.about != saved.about || draft.roles != saved.roles
    }
    val enChanged = if (saved == null) {
        draft.aboutEn.isNotBlank() || draft.rolesEn.any { it.isNotBlank() }
    } else {
        draft.aboutEn != saved.aboutEn || draft.rolesEn != saved.rolesEn
    }
    return outdatedItemOf(draft.name, jaChanged, enChanged)
}

private fun outdatedItemOf(
    name: String,
    jaChanged: Boolean,
    enChanged: Boolean,
): WorkbenchViewModelState.OutdatedItem? = when {
    jaChanged && !enChanged -> WorkbenchViewModelState.OutdatedItem(name, staleEnglish = true)
    enChanged && !jaChanged -> WorkbenchViewModelState.OutdatedItem(name, staleEnglish = false)
    else -> null
}
