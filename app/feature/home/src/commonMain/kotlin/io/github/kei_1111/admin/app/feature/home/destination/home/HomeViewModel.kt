package io.github.kei_1111.admin.app.feature.home.destination.home

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.kei_1111.admin.app.core.mvi.MviViewModel

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
internal class HomeViewModel : MviViewModel<HomeViewModelState, HomeState, HomeIntent>() {

    override fun createInitialViewModelState() = HomeViewModelState()
    override fun createInitialState() = HomeState()

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.UpdateMemo -> updateViewModelState { copy(memo = intent.memo) }
            is HomeIntent.ClearMemo -> updateViewModelState { copy(memo = "") }
        }
    }
}
