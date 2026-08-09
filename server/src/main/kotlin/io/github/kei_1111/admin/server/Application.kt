package io.github.kei_1111.admin.server

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.github.kei_1111.admin.server.routing.contentRoutes
import io.github.kei_1111.admin.server.routing.imageRoutes
import io.github.kei_1111.admin.server.routing.imageServingRoutes
import io.github.kei_1111.admin.server.routing.previewRoutes
import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.ImageService
import io.github.kei_1111.admin.server.service.PortfolioPreviewService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.server.storage.FileContentStorage
import io.github.kei_1111.admin.server.storage.GcsContentStorage
import io.github.kei_1111.admin.shared.model.HealthResponse
import io.github.kei_1111.admin.shared.model.MeResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.net.URI
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import io.ktor.client.engine.cio.CIO as ClientCIO

private const val DEFAULT_PORT = 8082
private const val DEFAULT_PORTFOLIO_API_BASE = "https://kei-1111-server-672756196519.asia-northeast1.run.app"
private const val GOOGLE_ISSUER = "https://accounts.google.com"
private const val GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs"
private const val JWKS_CACHE_SIZE = 10L
private const val JWKS_CACHE_HOURS = 24L

data class AuthConfig(
    val jwkProvider: JwkProvider,
    val clientId: String,
    val allowedEmail: String,
)

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    embeddedServer(CIO, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // ローカル開発専用: 認証をスキップし、GCS の代わりにローカルファイルへ読み書きする。
    // Cloud Run のデプロイ設定(deploy-server.yml)はこの env を設定しない。
    val devAuthBypass = System.getenv("DEV_AUTH_BYPASS") == "true"
    configureApplication(
        authConfig = AuthConfig(
            jwkProvider = JwkProviderBuilder(URI(GOOGLE_JWKS_URL).toURL())
                .cached(JWKS_CACHE_SIZE, JWKS_CACHE_HOURS, TimeUnit.HOURS)
                .build(),
            // 未設定なら空文字 = どのトークンの audience とも一致せず全拒否(fail closed)
            clientId = System.getenv("GOOGLE_OAUTH_CLIENT_ID").orEmpty(),
            allowedEmail = System.getenv("ADMIN_ALLOWED_EMAIL").orEmpty(),
        ),
        contentService = ContentService(storage = defaultStorage(devAuthBypass)),
        imageService = ImageService(storage = defaultStorage(devAuthBypass)),
        previewService = defaultPortfolioPreviewService(),
        devAuthBypass = devAuthBypass,
    )
}

private fun defaultStorage(devAuthBypass: Boolean): ContentStorage = if (devAuthBypass) {
    FileContentStorage(root = Path.of("build/local-content"))
} else {
    GcsContentStorage(bucket = System.getenv("CONTENT_BUCKET").orEmpty())
}

private fun defaultPortfolioPreviewService(): PortfolioPreviewService {
    val base = System.getenv("PORTFOLIO_API_BASE") ?: DEFAULT_PORTFOLIO_API_BASE
    val client = HttpClient(ClientCIO)
    return PortfolioPreviewService { path ->
        // 本体 API の失敗は null に畳む(呼び出し側が 502 に変換)。キャンセルは握り潰さない
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        try {
            val response = client.get(base + path)
            if (response.status == HttpStatusCode.OK) response.bodyAsText() else null
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            null
        }
    }
}

@Suppress("LongMethod")
fun Application.configureApplication(
    authConfig: AuthConfig,
    contentService: ContentService,
    previewService: PortfolioPreviewService,
    imageService: ImageService,
    publishService: PublishService? = null,
    devAuthBypass: Boolean = false,
) {
    val publish = publishService ?: PublishService(
        storage = defaultStorage(devAuthBypass),
        contentService = contentService,
    )
    if (devAuthBypass) {
        log.warn("DEV_AUTH_BYPASS is enabled — API routes are UNAUTHENTICATED (local development only)")
    }
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    install(Authentication) {
        jwt("google") {
            verifier(authConfig.jwkProvider, GOOGLE_ISSUER) {
                withAudience(authConfig.clientId)
                withIssuer(GOOGLE_ISSUER)
            }
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                val emailVerified = credential.payload.getClaim("email_verified").asBoolean() ?: false
                // 単一管理者: 署名・audience が正当でも allowlist 外のアカウントは拒否する
                if (emailVerified && email == authConfig.allowedEmail) JWTPrincipal(credential.payload) else null
            }
        }
    }
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "OK"))
        }
        if (devAuthBypass) {
            get("/api/me") {
                call.respond(MeResponse(email = "dev@localhost"))
            }
            contentRoutes(contentService, publish)
            previewRoutes(previewService)
            imageRoutes(imageService)
        } else {
            authenticate("google") {
                get("/api/me") {
                    val principal = requireNotNull(call.principal<JWTPrincipal>())
                    call.respond(MeResponse(email = principal.payload.getClaim("email").asString()))
                }
                contentRoutes(contentService, publish)
                previewRoutes(previewService)
                imageRoutes(imageService)
            }
        }
        // スクリーンショット配信。Preview 表示と本体サイトからの参照を想定した公開読み出し
        imageServingRoutes(imageService)
        // デプロイビルドが -PbundleWebApp で同梱する管理 UI(同一オリジン配信で CORS 不要)。
        // 同梱なしのビルドでは何も配信しないだけで無害。
        staticResources("/", "static") {
            default("index.html")
        }
    }
}
