package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.storage.FileContentStorage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileContentStorageTest {

    @field:TempDir
    lateinit var root: Path

    @Test
    fun textWriteAndReadRoundTripIncludesNestedPaths() = runTest {
        val storage = FileContentStorage(root = root)
        val content = """{"name":"nested"}"""

        storage.write("content/draft/works.json", content)

        assertEquals(content, storage.read("content/draft/works.json"))
    }

    @Test
    fun readingMissingTextReturnsNull() = runTest {
        val storage = FileContentStorage(root = root)

        assertNull(storage.read("content/missing.json"))
    }

    @Test
    fun byteWriteAndReadRoundTripIncludesNestedPaths() = runTest {
        val storage = FileContentStorage(root = root)
        val content = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)

        storage.writeBytes("images/works/withmo/shot.png", content, "image/png")

        assertContentEquals(content, storage.readBytes("images/works/withmo/shot.png"))
    }

    @Test
    fun readingMissingBytesReturnsNull() = runTest {
        val storage = FileContentStorage(root = root)

        assertNull(storage.readBytes("images/missing.png"))
    }
}
