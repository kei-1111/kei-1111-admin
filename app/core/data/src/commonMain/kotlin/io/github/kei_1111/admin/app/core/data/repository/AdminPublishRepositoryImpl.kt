package io.github.kei_1111.admin.app.core.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.DiscardDraftRequest
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
internal class AdminPublishRepositoryImpl(
    private val httpClient: HttpClient,
) : AdminPublishRepository {

    override suspend fun fetchMeta(): ContentMeta =
        httpClient.get("/api/meta") { authorize() }.body()

    override suspend fun publish(): ContentMeta =
        httpClient.post("/api/publish") { authorize() }.body()

    override suspend fun fetchPublishedSnapshot(): PublishedSnapshot =
        httpClient.get("/api/published") { authorize() }.body()

    override suspend fun discardDraft(document: ContentDocument) {
        httpClient.post("/api/drafts/discard") {
            authorize()
            contentType(ContentType.Application.Json)
            setBody(DiscardDraftRequest(document = document))
        }
    }
}
