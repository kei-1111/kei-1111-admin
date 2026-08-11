package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

private fun previewContentStorage() = object : ContentStorage {
    override suspend fun read(path: String): String? = null

    override suspend fun write(path: String, content: String) = Unit

    override suspend fun readBytes(path: String): ByteArray? = null

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) = Unit
}

private fun previewTestApplication(
    previewService: PortfolioPreviewService,
    block: suspend ApplicationTestBuilder.(HttpClient) -> Unit,
) = testApplication {
    application {
        val storage = previewContentStorage()
        val contentService = ContentService(storage = storage)
        configureApplication(
            authConfig = TestGoogleAuth.authConfig,
            contentService = contentService,
            previewService = previewService,
            imageService = ImageService(storage = storage),
            publishService = PublishService(storage = storage, contentService = contentService),
        )
    }
    block(client)
}

private suspend fun assertSuccessfulPreview(
    client: HttpClient,
    route: String,
    upstreamPath: String,
) {
    val response = client.get(route) { bearerAuth(TestGoogleAuth.token()) }

    assertEquals(HttpStatusCode.OK, response.status)
    assertEquals(ContentType.Application.Json.toString(), response.bareContentType())
    assertEquals("""{"path":"$upstreamPath"}""", response.bodyAsText())
}

private suspend fun assertUnavailablePreview(client: HttpClient, route: String) {
    val response = client.get(route) { bearerAuth(TestGoogleAuth.token()) }

    assertEquals(HttpStatusCode.BadGateway, response.status)
    assertEquals(ContentType.Application.Json.toString(), response.bareContentType())
    assertEquals("""{"error":"portfolio API unavailable"}""", response.bodyAsText())
}

private fun HttpResponse.bareContentType(): String? = headers[HttpHeaders.ContentType]?.substringBefore(';')

class PreviewRoutesTest {

    @Test
    fun profilePreviewPassesThroughThePortfolioResponse() = previewTestApplication(
        previewService = PortfolioPreviewService { path -> """{"path":"$path"}""" },
    ) { client ->
        assertSuccessfulPreview(client, "/api/preview/profile", "/api/profile")
    }

    @Test
    fun contributionsPreviewPassesThroughThePortfolioResponse() = previewTestApplication(
        previewService = PortfolioPreviewService { path -> """{"path":"$path"}""" },
    ) { client ->
        assertSuccessfulPreview(client, "/api/preview/contributions", "/api/contributions")
    }

    @Test
    fun issuesPreviewPassesThroughThePortfolioResponse() = previewTestApplication(
        previewService = PortfolioPreviewService { path -> """{"path":"$path"}""" },
    ) { client ->
        assertSuccessfulPreview(client, "/api/preview/issues", "/api/issues")
    }

    @Test
    fun profilePreviewMapsAnUnavailablePortfolioToBadGateway() = previewTestApplication(
        previewService = PortfolioPreviewService { null },
    ) { client ->
        assertUnavailablePreview(client, "/api/preview/profile")
    }

    @Test
    fun contributionsPreviewMapsAnUnavailablePortfolioToBadGateway() = previewTestApplication(
        previewService = PortfolioPreviewService { null },
    ) { client ->
        assertUnavailablePreview(client, "/api/preview/contributions")
    }

    @Test
    fun issuesPreviewMapsAnUnavailablePortfolioToBadGateway() = previewTestApplication(
        previewService = PortfolioPreviewService { null },
    ) { client ->
        assertUnavailablePreview(client, "/api/preview/issues")
    }

    @Test
    fun previewRoutesRequireAuthentication() = previewTestApplication(
        previewService = PortfolioPreviewService { "{}" },
    ) { client ->
        listOf(
            "/api/preview/profile",
            "/api/preview/contributions",
            "/api/preview/issues",
        ).forEach { route ->
            assertEquals(HttpStatusCode.Unauthorized, client.get(route).status)
        }
    }
}
