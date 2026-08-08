package io.github.kei_1111.admin.server.routing

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.WorksContent
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

/** HTTP 変換のみ。ポリシー(draft/published の分離)は ContentService が持つ。 */
fun Route.contentRoutes(contentService: ContentService) {
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
    get("/api/meta") {
        call.respond(contentService.meta())
    }
    post("/api/publish") {
        call.respond(contentService.publish())
    }
}
