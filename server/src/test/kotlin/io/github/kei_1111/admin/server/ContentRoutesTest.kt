package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeContentStorage : ContentStorage {
    val objects = mutableMapOf<String, String>()

    override suspend fun read(path: String): String? = objects[path]

    override suspend fun write(path: String, content: String) {
        objects[path] = content
    }
}

class ContentRoutesTest {

    private fun contentTestApplication(
        storage: FakeContentStorage = FakeContentStorage(),
        block: suspend ApplicationTestBuilder.(HttpClient, FakeContentStorage) -> Unit,
    ) = testApplication {
        application {
            configureApplication(
                authConfig = TestGoogleAuth.authConfig,
                contentService = ContentService(storage = storage, now = { "2026-08-08T12:00:00Z" }),
                previewService = PortfolioPreviewService { path -> """{"path":"$path"}""" },
            )
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        block(client, storage)
    }

    @Test
    fun worksDraftFallsBackToBundledSeed() = contentTestApplication { client, _ ->
        val response = client.get("/api/works") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(HttpStatusCode.OK, response.status)
        val works = response.body<WorksContent>().works
        assertTrue(works.any { it.id == "withmo" })
    }

    @Test
    fun profileDraftFallsBackToBundledSeed() = contentTestApplication { client, _ ->
        val response = client.get("/api/profile") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("けい", response.body<AdminProfile>().displayName)
    }

    @Test
    fun savedWorksDraftRoundTrips() = contentTestApplication { client, _ ->
        val content = WorksContent(works = listOf(Work(id = "withmo", name = "withmo")))

        val put = client.put("/api/works") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(content)
        }
        val get = client.get("/api/works") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(HttpStatusCode.OK, put.status)
        assertEquals(content, get.body<WorksContent>())
    }

    @Test
    fun savedProfileDraftRoundTrips() = contentTestApplication { client, _ ->
        val profile = AdminProfile(displayName = "けい", role = "Android Engineer")

        client.put("/api/profile") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(profile)
        }
        val get = client.get("/api/profile") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(profile, get.body<AdminProfile>())
    }

    @Test
    fun publishCopiesOnlyPublishedWorksAndStampsMeta() = contentTestApplication { client, storage ->
        val content = WorksContent(
            works = listOf(
                Work(id = "published", name = "published", status = ContentStatus.Published),
                Work(id = "draft", name = "draft", status = ContentStatus.Draft),
            ),
        )
        client.put("/api/works") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(content)
        }

        val publish = client.post("/api/publish") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(HttpStatusCode.OK, publish.status)
        assertEquals("2026-08-08T12:00:00Z", publish.body<ContentMeta>().lastPublishedAt)
        val published = storage.objects["content/published/works.json"].orEmpty()
        assertTrue(published.contains("\"published\""))
        assertTrue(!published.contains("\"draft\""))
    }

    @Test
    fun metaDefaultsToNeverPublished() = contentTestApplication { client, _ ->
        val response = client.get("/api/meta") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(ContentMeta(), response.body<ContentMeta>())
    }

    @Test
    fun previewEndpointsProxyThePortfolioApi() = contentTestApplication { client, _ ->
        val profile = client.get("/api/preview/profile") { bearerAuth(TestGoogleAuth.token()) }
        val contributions = client.get("/api/preview/contributions") { bearerAuth(TestGoogleAuth.token()) }

        assertEquals(HttpStatusCode.OK, profile.status)
        assertTrue(profile.bodyAsText().contains("/api/profile"))
        assertTrue(contributions.bodyAsText().contains("/api/contributions"))
    }

    @Test
    fun previewEndpointsRequireAuthentication() = contentTestApplication { client, _ ->
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/preview/profile").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/preview/contributions").status)
    }

    @Test
    fun contentEndpointsRequireAuthentication() = contentTestApplication { client, _ ->
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/works").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/profile").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/api/publish").status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/api/meta").status)
    }
}
