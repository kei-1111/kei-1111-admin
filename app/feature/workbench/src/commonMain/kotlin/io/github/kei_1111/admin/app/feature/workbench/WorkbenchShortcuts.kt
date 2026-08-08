package io.github.kei_1111.admin.app.feature.workbench

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ⌘S(下書き保存)/ ⌘⏎(公開)のグローバルショートカット通知。
 * 発生源が DOM の keydown リスナー(webApp wasmJs)のため、Compose の外から tick を進める。
 */
object WorkbenchShortcuts {
    private val _saveTick = MutableStateFlow(0)
    val saveTick: StateFlow<Int> = _saveTick.asStateFlow()

    private val _publishTick = MutableStateFlow(0)
    val publishTick: StateFlow<Int> = _publishTick.asStateFlow()

    fun requestSave() {
        _saveTick.value += 1
    }

    fun requestPublish() {
        _publishTick.value += 1
    }
}
