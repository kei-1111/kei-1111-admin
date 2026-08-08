package io.github.kei_1111.admin.app.feature.workbench.destination.workbench

import io.github.kei_1111.admin.app.core.testing.ViewModelTestBase
import io.github.kei_1111.admin.app.core.testing.startCollecting
import io.github.kei_1111.admin.app.feature.workbench.model.AdminNode
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.ContentStatus
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WorkbenchViewModelTest : ViewModelTestBase() {

    @Test
    fun selectingWorkNodeOpensAndActivatesItsEditorTab() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
        val firstWorkId = viewModel.state.value.works.first().id

        viewModel.onIntent(WorkbenchIntent.SelectNode(AdminNode.WorkItem(firstWorkId)))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(WorkbenchTab.WorkEditor(firstWorkId), state.activeTab)
        assertTrue(state.openTabs.contains(WorkbenchTab.WorkEditor(firstWorkId)))
    }

    @Test
    fun updatingWorkDraftMarksItUnsaved() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
        val work = viewModel.state.value.works.first()

        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited")))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(1, state.unsavedCount)
        assertTrue(work.id in state.unsavedWorkIds)
        assertEquals("edited", state.works.first { it.id == work.id }.about)
    }

    @Test
    fun savingDraftClearsUnsavedState() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
        val work = viewModel.state.value.works.first()
        viewModel.onIntent(WorkbenchIntent.UpdateWorkDraft(work.copy(about = "edited")))

        viewModel.onIntent(WorkbenchIntent.SaveDraft)
        runCurrent()

        val state = viewModel.state.value
        assertEquals(0, state.unsavedCount)
        assertEquals("edited", state.works.first { it.id == work.id }.about)
    }

    @Test
    fun closingDirtyTabAsksForConfirmationAndDiscardOnConfirm() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
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
    fun closingCleanTabClosesImmediately() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
        viewModel.onIntent(WorkbenchIntent.SelectNode(AdminNode.Profile))
        runCurrent()

        viewModel.onIntent(WorkbenchIntent.CloseTab(WorkbenchTab.ProfileEditor))
        runCurrent()

        val state = viewModel.state.value
        assertEquals(null, state.closeConfirmTab)
        assertFalse(state.openTabs.contains(WorkbenchTab.ProfileEditor))
    }

    @Test
    fun confirmedPublishAppliesDraftsAndMarksEverythingPublished() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
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
        assertEquals("just now", state.lastDeploy)
        assertTrue(state.works.all { it.status == ContentStatus.Published })
    }

    @Test
    fun creatingWorkOpensItsEditorAsDraft() = runTest {
        val viewModel = WorkbenchViewModel()
        startCollecting(viewModel.state)
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
