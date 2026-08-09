package io.github.kei_1111.admin.app.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
internal class AdminContentRepositoryImpl(
    private val httpClient: HttpClient,
) : AdminContentRepository {

    // 同一オリジン配信のため相対 URL で管理サーバーに届く
    override suspend fun fetchWorksDraft(): WorksContent =
        httpClient.get("/api/works") { authorize() }.body()

    override suspend fun saveWorksDraft(content: WorksContent): WorksContent = httpClient.putJsonAuthorized("/api/works", content)

    override suspend fun fetchProfileDraft(): AdminProfile =
        httpClient.get("/api/profile") { authorize() }.body()

    override suspend fun saveProfileDraft(profile: AdminProfile): AdminProfile = httpClient.putJsonAuthorized("/api/profile", profile)

    override suspend fun fetchMeta(): ContentMeta =
        httpClient.get("/api/meta") { authorize() }.body()

    override suspend fun publish(): ContentMeta =
        httpClient.post("/api/publish") { authorize() }.body()

    override suspend fun fetchTerminalDraft(): TerminalCommandsContent =
        httpClient.get("/api/terminal") { authorize() }.body()

    override suspend fun saveTerminalDraft(content: TerminalCommandsContent): TerminalCommandsContent =
        httpClient.putJsonAuthorized("/api/terminal", content)

    override suspend fun fetchReadmeDraft(): ReadmeContent =
        httpClient.get("/api/readme") { authorize() }.body()

    override suspend fun saveReadmeDraft(content: ReadmeContent): ReadmeContent =
        httpClient.putJsonAuthorized("/api/readme", content)
}
