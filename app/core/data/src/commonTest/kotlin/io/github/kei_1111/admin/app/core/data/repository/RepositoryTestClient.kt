package io.github.kei_1111.admin.app.core.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

internal fun testHttpClient(
    responseBody: String,
    expectedMethod: HttpMethod,
    expectedPath: String,
    status: HttpStatusCode = HttpStatusCode.OK,
    expectSuccess: Boolean = false,
    verifyRequest: (HttpRequestData) -> Unit = {},
): HttpClient = HttpClient(
    MockEngine { request ->
        assertEquals(expectedMethod, request.method)
        assertEquals(expectedPath, request.url.encodedPath)
        verifyRequest(request)
        respond(
            content = responseBody,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )
    },
) {
    this.expectSuccess = expectSuccess
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
