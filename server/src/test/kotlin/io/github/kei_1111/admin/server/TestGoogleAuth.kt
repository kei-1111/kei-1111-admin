package io.github.kei_1111.admin.server

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.Date

internal const val TEST_CLIENT_ID = "test-client-id"
internal const val TEST_ALLOWED_EMAIL = "admin@example.com"
private const val TEST_ISSUER = "https://accounts.google.com"
private const val TEST_KEY_ID = "test-key"
private const val ONE_MINUTE_MILLIS = 60_000L
private const val RSA_KEY_SIZE = 2048

/** 各テストで共有する、本物の Google JWKS を置き換えるテスト用鍵ペアとトークン発行。 */
internal object TestGoogleAuth {
    private val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(RSA_KEY_SIZE) }.generateKeyPair()
    private val publicKey = keyPair.public as RSAPublicKey
    private val privateKey = keyPair.private as RSAPrivateKey

    val jwkProvider = JwkProvider { keyId ->
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

    val authConfig = AuthConfig(
        jwkProvider = jwkProvider,
        clientId = TEST_CLIENT_ID,
        allowedEmail = TEST_ALLOWED_EMAIL,
    )

    fun token(
        audience: String = TEST_CLIENT_ID,
        email: String = TEST_ALLOWED_EMAIL,
        emailVerified: Boolean = true,
        expiresAt: Date = Date(System.currentTimeMillis() + ONE_MINUTE_MILLIS),
    ): String = JWT.create()
        .withKeyId(TEST_KEY_ID)
        .withIssuer(TEST_ISSUER)
        .withAudience(audience)
        .withClaim("email", email)
        .withClaim("email_verified", emailVerified)
        .withExpiresAt(expiresAt)
        .sign(Algorithm.RSA256(publicKey, privateKey))
}
