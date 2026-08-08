package io.github.kei_1111.admin.server

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.github.kei_1111.admin.server.routing.contentRoutes
import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.storage.GcsContentStorage
import io.github.kei_1111.admin.shared.model.HealthResponse
import io.github.kei_1111.admin.shared.model.MeResponse
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
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
import java.net.URI
import java.util.concurrent.TimeUnit

private const val DEFAULT_PORT = 8082
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
    configureApplication(
        authConfig = AuthConfig(
            jwkProvider = JwkProviderBuilder(URI(GOOGLE_JWKS_URL).toURL())
                .cached(JWKS_CACHE_SIZE, JWKS_CACHE_HOURS, TimeUnit.HOURS)
                .build(),
            // 未設定なら空文字 = どのトークンの audience とも一致せず全拒否(fail closed)
            clientId = System.getenv("GOOGLE_OAUTH_CLIENT_ID").orEmpty(),
            allowedEmail = System.getenv("ADMIN_ALLOWED_EMAIL").orEmpty(),
        ),
        contentService = ContentService(
            storage = GcsContentStorage(bucket = System.getenv("CONTENT_BUCKET").orEmpty()),
        ),
    )
}

fun Application.configureApplication(
    authConfig: AuthConfig,
    contentService: ContentService,
) {
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
        authenticate("google") {
            get("/api/me") {
                val principal = requireNotNull(call.principal<JWTPrincipal>())
                call.respond(MeResponse(email = principal.payload.getClaim("email").asString()))
            }
            contentRoutes(contentService)
        }
        // デプロイビルドが -PbundleWebApp で同梱する管理 UI(同一オリジン配信で CORS 不要)。
        // 同梱なしのビルドでは何も配信しないだけで無害。
        staticResources("/", "static") {
            default("index.html")
        }
    }
}
