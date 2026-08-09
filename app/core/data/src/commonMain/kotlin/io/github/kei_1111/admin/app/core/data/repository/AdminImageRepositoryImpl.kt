package io.github.kei_1111.admin.app.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kei_1111.admin.shared.model.UploadedImage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
internal class AdminImageRepositoryImpl(
    private val httpClient: HttpClient,
) : AdminImageRepository {

    override suspend fun uploadWorkImage(
        workId: String,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String = uploadImage("/api/images/works/$workId", fileName, mimeType, bytes)

    override suspend fun uploadProfileImage(
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String = uploadImage("/api/images/profile", fileName, mimeType, bytes)

    private suspend fun uploadImage(
        url: String,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String = httpClient.submitFormWithBinaryData(
        url = url,
        formData = formData {
            append(
                key = "file",
                value = bytes,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, mimeType)
                    append(HttpHeaders.ContentDisposition, "filename=\"${fileName.replace("\"", "")}\"")
                },
            )
        },
    ) { authorize() }.body<UploadedImage>().path
}
