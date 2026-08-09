package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal fun HttpRequestBuilder.authorize() {
    AdminAuthController.idToken.value?.let { header(HttpHeaders.Authorization, "Bearer $it") }
}

internal suspend inline fun <reified T> HttpClient.putJsonAuthorized(url: String, payload: T): T =
    put(url) {
        authorize()
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.body()
