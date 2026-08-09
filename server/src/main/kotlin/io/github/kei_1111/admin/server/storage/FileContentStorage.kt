package io.github.kei_1111.admin.server.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path

/** ローカル開発モード(DEV_AUTH_BYPASS)用。GCS の代わりにローカルディレクトリへ読み書きする。 */
class FileContentStorage(
    private val root: Path,
) : ContentStorage {

    override suspend fun read(path: String): String? = withContext(Dispatchers.IO) {
        val file = root.resolve(path)
        if (Files.exists(file)) Files.readString(file) else null
    }

    override suspend fun write(path: String, content: String) {
        withContext(Dispatchers.IO) {
            val file = root.resolve(path)
            Files.createDirectories(file.parent)
            Files.writeString(file, content)
        }
    }

    override suspend fun readBytes(path: String): ByteArray? = withContext(Dispatchers.IO) {
        val file = root.resolve(path)
        if (Files.exists(file)) Files.readAllBytes(file) else null
    }

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        withContext(Dispatchers.IO) {
            val file = root.resolve(path)
            Files.createDirectories(file.parent)
            Files.write(file, content)
        }
    }
}
