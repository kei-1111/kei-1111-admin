package io.github.kei_1111.admin.server.routing

import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get

/** 本体サイト API のパススルー。JSON は解釈せずそのまま返す(契約は本体サイトが所有)。 */
fun Route.previewRoutes(previewService: PortfolioPreviewService) {
    get("/api/preview/profile") {
        respondProxied(previewService.profileJson())
    }
    get("/api/preview/contributions") {
        respondProxied(previewService.contributionsJson())
    }
    get("/api/preview/issues") {
        respondProxied(previewService.issuesJson())
    }
}

private suspend fun RoutingContext.respondProxied(json: String?) {
    if (json == null) {
        call.respondText(
            text = """{"error":"portfolio API unavailable"}""",
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.BadGateway,
        )
    } else {
        call.respondText(text = json, contentType = ContentType.Application.Json)
    }
}
