package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

private const val BEARER_PREFIX = "Bearer "

internal fun HttpRequestBuilder.authorize() {
    AdminAuthController.idToken.value?.let { header(HttpHeaders.Authorization, BEARER_PREFIX + it) }
}

/**
 * Google ID トークンは約1時間で失効する。401 を返したトークンを破棄して UI に再サインインを促す。
 * 破棄はリクエストが実際に使ったトークンに限定する — そうしないと、再サインイン直後に届いた
 * 古いリクエストの 401 が取得したてのトークンを消してしまう。
 */
internal fun HttpClientConfig<*>.invalidateIdTokenOnUnauthorized() {
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status == HttpStatusCode.Unauthorized) {
                response.request.headers[HttpHeaders.Authorization]
                    ?.removePrefix(BEARER_PREFIX)
                    ?.let(AdminAuthController::invalidateIdToken)
            }
        }
    }
}

internal suspend inline fun <reified T> HttpClient.putJsonAuthorized(url: String, payload: T): T =
    put(url) {
        authorize()
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.body()
