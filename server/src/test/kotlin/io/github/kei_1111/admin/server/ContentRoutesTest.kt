package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.DiscardDraftRequest
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.ReadmeInline
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand
import io.github.kei_1111.admin.shared.model.UploadedImage
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class FakeContentStorage : ContentStorage {
    val objects = mutableMapOf<String, String>()
    val binaries = mutableMapOf<String, ByteArray>()

    override suspend fun read(path: String): String? = objects[path]

    override suspend fun write(path: String, content: String) {
        objects[path] = content
    }

    override suspend fun readBytes(path: String): ByteArray? = binaries[path]

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        binaries[path] = content
    }
}

class ContentRoutesTest {

    private fun contentTestApplication(
        storage: FakeContentStorage = FakeContentStorage(),
        block: suspend ApplicationTestBuilder.(HttpClient, FakeContentStorage) -> Unit,
    ) = testApplication {
        application {
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

    @Test
    fun terminalAndReadmeDraftsRoundTripAndPublish() = contentTestApplication { client, storage ->
        // シードが初期値として返る
        val seeded = client.get("/api/terminal") { bearerAuth(TestGoogleAuth.token()) }
            .body<TerminalCommandsContent>()
        assertTrue(seeded.commands.any { it.keyword == "neofetch" && it.description.isNotEmpty() })
        val seededReadme = client.get("/api/readme") { bearerAuth(TestGoogleAuth.token()) }
            .body<ReadmeContent>()
        assertTrue(seededReadme.ja.isNotEmpty() && seededReadme.en.isNotEmpty())

        // 下書き保存 → 公開で published/ に写る
        val commands = TerminalCommandsContent(
            commands = listOf(TerminalTextCommand(keyword = "hello", lines = listOf("world"))),
        )
        client.put("/api/terminal") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(commands)
        }
        val readme = ReadmeContent(
            ja = listOf(ReadmeBlock.Heading(level = 1, inlines = listOf(ReadmeInline.PlainText("見出し")))),
            en = listOf(ReadmeBlock.Heading(level = 1, inlines = listOf(ReadmeInline.PlainText("Heading")))),
        )
        client.put("/api/readme") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(readme)
        }
        client.post("/api/publish") { bearerAuth(TestGoogleAuth.token()) }

        assertTrue(storage.objects.containsKey("content/published/terminal-commands.json"))
        assertTrue(storage.objects.getValue("content/published/terminal-commands.json").contains("hello"))
        assertTrue(storage.objects.containsKey("content/published/readme.json"))
        assertTrue(storage.objects.getValue("content/published/readme.json").contains("Heading"))
    }

    @Test
    fun uploadsProfileAvatarUnderTheProfileDirectory() = contentTestApplication { client, storage ->
        val response = client.post("/api/images/profile") {
            bearerAuth(TestGoogleAuth.token())
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"avatar.png\"")
                            },
                        )
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val path = response.body<UploadedImage>().path
        assertTrue(path.startsWith("images/profile/") && path.endsWith(".png"))
        assertTrue(storage.binaries.containsKey(path))
    }

    @Test
    fun publishedSnapshotReturnsNullsBeforeFirstPublishAndContentAfter() = contentTestApplication { client, _ ->
        val before = client.get("/api/published") { bearerAuth(TestGoogleAuth.token()) }
            .body<PublishedSnapshot>()
        assertEquals(null, before.works)
        assertEquals(null, before.profile)

        client.post("/api/publish") { bearerAuth(TestGoogleAuth.token()) }

        val after = client.get("/api/published") { bearerAuth(TestGoogleAuth.token()) }
            .body<PublishedSnapshot>()
        assertTrue(after.works != null && after.profile != null && after.terminal != null && after.readme != null)
    }

    @Test
    fun discardingADraftRevertsItToThePublishedContent() = contentTestApplication { client, _ ->
        // 公開 → 下書きを書き換え → 破棄で公開内容へ戻る
        client.post("/api/publish") { bearerAuth(TestGoogleAuth.token()) }
        val published = client.get("/api/works") { bearerAuth(TestGoogleAuth.token()) }.body<WorksContent>()
        client.put("/api/works") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(WorksContent(works = emptyList()))
        }

        val response = client.post("/api/drafts/discard") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(DiscardDraftRequest(document = ContentDocument.Works))
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
        assertEquals(published, client.get("/api/works") { bearerAuth(TestGoogleAuth.token()) }.body<WorksContent>())
    }

    @Test
    fun discardingWithoutAPublishRevertsToTheSeed() = contentTestApplication { client, _ ->
        client.put("/api/works") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(WorksContent(works = emptyList()))
        }

        client.post("/api/drafts/discard") {
            bearerAuth(TestGoogleAuth.token())
            contentType(ContentType.Application.Json)
            setBody(DiscardDraftRequest(document = ContentDocument.Works))
        }

        val works = client.get("/api/works") { bearerAuth(TestGoogleAuth.token()) }.body<WorksContent>().works
        assertTrue(works.any { it.id == "withmo" })
    }

    @Test
    fun uploadsWorkImageAndServesItPublicly() = contentTestApplication { client, storage ->
        val response = client.post("/api/images/works/withmo") {
            bearerAuth(TestGoogleAuth.token())
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"shot.png\"")
                            },
                        )
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val path = response.body<UploadedImage>().path
        assertTrue(path.startsWith("images/works/withmo/") && path.endsWith(".png"))
        assertTrue(storage.binaries.containsKey(path))

        // 配信側は未認証で読める
        val served = client.get("/$path")
        assertEquals(HttpStatusCode.OK, served.status)
        assertEquals("image/png", served.headers[HttpHeaders.ContentType]?.substringBefore(';'))
    }

    @Test
    fun servesImagesWithCorsForThePortfolioOrigins() = contentTestApplication { client, storage ->
        storage.binaries["images/works/withmo/shot.webp"] = byteArrayOf(0x52, 0x49, 0x46, 0x46)

        val fromProduction = client.get("/images/works/withmo/shot.webp") {
            header(HttpHeaders.Origin, "https://kei-1111.github.io")
        }
        val fromDev = client.get("/images/works/withmo/shot.webp") {
            header(HttpHeaders.Origin, "http://localhost:8080")
        }
        val fromElsewhere = client.get("/images/works/withmo/shot.webp") {
            header(HttpHeaders.Origin, "https://evil.example")
        }

        assertEquals(HttpStatusCode.OK, fromProduction.status)
        assertEquals(
            "https://kei-1111.github.io",
            fromProduction.headers[HttpHeaders.AccessControlAllowOrigin],
        )
        assertEquals(
            "http://localhost:8080",
            fromDev.headers[HttpHeaders.AccessControlAllowOrigin],
        )
        assertEquals(null, fromElsewhere.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun servesImagesAsImmutableSoBrowsersAndCdnsStopRefetchingThem() =
        contentTestApplication { client, storage ->
            storage.binaries["images/works/withmo/shot.webp"] = byteArrayOf(0x52, 0x49, 0x46, 0x46)

            val response = client.get("/images/works/withmo/shot.webp")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("public, max-age=31536000, immutable", response.headers[HttpHeaders.CacheControl])
        }

    @Test
    fun answersRevalidationOfAnUnchangedImageWithNotModified() = contentTestApplication { client, storage ->
        storage.binaries["images/works/withmo/shot.webp"] = byteArrayOf(0x52, 0x49, 0x46, 0x46)

        val first = client.get("/images/works/withmo/shot.webp")
        val etag = first.headers[HttpHeaders.ETag]
        val revalidated = client.get("/images/works/withmo/shot.webp") {
            header(HttpHeaders.IfNoneMatch, etag.orEmpty())
        }

        assertNotNull(etag)
        assertEquals(HttpStatusCode.NotModified, revalidated.status)
        assertEquals("", revalidated.bodyAsText())
    }

    @Test
    fun servesTheImageWhenTheRevalidationTagDoesNotMatch() = contentTestApplication { client, storage ->
        storage.binaries["images/works/withmo/shot.webp"] = byteArrayOf(0x52, 0x49, 0x46, 0x46)

        val response = client.get("/images/works/withmo/shot.webp") {
            header(HttpHeaders.IfNoneMatch, "\"stale\"")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun rejectsUnsupportedImageContentType() = contentTestApplication { client, _ ->
        val response = client.post("/api/images/works/withmo") {
            bearerAuth(TestGoogleAuth.token())
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArrayOf(1),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/gif")
                                append(HttpHeaders.ContentDisposition, "filename=\"a.gif\"")
                            },
                        )
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
    }

    @Test
    fun rejectsImageWhoseBytesDoNotMatchTheDeclaredType() = contentTestApplication { client, _ ->
        val response = client.post("/api/images/works/withmo") {
            bearerAuth(TestGoogleAuth.token())
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            "<svg></svg>".encodeToByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"fake.png\"")
                            },
                        )
                    },
                ),
            )
        }

        assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
    }

    @Test
    fun rejectsUnauthenticatedImageUpload() = contentTestApplication { client, _ ->
        val response = client.post("/api/images/works/withmo") {
            setBody(MultiPartFormDataContent(formData { }))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
