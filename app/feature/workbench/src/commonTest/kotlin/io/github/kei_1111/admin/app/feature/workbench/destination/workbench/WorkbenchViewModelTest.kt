package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.app.core.domain.usecase.GetContentMetaUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetPortfolioContributionsUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetPortfolioProfileUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetReadmeDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetTerminalDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetWorksDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.PickImageUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.PublishContentUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveReadmeDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveTerminalDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveWorksDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.UploadProfileImageUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.UploadWorkImageUseCase
import io.github.kei_1111.admin.app.core.testing.ViewModelTestBase
import io.github.kei_1111.admin.app.core.testing.startCollecting
import io.github.kei_1111.admin.app.core.utils.PickedImageFile
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewContributionCalendar
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewGitHubProfile
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.ReadmeInline
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val SeedWorks = listOf(
    Work(id = "withmo", name = "withmo", status = ContentStatus.Published),
    Work(id = "timelog", name = "TimeLog", status = ContentStatus.Draft),
)
private val SeedProfile = AdminProfile(displayName = "けい")

/** UseCase 境界のフェイク。works/profile/meta を in-memory で持つ。 */
private class FakeAdminContentRepository(
    var works: WorksContent = WorksContent(works = SeedWorks),
    var profile: AdminProfile = SeedProfile,
    var terminal: TerminalCommandsContent = TerminalCommandsContent(),
    var readme: ReadmeContent = ReadmeContent(),
    var meta: ContentMeta = ContentMeta(),
    var failing: Boolean = false,
    var previewFailing: Boolean = false,
) {
    var publishCount = 0

    fun failIfRequested() {
        check(!failing) { "fake failure" }
    }
}

private fun viewModel(
    repository: FakeAdminContentRepository,
    pickedImage: PickedImageFile? = null,
) = WorkbenchViewModel(
    getWorksDraft = object : GetWorksDraftUseCase {
        override suspend fun invoke(): WorksContent {
            repository.failIfRequested()
            return repository.works
        }
    },
    saveWorksDraft = object : SaveWorksDraftUseCase {
        override suspend fun invoke(content: WorksContent): WorksContent {
            repository.failIfRequested()
            repository.works = content
            return content
        }
    },
    getProfileDraft = object : GetProfileDraftUseCase {
        override suspend fun invoke(): AdminProfile {
            repository.failIfRequested()
            return repository.profile
        }
    },
    saveProfileDraft = object : SaveProfileDraftUseCase {
        override suspend fun invoke(profile: AdminProfile): AdminProfile {
            repository.failIfRequested()
            repository.profile = profile
            return profile
        }
    },
    getContentMeta = object : GetContentMetaUseCase {
        override suspend fun invoke(): ContentMeta {
            repository.failIfRequested()
            return repository.meta
        }
    },
    publishContent = object : PublishContentUseCase {
        override suspend fun invoke(): ContentMeta {
            repository.failIfRequested()
            repository.publishCount += 1
            repository.meta = ContentMeta(lastPublishedAt = "2026-08-08T12:34:56Z")
            return repository.meta
        }
    },
    getPortfolioProfile = object : GetPortfolioProfileUseCase {
        override suspend fun invoke(): GitHubProfile {
            repository.failIfRequested()
            return PreviewGitHubProfile
        }
    },
    getPortfolioContributions = object : GetPortfolioContributionsUseCase {
        override suspend fun invoke(): ContributionCalendar {
            check(!repository.previewFailing) { "fake preview failure" }
            repository.failIfRequested()
            return PreviewContributionCalendar
        }
    },
    object : PickImageUseCase {
        override suspend fun invoke(): PickedImageFile? = pickedImage
    },
    object : UploadWorkImageUseCase {
        override suspend fun invoke(workId: String, file: PickedImageFile): String =
            "images/works/$workId/uploaded-${file.name}"
    },
    uploadProfileImage = object : UploadProfileImageUseCase {
        override suspend fun invoke(file: PickedImageFile): String = "images/profile/uploaded-${file.name}"
    },
    getTerminalDraft = object : GetTerminalDraftUseCase {
        override suspend fun invoke(): TerminalCommandsContent {
            repository.failIfRequested()
            return repository.terminal
        }
    },
    saveTerminalDraft = object : SaveTerminalDraftUseCase {
        override suspend fun invoke(content: TerminalCommandsContent): TerminalCommandsContent {
            repository.failIfRequested()
            repository.terminal = content
            return content
        }
    },
    getReadmeDraft = object : GetReadmeDraftUseCase {
        override suspend fun invoke(): ReadmeContent {
            repository.failIfRequested()
            return repository.readme
        }
    },
    saveReadmeDraft = object : SaveReadmeDraftUseCase {
        override suspend fun invoke(content: ReadmeContent): ReadmeContent {
            repository.failIfRequested()
            repository.readme = content
            return content
        }
    },
)

class WorkbenchViewModelTest : ViewModelTestBase() {

    private fun signIn() = AdminAuthController.receiveIdToken("test-token")

    @Test
    fun loadsDraftContentAfterSignIn() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(SeedWorks, state.works)
        assertEquals(SeedProfile, state.profile)
        assertNull(state.syncError)
    }

    @Test
    fun selectingWorkNodeOpensAndActivatesItsEditorTab() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem("withmo")))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(WorkbenchTab.WorkEditor("withmo"), state.activeTab)
        assertTrue(state.openTabs.contains(WorkbenchTab.WorkEditor("withmo")))
    }

    @Test
    fun updatingWorkDraftMarksItUnsaved() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()

        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited")))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(1, state.unsavedCount)
        assertTrue(work.id in state.unsavedWorkIds)
    }

    @Test
    fun savingDraftPersistsToRepositoryAndClearsUnsaved() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited", aboutEn = "edited en")))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertNull(state.syncError)
        assertEquals("edited", repository.works.works.first { it.id == work.id }.about)
    }

    @Test
    fun saveFailureKeepsDraftAndFlagsSyncError() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited", aboutEn = "edited en")))
        runCurrent()
        repository.failing = true

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(SyncErrorKind.Save, state.syncError)
        assertEquals(1, state.unsavedCount)
    }

    @Test
    fun closingDirtyTabAsksForConfirmationAndDiscardsOnConfirm() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(work.id)))
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(name = "renamed")))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.CloseTab(WorkbenchTab.WorkEditor(work.id)))
        runCurrent()
        assertNotNull(viewModel.state.value.closeConfirmTab)

        viewModel.onIntent(WorkbenchIntent.ConfirmCloseTab)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertFalse(state.openTabs.contains(WorkbenchTab.WorkEditor(work.id)))
        assertEquals(work.name, state.works.first { it.id == work.id }.name)
    }

    @Test
    fun confirmedPublishPersistsPublishesAndStampsLastDeploy() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "to publish", aboutEn = "to publish en")))
        viewModel.onIntent(WorkbenchIntent.RequestPublish)
        runCurrent()
        assertTrue(viewModel.state.value.publishConfirmVisible)

        viewModel.onIntent(WorkbenchIntent.ConfirmPublish)
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.publishConfirmVisible)
        assertEquals(0, state.unsavedCount)
        assertEquals(1, repository.publishCount)
        assertEquals("2026-08-08 12:34", state.lastDeploy)
    }

    @Test
    fun japaneseOnlyChangeWarnsBeforeSavingAndConfirmProceeds() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "ja only")))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val warning = viewModel.state.value.languageOutdatedWarning
        assertNotNull(warning)
        assertEquals(listOf(work.name), warning.items.map { it.name })
        assertTrue(warning.items.single().staleEnglish)
        assertFalse(warning.alsoPublish)
        assertEquals(1, viewModel.state.value.unsavedCount)

        viewModel.onIntent(WorkbenchIntent.ConfirmLanguageOutdated)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertEquals("ja only", repository.works.works.first { it.id == work.id }.about)
    }

    @Test
    fun dismissingEnglishOutdatedWarningKeepsDraftUnsaved() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "ja only")))
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.DismissLanguageOutdated)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(null, state.languageOutdatedWarning)
        assertEquals(1, state.unsavedCount)
        assertNotEquals("ja only", repository.works.works.first { it.id == work.id }.about)
    }

    @Test
    fun englishOnlyChangeWarnsThatJapaneseIsOutdated() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(aboutEn = "en only")))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val warning = viewModel.state.value.languageOutdatedWarning
        assertNotNull(warning)
        assertEquals(listOf(work.name), warning.items.map { it.name })
        assertFalse(warning.items.single().staleEnglish)

        viewModel.onIntent(WorkbenchIntent.ConfirmLanguageOutdated)
        runCurrent()

        assertEquals(0, viewModel.state.value.unsavedCount)
        assertEquals("en only", repository.works.works.first { it.id == work.id }.aboutEn)
    }

    @Test
    fun publishWithJapaneseOnlyChangeWarnsWithPublishFlag() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "ja only")))
        viewModel.onIntent(WorkbenchIntent.RequestPublish)
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.ConfirmPublish)
        runCurrent()

        val warning = viewModel.state.value.languageOutdatedWarning
        assertNotNull(warning)
        assertTrue(warning.alsoPublish)
        assertEquals(0, repository.publishCount)

        viewModel.onIntent(WorkbenchIntent.ConfirmLanguageOutdated)
        runCurrent()

        assertEquals(1, repository.publishCount)
        assertEquals(0, viewModel.state.value.unsavedCount)
    }

    @Test
    fun loadsPortfolioPreviewDataAfterSignIn() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(PreviewGitHubProfile, state.portfolioProfile)
        assertEquals(PreviewContributionCalendar, state.contributions)
        assertFalse(state.contributionsFailed)
    }

    @Test
    fun contributionsFetchFailureFlagsTheCardAndRetryRecovers() = runTest {
        signIn()
        val repository = FakeAdminContentRepository(previewFailing = true)
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        assertTrue(viewModel.state.value.contributionsFailed)

        repository.previewFailing = false
        viewModel.onIntent(WorkbenchIntent.RetryPreview)
        runCurrent()

        assertFalse(viewModel.state.value.contributionsFailed)
        assertEquals(PreviewContributionCalendar, viewModel.state.value.contributions)
    }

    @Test
    fun discardingADraftDoesNotRecycleItsIdForTheNextNewWork() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()
        val secondId = viewModel.state.value.works.last().id

        // 2つ目の下書きを破棄してから新規作成しても、残っている下書きの ID とは衝突しない
        viewModel.onIntent(WorkbenchIntent.CloseTab(WorkbenchTab.WorkEditor(secondId)))
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.ConfirmCloseTab)
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()

        val ids = viewModel.state.value.works.map { it.id }
        assertEquals(ids.distinct(), ids)
    }

    @Test
    fun confirmedDeleteRemovesWorkAndPersistsOnNextSave() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()

        viewModel.onIntent(WorkbenchIntent.RequestDeleteWork(work.id))
        runCurrent()
        assertEquals(work.id, viewModel.state.value.deleteConfirmWork?.id)

        viewModel.onIntent(WorkbenchIntent.ConfirmDeleteWork)
        runCurrent()
        val state = viewModel.state.value
        assertTrue(state.works.none { it.id == work.id })
        assertTrue(state.unsavedCount > 0)

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()
        assertEquals(0, viewModel.state.value.unsavedCount)
        assertTrue(repository.works.works.none { it.id == work.id })
    }

    @Test
    fun deletingUnsavedDraftDoesNotRecycleItsIdForTheNextNewWork() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()
        val created = viewModel.state.value.works.last()

        // 未保存のまま削除 → 保存前に新規作成しても、削除待ち id を再利用せず一覧に現れる
        viewModel.onIntent(WorkbenchIntent.RequestDeleteWork(created.id))
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.ConfirmDeleteWork)
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()

        val works = viewModel.state.value.works
        val recreated = works.last()
        assertTrue(recreated.id != created.id)
        assertTrue(works.any { it.id == recreated.id })
    }

    @Test
    fun addScreenshotUploadsAndAppendsPathToDraft() = runTest {
        signIn()
        val viewModel = viewModel(
            FakeAdminContentRepository(),
            pickedImage = PickedImageFile(name = "shot.png", mimeType = "image/png", bytes = byteArrayOf(1)),
        )
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()

        viewModel.onIntent(WorkbenchIntent.AddScreenshot(work.id))
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.uploadingScreenshot)
        val updated = state.works.first { it.id == work.id }
        assertEquals(work.screenshots + "images/works/${work.id}/uploaded-shot.png", updated.screenshots)
        assertTrue(work.id in state.unsavedWorkIds)
    }

    @Test
    fun cancellingImagePickerLeavesDraftUntouched() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()
        val work = viewModel.state.value.works.first()

        viewModel.onIntent(WorkbenchIntent.AddScreenshot(work.id))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertFalse(state.uploadingScreenshot)
    }

    @Test
    fun terminalDraftSavesThroughTheRepository() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()

        val edited = TerminalCommandsContent(
            commands = listOf(TerminalTextCommand(keyword = "coffee", lines = listOf("brewing..."))),
        )
        viewModel.onIntent(WorkbenchIntent.UpdateTerminalDraft(edited))
        runCurrent()
        assertEquals(1, viewModel.state.value.unsavedCount)

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        assertEquals(0, viewModel.state.value.unsavedCount)
        assertEquals(edited, repository.terminal)
    }

    @Test
    fun japaneseOnlyReadmeChangeWarnsBeforeSaving() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(repository)
        startCollecting(viewModel.state)
        runCurrent()

        val edited = ReadmeContent(
            ja = listOf(ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText("日本語のみ更新")))),
            en = repository.readme.en,
        )
        viewModel.onIntent(WorkbenchIntent.UpdateReadmeDraft(edited))
        runCurrent()
        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val warning = viewModel.state.value.languageOutdatedWarning
        assertNotNull(warning)
        assertEquals(listOf("README"), warning.items.map { it.name })
    }

    @Test
    fun addProfileAvatarUploadsAndReplacesTheAvatarUrl() = runTest {
        signIn()
        val repository = FakeAdminContentRepository()
        val viewModel = viewModel(
            repository,
            pickedImage = PickedImageFile(name = "avatar.png", mimeType = "image/png", bytes = byteArrayOf(1)),
        )
        startCollecting(viewModel.state)
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.AddProfileAvatar)
        runCurrent()

        val state = viewModel.state.value
        assertEquals("images/profile/uploaded-avatar.png", state.profile.avatarUrl)
        assertTrue(state.profileUnsaved)
    }

    @Test
    fun creatingWorkOpensItsEditorAsDraft() = runTest {
        signIn()
        val viewModel = viewModel(FakeAdminContentRepository())
        startCollecting(viewModel.state)
        runCurrent()
        val before = viewModel.state.value.works.size

        viewModel.onIntent(WorkbenchIntent.CreateWork)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(before + 1, state.works.size)
        val created = state.works.last()
        assertEquals(ContentStatus.Draft, created.status)
        assertEquals(WorkbenchTab.WorkEditor(created.id), state.activeTab)
    }
}
