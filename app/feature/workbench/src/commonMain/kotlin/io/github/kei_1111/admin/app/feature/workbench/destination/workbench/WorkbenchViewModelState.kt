package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.ViewModelState
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.PublishDiff
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubIssues
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

internal data class WorkbenchViewModelState(
    /** 保存済み(下書き保存された)内容。サーバー接続後はサーバーの draft と一致する。 */
    val savedWorks: List<Work> = emptyList(),
    val savedProfile: AdminProfile = AdminProfile(),
    /** 編集バッファ。保存済みと異なるエントリが「未保存の変更」。 */
    val workDrafts: Map<String, Work> = emptyMap(),
    val profileDraft: AdminProfile? = null,
    val savedTerminal: TerminalCommandsContent = TerminalCommandsContent(),
    val terminalDraft: TerminalCommandsContent? = null,
    val savedReadme: ReadmeContent = ReadmeContent(),
    val readmeDraft: ReadmeContent? = null,
    val selectedNode: AdminNode = AdminNode.Works,
    val openTabs: List<WorkbenchTab> = listOf(WorkbenchTab.WorksList),
    val activeTab: WorkbenchTab = WorkbenchTab.WorksList,
    val saving: Boolean = false,
    val uploadingScreenshot: Boolean = false,
    val publishing: Boolean = false,
    val lastDeploy: String = "",
    val closeConfirmTab: WorkbenchTab? = null,
    val publishConfirmVisible: Boolean = false,
    /** 公開確認ダイアログに出す差分。null はロード中(確定ボタン無効)。 */
    val publishDiff: PublishDiff? = null,
    val publishDiffFailed: Boolean = false,
    /** 破棄確認待ちのドキュメント。 */
    val discardConfirmDocument: ContentDocument? = null,
    /** 単一作品の編集バッファ破棄の確認待ち。 */
    val revertConfirmWorkId: String? = null,
    /** 次回保存で下書きから取り除く作品 id。保存されるまでは未保存の変更として数える。 */
    val deletedWorkIds: Set<String> = emptySet(),
    val deleteConfirmWorkId: String? = null,
    val syncError: SyncErrorKind? = null,
    val loading: Boolean = true,
    /** 4ドキュメント(works/profile/terminal/readme)の初回読み込みが完了し、保存操作が安全になったか。 */
    val contentLoaded: Boolean = false,
    val portfolioProfile: GitHubProfile? = null,
    val contributions: ContributionCalendar? = null,
    val contributionsFailed: Boolean = false,
    val portfolioIssues: GitHubIssues? = null,
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

    private fun isTerminalDirty(): Boolean = terminalDraft != null && terminalDraft != savedTerminal

    private fun isReadmeDirty(): Boolean = readmeDraft != null && readmeDraft != savedReadme

    override fun toState(): WorkbenchState {
        val dirtyIds = dirtyWorkIds()
        val profileDirty = isProfileDirty()
        val extraDirty = (if (isTerminalDirty()) 1 else 0) + (if (isReadmeDirty()) 1 else 0)
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
            unsavedCount = dirtyIds.size + deletedWorkIds.size + extraDirty + (if (profileDirty) 1 else 0),
            unsavedWorkIds = dirtyIds,
            profileUnsaved = profileDirty,
            terminal = terminalDraft ?: savedTerminal,
            terminalUnsaved = isTerminalDirty(),
            readme = readmeDraft ?: savedReadme,
            readmeUnsaved = isReadmeDirty(),
            saving = saving,
            uploadingScreenshot = uploadingScreenshot,
            publishing = publishing,
            lastDeploy = lastDeploy,
            closeConfirmTab = closeConfirmTab,
            publishConfirmVisible = publishConfirmVisible,
            publishDiff = publishDiff,
            publishDiffFailed = publishDiffFailed,
            discardConfirmDocument = discardConfirmDocument,
            revertConfirmWork = revertConfirmWorkId?.let { id -> mergedWorks.firstOrNull { it.id == id } },
            deleteConfirmWork = deleteConfirmWorkId?.let { id -> mergedWorks.firstOrNull { it.id == id } },
            syncError = syncError,
            loading = loading,
            contentLoaded = contentLoaded,
            portfolioProfile = portfolioProfile,
            contributions = contributions,
            contributionsFailed = contributionsFailed,
            portfolioIssues = portfolioIssues,
            languageOutdatedWarning = languageOutdatedWarning,
        )
    }
}
