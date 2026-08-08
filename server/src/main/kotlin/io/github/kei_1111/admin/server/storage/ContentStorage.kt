package io.github.kei_1111.admin.server.storage

/** コンテンツ置き場(GCS)の最小抽象。パスはバケット内オブジェクト名。 */
interface ContentStorage {
    suspend fun read(path: String): String?
    suspend fun write(path: String, content: String)
}
