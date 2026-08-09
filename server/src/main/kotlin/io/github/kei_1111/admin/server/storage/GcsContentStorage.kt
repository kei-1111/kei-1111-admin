package io.github.kei_1111.admin.server.storage

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Cloud Run のサービスアカウント(ADC)で認証する GCS 実装。クライアントはブロッキングのため IO へ逃がす。 */
class GcsContentStorage(
    private val bucket: String,
    private val storage: Storage = StorageOptions.getDefaultInstance().service,
) : ContentStorage {

    override suspend fun read(path: String): String? = withContext(Dispatchers.IO) {
        storage.get(BlobId.of(bucket, path))?.getContent()?.decodeToString()
    }

    override suspend fun write(path: String, content: String) {
        withContext(Dispatchers.IO) {
            val blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, path))
                .setContentType("application/json")
                .build()
            storage.create(blobInfo, content.encodeToByteArray())
        }
    }

    override suspend fun readBytes(path: String): ByteArray? = withContext(Dispatchers.IO) {
        storage.get(BlobId.of(bucket, path))?.getContent()
    }

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        withContext(Dispatchers.IO) {
            val blobInfo = BlobInfo.newBuilder(BlobId.of(bucket, path))
                .setContentType(contentType)
                .build()
            storage.create(blobInfo, content)
        }
    }
}
