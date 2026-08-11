package io.github.kei_1111.admin.server.service

import io.github.kei_1111.admin.server.storage.ContentStorage

/** 作品スクリーンショットの保存と配信読み出し。パス規約と受け入れ検証はここに閉じる。 */
class ImageService(
    private val storage: ContentStorage,
    private val now: () -> Long = { System.currentTimeMillis() },
) {
    /** 保存の結果。ルーティングはこれを HTTP ステータスへ写像するだけでよい。 */
    sealed interface SaveResult {
        data class Saved(val path: String) : SaveResult

        /** content-type が許可外、または実バイナリが申告 content-type と一致しない。 */
        data object UnsupportedType : SaveResult
        data object TooLarge : SaveResult
    }

    /** 検証して保存し、配信パス(images/works/...)を返す。 */
    suspend fun saveWorkImage(workId: String, fileName: String, content: ByteArray, contentType: String): SaveResult =
        saveImage(
            directory = "images/works/${sanitize(workId)}",
            fileName = fileName,
            content = content,
            contentType = contentType,
        )

    /** プロフィールアバターの検証と保存。配信パス(images/profile/...)を返す。 */
    suspend fun saveProfileImage(fileName: String, content: ByteArray, contentType: String): SaveResult =
        saveImage(directory = "images/profile", fileName = fileName, content = content, contentType = contentType)

    private suspend fun saveImage(
        directory: String,
        fileName: String,
        content: ByteArray,
        contentType: String,
    ): SaveResult {
        val extension = EXTENSIONS[contentType]
        return when {
            extension == null -> SaveResult.UnsupportedType
            content.size > MAX_IMAGE_BYTES -> SaveResult.TooLarge
            !matchesSignature(content, contentType) -> SaveResult.UnsupportedType
            else -> {
                val path = "$directory/${now()}-${sanitize(fileName.substringBeforeLast('.'))}.$extension"
                storage.writeBytes(path, content, contentType)
                SaveResult.Saved(path)
            }
        }
    }

    /** 配信用読み出し。パス規約外(トラバーサル等)は null。 */
    suspend fun readImage(path: String): StoredImage? {
        val valid = path.startsWith("images/") && !path.contains("..")
        val contentType = EXTENSIONS.entries.firstOrNull { path.endsWith(".${it.value}") }?.key
        val bytes = if (valid && contentType != null) storage.readBytes(path) else null
        return if (bytes == null || contentType == null) null else StoredImage(bytes = bytes, contentType = contentType)
    }

    data class StoredImage(val bytes: ByteArray, val contentType: String)

    companion object {
        val EXTENSIONS: Map<String, String> = mapOf(
            "image/png" to "png",
            "image/jpeg" to "jpg",
            "image/webp" to "webp",
        )
        private const val MAX_IMAGE_MEGABYTES = 5
        private const val BYTES_PER_KILOBYTE = 1024
        const val MAX_IMAGE_BYTES: Int = MAX_IMAGE_MEGABYTES * BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE

        private const val MAX_NAME_LENGTH = 64
        private const val WEBP_TAG_OFFSET = 8

        private val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte())
        private val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
        private val RIFF_TAG = "RIFF".encodeToByteArray()
        private val WEBP_TAG = "WEBP".encodeToByteArray()

        /** 申告 content-type と実バイナリの先頭シグネチャの一致を検証する(偽装対策)。 */
        private fun matchesSignature(bytes: ByteArray, contentType: String): Boolean = when (contentType) {
            "image/png" -> bytes.startsWith(PNG_SIGNATURE)
            "image/jpeg" -> bytes.startsWith(JPEG_SIGNATURE)
            "image/webp" ->
                bytes.startsWith(RIFF_TAG) &&
                    bytes.size > WEBP_TAG_OFFSET + WEBP_TAG.lastIndex &&
                    bytes.sliceArray(WEBP_TAG_OFFSET until WEBP_TAG_OFFSET + WEBP_TAG.size).contentEquals(WEBP_TAG)
            else -> false
        }

        private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
            size >= prefix.size && sliceArray(prefix.indices).contentEquals(prefix)

        // "." / ".." だけの値はファイルパスとして解決されうるため、そのまま名前に使わない
        private fun sanitize(value: String): String = value
            .replace(Regex("[^A-Za-z0-9._-]"), "-")
            .take(MAX_NAME_LENGTH)
            .takeUnless { it.isEmpty() || it.all { char -> char == '.' } }
            ?: "image"
    }
}
