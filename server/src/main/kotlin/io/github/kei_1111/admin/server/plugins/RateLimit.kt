package io.github.kei_1111.admin.server.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.request.header
import kotlin.time.Duration.Companion.minutes

internal const val API_RATE_LIMIT_PER_MINUTE = 60

internal val ApiRateLimiterName = RateLimitName("api")

/**
 * 管理 API 用のレート制限。Cloud Run は --allow-unauthenticated で公開されており、
 * 未認証リクエストでも JWT 検証と GCS アクセスのコストが発生するため入口で絞る。
 * UI の静的配信と公開画像配信には掛けない(1ページで多数のアセットを取得するため)。
 */
fun Application.configureRateLimit() {
    install(RateLimit) {
        register(ApiRateLimiterName) {
            rateLimiter(limit = API_RATE_LIMIT_PER_MINUTE, refillPeriod = 1.minutes)
            requestKey { call ->
                // Cloud Run のフロントエンドが末尾に追加するクライアント IP のみを信頼する。
                call.request.header(HttpHeaders.XForwardedFor)
                    ?.substringAfterLast(',')
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: call.request.origin.remoteHost
            }
        }
    }
}
