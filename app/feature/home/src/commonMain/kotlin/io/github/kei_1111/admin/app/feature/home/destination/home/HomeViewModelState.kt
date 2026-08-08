package io.github.kei_1111.admin.app.feature.home.destination.home

import io.github.kei_1111.admin.app.core.mvi.ViewModelState

internal data class HomeViewModelState(
    val memo: String = "",
) : ViewModelState<HomeState> {
    override fun toState(): HomeState = HomeState(memo = memo)
}
