package io.github.kei_1111.admin.app.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.UploadedImage
import io.github.kei_1111.admin.shared.model.WorksContent
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
internal class AdminContentRepositoryImpl(
    private val httpClient: HttpClient,
) : AdminContentRepository {

    // 同一オリジン配信のため相対 URL で管理サーバーに届く
    override suspend fun fetchWorksDraft(): WorksContent =
        httpClient.get("/api/works") { authorize() }.body()

    override suspend fun saveWorksDraft(content: WorksContent): WorksContent =
        httpClient.put("/api/works") {
            authorize()
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun fetchProfileDraft(): AdminProfile =
        httpClient.get("/api/profile") { authorize() }.body()

    override suspend fun saveProfileDraft(profile: AdminProfile): AdminProfile =
        httpClient.put("/api/profile") {
            authorize()
            contentType(ContentType.Application.Json)
            setBody(profile)
        }.body()

    override suspend fun fetchMeta(): ContentMeta =
        httpClient.get("/api/meta") { authorize() }.body()

    override suspend fun publish(): ContentMeta =
        httpClient.post("/api/publish") { authorize() }.body()

    override suspend fun fetchPortfolioProfile(): GitHubProfile =
        httpClient.get("/api/preview/profile") { authorize() }.body()

    override suspend fun fetchPortfolioContributions(): ContributionCalendar =
        httpClient.get("/api/preview/contributions") { authorize() }.body()

    private fun HttpRequestBuilder.authorize() {
        AdminAuthController.idToken.value?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    override suspend fun uploadWorkImage(
        workId: String,
        fileName: String,
        mimeType: String,
        bytes: ByteArray,
    ): String = httpClient.submitFormWithBinaryData(
        url = "/api/images/works/$workId",
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
