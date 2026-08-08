package io.github.kei_1111.admin.app.core.common.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Sign-In(wasmJs の GIS コールバック)から届く ID トークンの唯一の保持者。
 * 発生源が Compose の外(DOM のサインインボタン)のため、テーマと同様 App レベルの状態として持つ。
 */
object AdminAuthController {
    private val _idToken = MutableStateFlow<String?>(null)
    val idToken: StateFlow<String?> = _idToken.asStateFlow()

    fun receiveIdToken(token: String) {
        _idToken.value = token
    }
}
