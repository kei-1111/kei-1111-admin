package io.github.kei_1111.admin.app.feature.home.destination.home

import io.github.kei_1111.admin.app.core.mvi.State

internal data class HomeState(
    val memo: String = "",
) : State
