package io.github.kei_1111.admin.app.core.domain.usecase

import io.github.kei_1111.admin.app.core.data.repository.AdminImageRepository
import io.github.kei_1111.admin.app.core.utils.PickedImageFile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ImageUseCasesTest {

    // PickImageUseCaseImpl はプラットフォームの expect 関数へ委譲するだけで、
    // ホストテストの actual は常に null を返すため、ここで検証できる振る舞いがない。

    @Test
    fun uploadWorkImagePassesArgumentsAndReturnsRepositoryValue() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        val file = PickedImageFile(name = "hero.png", mimeType = "image/png", bytes = bytes)
        val repository = FakeAdminImageRepository(workResult = "images/works/work-1/hero.png")

        val actual = UploadWorkImageUseCaseImpl(repository)(workId = "work-1", file = file)

        assertEquals("work-1", repository.receivedWorkId)
        assertEquals("hero.png", repository.receivedFileName)
        assertEquals("image/png", repository.receivedMimeType)
        assertSame(bytes, repository.receivedBytes)
        assertEquals("images/works/work-1/hero.png", actual)
    }

    @Test
    fun uploadProfileImagePassesArgumentsAndReturnsRepositoryValue() = runTest {
        val bytes = byteArrayOf(4, 5, 6)
        val file = PickedImageFile(name = "avatar.webp", mimeType = "image/webp", bytes = bytes)
        val repository = FakeAdminImageRepository(profileResult = "images/profile/avatar.webp")

        val actual = UploadProfileImageUseCaseImpl(repository)(file)

        assertEquals("avatar.webp", repository.receivedFileName)
        assertEquals("image/webp", repository.receivedMimeType)
        assertSame(bytes, repository.receivedBytes)
        assertEquals("images/profile/avatar.webp", actual)
    }
}

private class FakeAdminImageRepository(
    private val workResult: String = "",
    private val profileResult: String = "",
) : AdminImageRepository {
    var receivedWorkId: String? = null
    var receivedFileName: String? = null
    var receivedMimeType: String? = null
    var receivedBytes: ByteArray? = null

    override suspend fun uploadWorkImage(
        workId: String,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String {
        receivedWorkId = workId
        receivedFileName = fileName
        receivedMimeType = mimeType
        receivedBytes = bytes
        return workResult
    }

    override suspend fun uploadProfileImage(
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String {
        receivedFileName = fileName
        receivedMimeType = mimeType
        receivedBytes = bytes
        return profileResult
    }
}
