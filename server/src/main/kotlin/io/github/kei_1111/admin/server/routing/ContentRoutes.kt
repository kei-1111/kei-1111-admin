package io.github.kei_1111.admin.server.routing

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.DiscardDraftRequest
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

/** HTTP 変換のみ。ポリシー(draft/published の分離)は ContentService が持つ。 */
fun Route.contentRoutes(contentService: ContentService, publishService: PublishService) {
    get("/api/works") {
        call.respond(contentService.worksDraft())
    }
    put("/api/works") {
        contentService.saveWorksDraft(call.receive<WorksContent>())
        call.respond(contentService.worksDraft())
    }
    get("/api/profile") {
        call.respond(contentService.profileDraft())
    }
    put("/api/profile") {
        contentService.saveProfileDraft(call.receive<AdminProfile>())
        call.respond(contentService.profileDraft())
    }
    get("/api/terminal") {
        call.respond(contentService.terminalDraft())
    }
    put("/api/terminal") {
        contentService.saveTerminalDraft(call.receive<TerminalCommandsContent>())
        call.respond(contentService.terminalDraft())
    }
    get("/api/readme") {
        call.respond(contentService.readmeDraft())
    }
    put("/api/readme") {
        contentService.saveReadmeDraft(call.receive<ReadmeContent>())
        call.respond(contentService.readmeDraft())
    }
    get("/api/published") {
        call.respond(publishService.publishedSnapshot())
    }
    post("/api/drafts/discard") {
        publishService.discardDraft(call.receive<DiscardDraftRequest>().document)
        call.respond(HttpStatusCode.NoContent)
    }
    get("/api/meta") {
        call.respond(publishService.meta())
    }
    post("/api/publish") {
        call.respond(publishService.publish())
    }
}
