package io.github.kei_1111.admin.app.core.data.repository

import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AdminContentRepositoryImplTest {

    @Test
    fun fetchWorksDraftUsesGetPathAndIgnoresUnknownFields() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"works":[{"id":"work-1","name":"Work 1","futureField":true}],"futureField":true}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/works",
            ),
        )

        val actual = repository.fetchWorksDraft()

        assertEquals(WorksContent(works = listOf(Work(id = "work-1", name = "Work 1"))), actual)
    }

    @Test
    fun saveWorksDraftUsesPutPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"works":[{"id":"saved","name":"Saved"}]}""",
                expectedMethod = HttpMethod.Put,
                expectedPath = "/api/works",
            ),
        )

        val actual = repository.saveWorksDraft(WorksContent())

        assertEquals(WorksContent(works = listOf(Work(id = "saved", name = "Saved"))), actual)
    }

    @Test
    fun fetchProfileDraftUsesGetPathAttachesBearerAndReturnsDecodedBody() = runTest {
        val previousToken = AdminAuthController.idToken.value
        AdminAuthController.receiveIdToken("test-token")
        try {
            val repository = AdminContentRepositoryImpl(
                testHttpClient(
                    responseBody = """{"displayName":"Kei"}""",
                    expectedMethod = HttpMethod.Get,
                    expectedPath = "/api/profile",
                    verifyRequest = { request ->
                        assertEquals("Bearer test-token", request.headers[HttpHeaders.Authorization])
                    },
                ),
            )

            val actual = repository.fetchProfileDraft()

            assertEquals(AdminProfile(displayName = "Kei"), actual)
        } finally {
            previousToken?.let(AdminAuthController::receiveIdToken)
                ?: AdminAuthController.invalidateIdToken("test-token")
        }
    }

    @Test
    fun saveProfileDraftUsesPutPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"displayName":"Saved"}""",
                expectedMethod = HttpMethod.Put,
                expectedPath = "/api/profile",
            ),
        )

        val actual = repository.saveProfileDraft(AdminProfile())

        assertEquals(AdminProfile(displayName = "Saved"), actual)
    }

    @Test
    fun fetchTerminalDraftUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"commands":[{"keyword":"coffee"}]}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/terminal",
            ),
        )

        val actual = repository.fetchTerminalDraft()

        assertEquals(TerminalCommandsContent(commands = listOf(TerminalTextCommand(keyword = "coffee"))), actual)
    }

    @Test
    fun saveTerminalDraftUsesPutPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"commands":[{"keyword":"saved","lines":["done"]}]}""",
                expectedMethod = HttpMethod.Put,
                expectedPath = "/api/terminal",
            ),
        )

        val actual = repository.saveTerminalDraft(TerminalCommandsContent())

        assertEquals(
            TerminalCommandsContent(commands = listOf(TerminalTextCommand(keyword = "saved", lines = listOf("done")))),
            actual,
        )
    }

    @Test
    fun fetchReadmeDraftUsesGetPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"ja":[],"en":[]}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/readme",
            ),
        )

        val actual = repository.fetchReadmeDraft()

        assertEquals(ReadmeContent(), actual)
    }

    @Test
    fun saveReadmeDraftUsesPutPathAndReturnsDecodedBody() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"ja":[],"en":[]}""",
                expectedMethod = HttpMethod.Put,
                expectedPath = "/api/readme",
            ),
        )

        val actual = repository.saveReadmeDraft(ReadmeContent())

        assertEquals(ReadmeContent(), actual)
    }

    @Test
    fun nonSuccessfulResponseSurfacesAsAnException() = runTest {
        val repository = AdminContentRepositoryImpl(
            testHttpClient(
                responseBody = """{"works":[]}""",
                expectedMethod = HttpMethod.Get,
                expectedPath = "/api/works",
                status = HttpStatusCode.ServiceUnavailable,
                expectSuccess = true,
            ),
        )

        assertFails { repository.fetchWorksDraft() }
    }
}
