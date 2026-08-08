package io.github.kei_1111.admin.app.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kei_1111.admin.app.core.common.result.Result
import io.github.kei_1111.admin.app.core.common.result.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("VariableNaming")
abstract class MviViewModel<VS : ViewModelState<S>, S : State, I : Intent> : ViewModel() {
    protected val _viewModelState = MutableStateFlow<VS>(createInitialViewModelState())

    val state: StateFlow<S> = _viewModelState
        .map(ViewModelState<S>::toState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = createInitialState(),
        )

    protected abstract fun createInitialViewModelState(): VS

    protected abstract fun createInitialState(): S

    abstract fun onIntent(intent: I)

    protected fun updateViewModelState(update: VS.() -> VS) {
        _viewModelState.update { update(it) }
    }

    protected fun <T> Flow<T>.collectAsResult(reduce: VS.(Result<T>) -> VS): Job =
        viewModelScope.launch {
            asResult().collect { result -> updateViewModelState { reduce(result) } }
        }

    protected fun Flow<*>.prefetchAsResult(): Job = asResult().launchIn(viewModelScope)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
