package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.mvi.Intent
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.Work

internal sealed interface WorkbenchIntent : Intent {
    data class SelectNode(val node: AdminNode) : WorkbenchIntent
    data class ActivateTab(val tab: WorkbenchTab) : WorkbenchIntent
    data class CloseTab(val tab: WorkbenchTab) : WorkbenchIntent
    data object ConfirmCloseTab : WorkbenchIntent
    data object DismissCloseConfirm : WorkbenchIntent
    data object CreateWork : WorkbenchIntent
    data class AddScreenshot(val workId: String) : WorkbenchIntent
    data class AddWorkIcon(val workId: String) : WorkbenchIntent
    data class RequestDeleteWork(val workId: String) : WorkbenchIntent
    data object ConfirmDeleteWork : WorkbenchIntent
    data object DismissDeleteConfirm : WorkbenchIntent
    data class UpdateWorkDraft(val work: Work) : WorkbenchIntent
    data class UpdateProfileDraft(val profile: AdminProfile) : WorkbenchIntent
    data class UpdateTerminalDraft(val content: TerminalCommandsContent) : WorkbenchIntent
    data class UpdateReadmeDraft(val content: ReadmeContent) : WorkbenchIntent
    data object AddProfileAvatar : WorkbenchIntent
    data object SyncPinnedRepos : WorkbenchIntent
    data object SaveDraft : WorkbenchIntent
    data object RetryLoad : WorkbenchIntent
    data object RetryPreview : WorkbenchIntent
    data object ConfirmLanguageOutdated : WorkbenchIntent
    data object DismissLanguageOutdated : WorkbenchIntent
    data object RequestPublish : WorkbenchIntent
    data class RequestDiscardDraft(val document: ContentDocument) : WorkbenchIntent
    data object ConfirmDiscardDraft : WorkbenchIntent
    data object DismissDiscardConfirm : WorkbenchIntent
    data class RequestRevertWork(val workId: String) : WorkbenchIntent
    data object ConfirmRevertWork : WorkbenchIntent
    data object DismissRevertConfirm : WorkbenchIntent
    data object ConfirmPublish : WorkbenchIntent
    data object DismissPublishConfirm : WorkbenchIntent
}
