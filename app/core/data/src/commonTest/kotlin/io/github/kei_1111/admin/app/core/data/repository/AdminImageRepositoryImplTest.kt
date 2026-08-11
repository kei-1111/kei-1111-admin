package io.github.kei_1111.admin.app.core.data.repository

import io.ktor.http.HttpMethod
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AdminImageRepositoryImplTest {

    @Test
    fun uploadWorkImageUsesPostPathAndReturnsDecodedPath() = runTest {
        val repository = AdminImageRepositoryImpl(
            testHttpClient(
                responseBody = """{"path":"images/works/work-1/hero.png"}""",
                expectedMethod = HttpMethod.Post,
                expectedPath = "/api/images/works/work-1",
            ),
        )

        val actual = repository.uploadWorkImage(
            workId = "work-1",
            fileName = "hero.png",
            mimeType = "image/png",
            bytes = byteArrayOf(1, 2, 3),
        )

        assertEquals("images/works/work-1/hero.png", actual)
    }

    @Test
    fun uploadProfileImageUsesPostPathAndReturnsDecodedPath() = runTest {
        val repository = AdminImageRepositoryImpl(
            testHttpClient(
                responseBody = """{"path":"images/profile/avatar.webp"}""",
                expectedMethod = HttpMethod.Post,
                expectedPath = "/api/images/profile",
            ),
        )

        val actual = repository.uploadProfileImage(
            fileName = "avatar.webp",
            mimeType = "image/webp",
            bytes = byteArrayOf(4, 5, 6),
        )

        assertEquals("images/profile/avatar.webp", actual)
    }
}
