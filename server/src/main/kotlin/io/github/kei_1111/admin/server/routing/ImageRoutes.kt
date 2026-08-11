package io.github.kei_1111.admin.server.routing

import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.shared.model.UploadedImage
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
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
        receiveValidatedImage { fileName, bytes, contentType ->
            imageService.saveWorkImage(workId = workId, fileName = fileName, content = bytes, contentType = contentType)
        }
    }
    post("/api/images/profile") {
        receiveValidatedImage { fileName, bytes, contentType ->
            imageService.saveProfileImage(fileName = fileName, content = bytes, contentType = contentType)
        }
    }
}

/** multipart の先頭 FileItem を [save] へ渡し、その判定結果をステータスへ写像する。 */
private suspend fun RoutingContext.receiveValidatedImage(
    save: suspend (fileName: String, bytes: ByteArray, contentType: String) -> ImageService.SaveResult,
) {
    var result: ImageService.SaveResult? = null
    call.receiveMultipart().forEachPart { part ->
        try {
            if (part is PartData.FileItem && result == null) {
                // 上限+1 で打ち切り読みし、超過サイズを全量バッファリングしない(超過の判定自体は service 側)
                val bytes = part.provider()
                    .readRemaining((ImageService.MAX_IMAGE_BYTES + 1).toLong())
                    .readByteArray()
                result = save(part.originalFileName ?: "image", bytes, part.contentType?.toString().orEmpty())
            }
        } finally {
            part.release()
        }
    }
    when (val outcome = result) {
        null -> call.respond(HttpStatusCode.BadRequest)
        is ImageService.SaveResult.Saved -> call.respond(UploadedImage(path = outcome.path))
        ImageService.SaveResult.UnsupportedType -> call.respond(HttpStatusCode.UnsupportedMediaType)
        ImageService.SaveResult.TooLarge -> call.respond(HttpStatusCode.PayloadTooLarge)
    }
}

// 本体サイトは別オリジンから Coil(ブラウザ fetch)で画像を取るため、CORS 許可がないと表示できない
private val imageCorsOrigins = setOf("https://kei-1111.github.io", "http://localhost:8080")

/** 公開側のスクリーンショット配信(認証不要)。Application の routing 直下で登録する。 */
fun Route.imageServingRoutes(imageService: ImageService) {
    get("/images/{path...}") {
        val relative = call.parameters.getAll("path")?.joinToString("/") ?: ""
        val image = imageService.readImage("images/$relative")
        if (image == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.request.headers[HttpHeaders.Origin]
                ?.takeIf { it in imageCorsOrigins }
                ?.let { origin ->
                    call.response.headers.append(HttpHeaders.AccessControlAllowOrigin, origin)
                    call.response.headers.append(HttpHeaders.Vary, HttpHeaders.Origin)
                }
            call.response.headers.append("X-Content-Type-Options", "nosniff")
            call.respondBytes(image.bytes, ContentType.parse(image.contentType))
        }
    }
}
