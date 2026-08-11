package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdminPublishRepositoryImplTest {

    @Test
    fun fetchMetaUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPublishRepositoryImpl(
            testHttpClient(
                responseBody = """{"lastPublishedAt":"2026-08-10T12:00:00Z"}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/meta",
            ),
        )

        val actual = repository.fetchMeta()

        assertEquals(ContentMeta(lastPublishedAt = "2026-08-10T12:00:00Z"), actual)
    }

    @Test
    fun publishUsesPostPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPublishRepositoryImpl(
            testHttpClient(
                responseBody = """{"lastPublishedAt":"2026-08-10T13:00:00Z"}""",
                expectedMethod = HttpMethod.Post,
                expectedPath = "/api/publish",
            ),
        )

        val actual = repository.publish()

        assertEquals(ContentMeta(lastPublishedAt = "2026-08-10T13:00:00Z"), actual)
    }

    @Test
    fun fetchPublishedSnapshotUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminPublishRepositoryImpl(
            testHttpClient(
                responseBody = """{"profile":{"displayName":"Published"}}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/published",
            ),
        )

        val actual = repository.fetchPublishedSnapshot()

        assertEquals(PublishedSnapshot(profile = AdminProfile(displayName = "Published")), actual)
    }

    @Test
    fun discardDraftUsesPostPathAndReturnsUnit() = runTest {
        val repository = AdminPublishRepositoryImpl(
            testHttpClient(
                responseBody = "",
                expectedMethod = HttpMethod.Post,
                expectedPath = "/api/drafts/discard",
                status = HttpStatusCode.NoContent,
            ),
        )

        val actual = repository.discardDraft(ContentDocument.Profile)

        assertEquals(Unit, actual)
    }
}
