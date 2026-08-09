package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.State
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

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
    val uploadingScreenshot: Boolean = false,
    val publishing: Boolean = false,
    val lastDeploy: String = "",
    /** 未保存タブを閉じようとして確認待ちのタブ。 */
    val closeConfirmTab: WorkbenchTab? = null,
    val publishConfirmVisible: Boolean = false,
    /** 削除確認待ちの作品(null 以外でダイアログ表示)。 */
    val deleteConfirmWork: Work? = null,
    val syncError: SyncErrorKind? = null,
    val loading: Boolean = true,
    /** Preview カード用の GitHub 由来データ(本体 API プロキシ経由)。null は未取得。 */
    val portfolioProfile: GitHubProfile? = null,
    val contributions: ContributionCalendar? = null,
    val contributionsFailed: Boolean = false,
    val languageOutdatedWarning: WorkbenchViewModelState.LanguageOutdatedWarning? = null,
) : State

/** どの操作の同期に失敗したか(ステータスバーの文言出し分け用)。 */
internal enum class SyncErrorKind { Load, Save, Publish, Upload }
