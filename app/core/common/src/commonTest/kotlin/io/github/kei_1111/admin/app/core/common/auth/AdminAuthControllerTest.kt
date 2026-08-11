package io.github.kei_1111.admin.app.core.common.auth

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AdminAuthControllerTest {

    // プロセス内で共有される object のため、各テストの後で確実に空へ戻す。
    @AfterTest
    fun resetToken() {
        AdminAuthController.idToken.value?.let(AdminAuthController::invalidateIdToken)
    }

    @Test
    fun receivedTokenIsExposed() {
        AdminAuthController.receiveIdToken("token-1")

        assertEquals("token-1", AdminAuthController.idToken.value)
    }

    @Test
    fun invalidatingTheCurrentTokenClearsIt() {
        AdminAuthController.receiveIdToken("token-1")

        AdminAuthController.invalidateIdToken("token-1")

        assertNull(AdminAuthController.idToken.value)
    }

    @Test
    fun invalidatingAnAlreadyReplacedTokenKeepsTheNewOne() {
        AdminAuthController.receiveIdToken("expired-token")
        AdminAuthController.receiveIdToken("fresh-token")

        AdminAuthController.invalidateIdToken("expired-token")

        assertEquals("fresh-token", AdminAuthController.idToken.value)
    }
}
