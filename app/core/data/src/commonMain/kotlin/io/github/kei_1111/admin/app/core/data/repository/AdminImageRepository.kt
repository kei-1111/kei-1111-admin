package io.github.kei_1111.admin.app.core.data.repository

/** 画像アップロード。返り値は配信パス(images/...)。 */
interface AdminImageRepository {
    suspend fun uploadWorkImage(workId: String, fileName: String, mimeType: String, bytes: ByteArray): String
    suspend fun uploadProfileImage(fileName: String, mimeType: String, bytes: ByteArray): String
}
