package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.ViewModelState
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

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
    val uploadingScreenshot: Boolean = false,
    val publishing: Boolean = false,
    val lastDeploy: String = "",
    val closeConfirmTab: WorkbenchTab? = null,
    val publishConfirmVisible: Boolean = false,
    /** 次回保存で下書きから取り除く作品 id。保存されるまでは未保存の変更として数える。 */
    val deletedWorkIds: Set<String> = emptySet(),
    val deleteConfirmWorkId: String? = null,
    val syncError: SyncErrorKind? = null,
    val loading: Boolean = true,
    val portfolioProfile: GitHubProfile? = null,
    val contributions: ContributionCalendar? = null,
    val contributionsFailed: Boolean = false,
    /** 片方の言語だけ変更された項目。null 以外なら保存/公開前の確認ダイアログを出す。 */
    val languageOutdatedWarning: LanguageOutdatedWarning? = null,
) : ViewModelState<WorkbenchState> {

    /** 片方の言語が未更新のまま進めようとした保存操作の内容。 */
    data class LanguageOutdatedWarning(
        val items: List<OutdatedItem>,
        val alsoPublish: Boolean,
    )

    /** [staleEnglish] が true なら英語側、false なら日本語側が未更新。 */
    data class OutdatedItem(
        val name: String,
        val staleEnglish: Boolean,
    )

    private fun dirtyWorkIds(): Set<String> = workDrafts
        .filterValues { draft -> savedWorks.none { it.id == draft.id && it == draft } }
        .keys

    private fun isProfileDirty(): Boolean = profileDraft != null && profileDraft != savedProfile

    override fun toState(): WorkbenchState {
        val dirtyIds = dirtyWorkIds()
        val profileDirty = isProfileDirty()
        val mergedWorks = (
            savedWorks.map { workDrafts[it.id] ?: it } +
                workDrafts.values.filter { draft -> savedWorks.none { it.id == draft.id } }
            ).filterNot { it.id in deletedWorkIds }
        return WorkbenchState(
            works = mergedWorks,
            profile = profileDraft ?: savedProfile,
            selectedNode = selectedNode,
            openTabs = openTabs,
            activeTab = activeTab,
            unsavedCount = dirtyIds.size + deletedWorkIds.size + (if (profileDirty) 1 else 0),
            unsavedWorkIds = dirtyIds,
            profileUnsaved = profileDirty,
            saving = saving,
            uploadingScreenshot = uploadingScreenshot,
            publishing = publishing,
            lastDeploy = lastDeploy,
            closeConfirmTab = closeConfirmTab,
            publishConfirmVisible = publishConfirmVisible,
            deleteConfirmWork = deleteConfirmWorkId?.let { id -> mergedWorks.firstOrNull { it.id == id } },
            syncError = syncError,
            loading = loading,
            portfolioProfile = portfolioProfile,
            contributions = contributions,
            contributionsFailed = contributionsFailed,
            languageOutdatedWarning = languageOutdatedWarning,
        )
    }
}
