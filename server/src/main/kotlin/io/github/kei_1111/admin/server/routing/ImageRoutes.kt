package io.github.kei_1111.admin.server.routing

import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.shared.model.UploadedImage
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

/** 認証済みルート配下に置く(公開側の配信は Application が /images を直接返す)。 */
fun Route.imageRoutes(imageService: ImageService) {
    post("/api/images/works/{workId}") {
        val workId = call.parameters["workId"].orEmpty()
        if (workId.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        var uploaded: UploadedImage? = null
        var rejected: HttpStatusCode? = null
        call.receiveMultipart().forEachPart { part ->
            if (part is PartData.FileItem && uploaded == null && rejected == null) {
                val contentType = part.contentType?.toString().orEmpty()
                if (contentType !in ImageService.EXTENSIONS.keys) {
                    rejected = HttpStatusCode.UnsupportedMediaType
                } else {
                    // 上限+1 で打ち切り読みし、超過サイズを全量バッファリングしない
                    val bytes = part.provider()
                        .readRemaining((ImageService.MAX_IMAGE_BYTES + 1).toLong())
                        .readByteArray()
                    if (bytes.size > ImageService.MAX_IMAGE_BYTES) {
                        rejected = HttpStatusCode.PayloadTooLarge
                    } else if (!ImageService.matchesSignature(bytes, contentType)) {
                        rejected = HttpStatusCode.UnsupportedMediaType
                    } else {
                        val path = imageService.saveWorkImage(
                            workId = workId,
                            fileName = part.originalFileName ?: "screenshot",
                            content = bytes,
                            contentType = contentType,
                        )
                        uploaded = UploadedImage(path = path)
                    }
                }
            }
            part.dispose()
        }
        when {
            rejected != null -> call.respond(rejected)
            uploaded != null -> call.respond(uploaded)
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }
}

/** 公開側のスクリーンショット配信(認証不要)。Application の routing 直下で登録する。 */
fun Route.imageServingRoutes(imageService: ImageService) {
    get("/images/{path...}") {
        val relative = call.parameters.getAll("path")?.joinToString("/") ?: ""
        val image = imageService.readImage("images/$relative")
        if (image == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.response.headers.append("X-Content-Type-Options", "nosniff")
            call.respondBytes(image.bytes, ContentType.parse(image.contentType))
        }
    }
}
