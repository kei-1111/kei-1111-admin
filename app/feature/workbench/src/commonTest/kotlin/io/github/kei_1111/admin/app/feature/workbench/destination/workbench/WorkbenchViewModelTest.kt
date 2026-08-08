package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.app.core.domain.usecase.GetContentMetaUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.GetWorksDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.PublishContentUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveProfileDraftUseCase
import io.github.kei_1111.admin.app.core.domain.usecase.SaveWorksDraftUseCase
import io.github.kei_1111.admin.app.core.testing.ViewModelTestBase
import io.github.kei_1111.admin.app.core.testing.startCollecting
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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
    var meta: ContentMeta = ContentMeta(),
    var failing: Boolean = false,
) {
    var publishCount = 0

    fun failIfRequested() {
        check(!failing) { "fake failure" }
    }
}

private fun viewModel(repository: FakeAdminContentRepository) = WorkbenchViewModel(
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
        assertFalse(state.syncError)
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
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited")))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertFalse(state.syncError)
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
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited")))
        runCurrent()
        repository.failing = true

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val state = viewModel.state.value
        assertTrue(state.syncError)
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
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "to publish")))
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
