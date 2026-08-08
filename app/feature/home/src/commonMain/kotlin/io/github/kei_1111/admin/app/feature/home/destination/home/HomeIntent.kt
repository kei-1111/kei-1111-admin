package io.github.kei_1111.admin.app.feature.home.destination.home

import io.github.kei_1111.admin.app.core.mvi.Intent

internal sealed interface HomeIntent : Intent {
    data class UpdateMemo(val memo: String) : HomeIntent
    data object ClearMemo : HomeIntent
}
