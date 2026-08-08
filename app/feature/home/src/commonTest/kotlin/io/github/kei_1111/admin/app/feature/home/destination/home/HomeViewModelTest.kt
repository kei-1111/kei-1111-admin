package io.github.kei_1111.admin.app.feature.home.destination.home

import io.github.kei_1111.admin.app.core.testing.ViewModelTestBase
import io.github.kei_1111.admin.app.core.testing.startCollecting
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeViewModelTest : ViewModelTestBase() {

    @Test
    fun `UpdateMemo reflects the input into state`() = runTest {
        val viewModel = HomeViewModel()
        startCollecting(viewModel.state)

        viewModel.onIntent(HomeIntent.UpdateMemo("hello"))
        runCurrent()

        assertEquals("hello", viewModel.state.value.memo)
    }

    @Test
    fun `ClearMemo resets the memo to empty`() = runTest {
        val viewModel = HomeViewModel()
        startCollecting(viewModel.state)

        viewModel.onIntent(HomeIntent.UpdateMemo("hello"))
        viewModel.onIntent(HomeIntent.ClearMemo)
        runCurrent()

        assertEquals("", viewModel.state.value.memo)
    }
}
