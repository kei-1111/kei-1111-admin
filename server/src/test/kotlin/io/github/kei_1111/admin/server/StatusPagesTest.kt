package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private fun throwingContentStorage() = object : ContentStorage {
    override suspend fun read(path: String): String? = error("boom")

    override suspend fun write(path: String, content: String) = Unit

    override suspend fun readBytes(path: String): ByteArray? = null

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) = Unit
}

class StatusPagesTest {

    @Test
    fun unhandledFailureReturnsBareInternalServerError() = testApplication {
        application {
            val storage = throwingContentStorage()
            val contentService = ContentService(storage = storage)
            configureApplication(
                authConfig = TestGoogleAuth.authConfig,
                contentService = contentService,
                previewService = PortfolioPreviewService { "{}" },
                imageService = ImageService(storage = storage),
                publishService = PublishService(storage = storage, contentService = contentService),
            )
        }

        val response = client.get("/api/works") {
            bearerAuth(TestGoogleAuth.token())
        }
        val body = response.bodyAsText()

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertFalse(body.contains("boom"))
        assertFalse(body.contains("IllegalStateException"))
    }
}
