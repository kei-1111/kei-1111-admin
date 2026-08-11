package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private fun unauthorizedClient() = HttpClient(
    MockEngine { respond(content = "", status = HttpStatusCode.Unauthorized) },
) {
    invalidateIdTokenOnUnauthorized()
}

class UnauthorizedHandlingTest {

    // プロセス内で共有される object のため、各テストの後で確実に空へ戻す。
    @AfterTest
    fun resetToken() {
        AdminAuthController.idToken.value?.let(AdminAuthController::invalidateIdToken)
    }

    @Test
    fun unauthorizedResponseDiscardsTheTokenThatSentIt() = runTest {
        AdminAuthController.receiveIdToken("expired-token")

        unauthorizedClient().get("/api/works") { authorize() }

        assertNull(AdminAuthController.idToken.value)
    }

    @Test
    fun unauthorizedResponseForAnOlderTokenKeepsTheTokenFromARetriedSignIn() = runTest {
        AdminAuthController.receiveIdToken("fresh-token")

        // 失効済みトークンで送られていたリクエストの 401 が、再サインイン完了後に遅れて届く状況。
        unauthorizedClient().get("/api/works") {
            header(HttpHeaders.Authorization, "Bearer expired-token")
        }

        assertEquals("fresh-token", AdminAuthController.idToken.value)
    }

    @Test
    fun unauthorizedResponseWithoutAnAuthorizationHeaderLeavesTheTokenAlone() = runTest {
        AdminAuthController.receiveIdToken("fresh-token")

        unauthorizedClient().get("/images/works/withmo/shot.png")

        assertEquals("fresh-token", AdminAuthController.idToken.value)
    }
}
