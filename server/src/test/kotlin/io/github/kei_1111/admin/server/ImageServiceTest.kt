package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.storage.ContentStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val IMAGE_NOW = 1723100000000L

private class ImageContentStorage : ContentStorage {
    val binaries = mutableMapOf<String, ByteArray>()
    val contentTypes = mutableMapOf<String, String>()

    override suspend fun read(path: String): String? = null

    override suspend fun write(path: String, content: String) = Unit

    override suspend fun readBytes(path: String): ByteArray? = binaries[path]

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        binaries[path] = content
        contentTypes[path] = contentType
    }
}

private fun pngBytes(): ByteArray = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)

class ImageServiceTest {

    @Test
    fun validPngSavesUnderTheWorkDirectory() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })
        val content = pngBytes()
        val expectedPath = "images/works/withmo/$IMAGE_NOW-shot.png"

        val result = service.saveWorkImage(
            workId = "withmo",
            fileName = "shot.png",
            content = content,
            contentType = "image/png",
        )

        assertEquals(ImageService.SaveResult.Saved(expectedPath), result)
        assertContentEquals(content, storage.binaries.getValue(expectedPath))
        assertEquals("image/png", storage.contentTypes[expectedPath])
    }

    @Test
    fun profileImageSavesUnderTheProfileDirectory() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })
        val content = pngBytes()
        val expectedPath = "images/profile/$IMAGE_NOW-avatar.png"

        val result = service.saveProfileImage(
            fileName = "avatar.png",
            content = content,
            contentType = "image/png",
        )

        assertEquals(ImageService.SaveResult.Saved(expectedPath), result)
        assertContentEquals(content, storage.binaries.getValue(expectedPath))
    }

    @Test
    fun unsupportedContentTypeWritesNothing() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })

        val result = service.saveWorkImage(
            workId = "withmo",
            fileName = "animation.gif",
            content = byteArrayOf(0x47, 0x49, 0x46),
            contentType = "image/gif",
        )

        assertEquals(ImageService.SaveResult.UnsupportedType, result)
        assertTrue(storage.binaries.isEmpty())
    }

    @Test
    fun bytesThatDoNotMatchTheDeclaredTypeWriteNothing() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })

        val result = service.saveWorkImage(
            workId = "withmo",
            fileName = "fake.png",
            content = "GIF89a".encodeToByteArray(),
            contentType = "image/png",
        )

        assertEquals(ImageService.SaveResult.UnsupportedType, result)
        assertTrue(storage.binaries.isEmpty())
    }

    @Test
    fun contentLargerThanTheMaximumWritesNothing() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })

        val result = service.saveWorkImage(
            workId = "withmo",
            fileName = "large.png",
            content = ByteArray(ImageService.MAX_IMAGE_BYTES + 1),
            contentType = "image/png",
        )

        assertEquals(ImageService.SaveResult.TooLarge, result)
        assertTrue(storage.binaries.isEmpty())
    }

    @Test
    fun allDotsWorkIdsCannotEscapeTheWorksDirectory() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })

        listOf(".", "..").forEach { workId ->
            val result = assertIs<ImageService.SaveResult.Saved>(
                service.saveWorkImage(
                    workId = workId,
                    fileName = "shot.png",
                    content = pngBytes(),
                    contentType = "image/png",
                ),
            )

            assertTrue(result.path.startsWith("images/works/"))
            assertFalse(result.path.split('/').contains(".."))
            assertEquals("images/works/image/$IMAGE_NOW-shot.png", result.path)
        }
    }

    @Test
    fun fileNameWithSeparatorsAndOddCharactersBecomesOneSafeSegment() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })

        val result = assertIs<ImageService.SaveResult.Saved>(
            service.saveWorkImage(
                workId = "withmo",
                fileName = "../../evil name.png",
                content = pngBytes(),
                contentType = "image/png",
            ),
        )

        assertEquals("images/works/withmo/$IMAGE_NOW-..-..-evil-name.png", result.path)
        assertEquals(4, result.path.split('/').size)
        assertFalse(result.path.split('/').contains(".."))
    }

    @Test
    fun readImageRejectsPathsOutsideTheImageConvention() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })
        val invalidPaths = listOf(
            "content/avatar.png",
            "images/profile/../avatar.png",
            "images/profile/avatar.gif",
        )
        invalidPaths.forEach { storage.binaries[it] = pngBytes() }

        invalidPaths.forEach { path ->
            assertNull(service.readImage(path))
        }
    }

    @Test
    fun readImageReturnsStoredBytesAndContentTypeForAValidPath() = runTest {
        val storage = ImageContentStorage()
        val service = ImageService(storage = storage, now = { IMAGE_NOW })
        val path = "images/profile/avatar.png"
        val content = pngBytes()
        storage.binaries[path] = content

        val image = assertIs<ImageService.StoredImage>(service.readImage(path))

        assertContentEquals(content, image.bytes)
        assertEquals("image/png", image.contentType)
    }
}
