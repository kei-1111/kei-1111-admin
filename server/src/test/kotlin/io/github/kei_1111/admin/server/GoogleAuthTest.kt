package io.github.kei_1111.admin.server

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val CLIENT_ID = "test-client-id"
private const val ALLOWED_EMAIL = "admin@example.com"
private const val ISSUER = "https://accounts.google.com"
private const val KEY_ID = "test-key"
private const val ONE_MINUTE_MILLIS = 60_000L
private const val RSA_KEY_SIZE = 2048

class GoogleAuthTest {

    private val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(RSA_KEY_SIZE) }.generateKeyPair()
    private val publicKey = keyPair.public as RSAPublicKey
    private val privateKey = keyPair.private as RSAPrivateKey

    // 本物の Google JWKS の代わりに、テスト用鍵ペアの公開鍵を返すプロバイダ
    private val jwkProvider = JwkProvider { keyId ->
        val encoder = Base64.getUrlEncoder().withoutPadding()
        Jwk.fromValues(
            mapOf(
                "kid" to keyId,
                "kty" to "RSA",
                "alg" to "RS256",
                "use" to "sig",
                "n" to encoder.encodeToString(publicKey.modulus.toByteArray()),
                "e" to encoder.encodeToString(publicKey.publicExponent.toByteArray()),
            ),
        )
    }

    private fun token(
        audience: String = CLIENT_ID,
        email: String = ALLOWED_EMAIL,
        emailVerified: Boolean = true,
        expiresAt: Date = Date(System.currentTimeMillis() + ONE_MINUTE_MILLIS),
    ): String = JWT.create()
        .withKeyId(KEY_ID)
        .withIssuer(ISSUER)
        .withAudience(audience)
        .withClaim("email", email)
        .withClaim("email_verified", emailVerified)
        .withExpiresAt(expiresAt)
        .sign(Algorithm.RSA256(publicKey, privateKey))

    private fun authTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            configureApplication(
                AuthConfig(
                    jwkProvider = jwkProvider,
                    clientId = CLIENT_ID,
                    allowedEmail = ALLOWED_EMAIL,
                ),
            )
        }
        block()
    }

    @Test
    fun allowsTheAllowlistedAccountAndReturnsItsEmail() = authTestApplication {
        val response = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token()}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains(ALLOWED_EMAIL))
    }

    @Test
    fun rejectsRequestWithoutToken() = authTestApplication {
        val response = client.get("/api/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun rejectsTokenIssuedForAnotherAudience() = authTestApplication {
        val response = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token(audience = "other-client")}")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun rejectsAccountOutsideTheAllowlist() = authTestApplication {
        val response = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token(email = "intruder@example.com")}")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun rejectsUnverifiedEmail() = authTestApplication {
        val response = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token(emailVerified = false)}")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun rejectsExpiredToken() = authTestApplication {
        val expired = Date(System.currentTimeMillis() - ONE_MINUTE_MILLIS)

        val response = client.get("/api/me") {
            header(HttpHeaders.Authorization, "Bearer ${token(expiresAt = expired)}")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun keepsHealthPublic() = authTestApplication {
        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
