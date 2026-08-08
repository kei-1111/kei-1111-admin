package io.github.kei_1111.admin.app.core.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent

/**
 * `MviViewModel.state` は `WhileSubscribed` のため、コレクタ不在では upstream が動かない —
 * Intent 送出や fake の emit より先に必ず呼ぶ(`.claude/rules/mvi-testing.md` —
 * Collect First, Then Intent)。以降のスケジューラ前進(`runCurrent()`)は吸収しない。
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun TestScope.startCollecting(state: StateFlow<*>) {
    backgroundScope.launch { state.collect {} }
    runCurrent()
}
