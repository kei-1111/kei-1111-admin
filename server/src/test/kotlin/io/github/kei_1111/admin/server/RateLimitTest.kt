package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.plugins.API_RATE_LIMIT_PER_MINUTE
import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

private class RateLimitContentStorage : ContentStorage {
    private val objects = mutableMapOf<String, String>()
    private val binaries = mutableMapOf<String, ByteArray>()

    override suspend fun read(path: String): String? = objects[path]

    override suspend fun write(path: String, content: String) {
        objects[path] = content
    }

    override suspend fun readBytes(path: String): ByteArray? = binaries[path]

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        binaries[path] = content
    }
}

private fun rateLimitTestApplication(
    block: suspend ApplicationTestBuilder.(HttpClient) -> Unit,
) = testApplication {
    application {
        val storage = RateLimitContentStorage()
        val contentService = ContentService(storage = storage)
        configureApplication(
            authConfig = TestGoogleAuth.authConfig,
            contentService = contentService,
            previewService = PortfolioPreviewService { path -> """{"path":"$path"}""" },
            imageService = ImageService(storage = storage, now = { 1723100000000 }),
            publishService = PublishService(
                storage = storage,
                contentService = contentService,
                now = { "2026-08-08T12:00:00Z" },
            ),
        )
    }
    block(client)
}

class RateLimitTest {

    @Test
    fun apiReturnsTooManyRequestsOnceTheIpLimitIsExceeded() = rateLimitTestApplication { client ->
        val token = TestGoogleAuth.token()

        repeat(API_RATE_LIMIT_PER_MINUTE) {
            val response = client.get("/api/works") { bearerAuth(token) }
            assertEquals(HttpStatusCode.OK, response.status)
        }

        val response = client.get("/api/works") { bearerAuth(token) }
        assertEquals(HttpStatusCode.TooManyRequests, response.status)
    }

    @Test
    fun distinctClientIpsHaveIndependentBudgets() = rateLimitTestApplication { client ->
        val token = TestGoogleAuth.token()
        val exhaustedIp = "203.0.113.1"
        val freshIp = "198.51.100.7"

        repeat(API_RATE_LIMIT_PER_MINUTE) {
            val response = client.get("/api/works") {
                bearerAuth(token)
                header(HttpHeaders.XForwardedFor, exhaustedIp)
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }

        val exhausted = client.get("/api/works") {
            bearerAuth(token)
            header(HttpHeaders.XForwardedFor, exhaustedIp)
        }
        val fresh = client.get("/api/works") {
            bearerAuth(token)
            header(HttpHeaders.XForwardedFor, freshIp)
        }

        assertEquals(HttpStatusCode.TooManyRequests, exhausted.status)
        assertEquals(HttpStatusCode.OK, fresh.status)
    }

    @Test
    fun spoofedLeadingForwardedEntriesShareTheLastIpBudget() = rateLimitTestApplication { client ->
        val token = TestGoogleAuth.token()

        repeat(API_RATE_LIMIT_PER_MINUTE) { index ->
            val response = client.get("/api/works") {
                bearerAuth(token)
                header(HttpHeaders.XForwardedFor, "10.0.0.$index, 203.0.113.1")
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }

        val response = client.get("/api/works") {
            bearerAuth(token)
            header(HttpHeaders.XForwardedFor, "10.9.9.9, 203.0.113.1")
        }

        assertEquals(HttpStatusCode.TooManyRequests, response.status)
    }

    @Test
    fun healthStaysReachableAfterTheApiBudgetIsExhausted() = rateLimitTestApplication { client ->
        val token = TestGoogleAuth.token()
        val clientIp = "203.0.113.1"

        repeat(API_RATE_LIMIT_PER_MINUTE) {
            val response = client.get("/api/works") {
                bearerAuth(token)
                header(HttpHeaders.XForwardedFor, clientIp)
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }
        val exhausted = client.get("/api/works") {
            bearerAuth(token)
            header(HttpHeaders.XForwardedFor, clientIp)
        }
        val health = client.get("/health") {
            header(HttpHeaders.XForwardedFor, clientIp)
        }

        assertEquals(HttpStatusCode.TooManyRequests, exhausted.status)
        assertEquals(HttpStatusCode.OK, health.status)
    }
}
