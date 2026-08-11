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

    /**
     * サーバーが 401 を返した [token] だけを破棄する。再サインイン直後に、失効済みトークンで
     * 送られていたリクエストの 401 が遅れて届いても、取得したての有効なトークンは捨てない。
     * 一度受け取ったあとの null は「再サインインが必要」の合図として UI が扱う。
     */
    fun invalidateIdToken(token: String) {
        _idToken.compareAndSet(expect = token, update = null)
    }
}
