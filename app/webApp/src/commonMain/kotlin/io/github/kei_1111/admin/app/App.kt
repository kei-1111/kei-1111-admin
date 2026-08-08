package io.github.kei_1111.admin.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import io.github.kei_1111.admin.app.auth.AdminAuthController
import io.github.kei_1111.admin.app.core.common.coroutines.recoverOrElse
import io.github.kei_1111.admin.app.core.designsystem.AdminTheme
import io.github.kei_1111.admin.app.di.AppGraph
import io.github.kei_1111.admin.app.navigation.AppNavDisplay
import io.github.kei_1111.admin.shared.model.MeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

private const val STATUS_PADDING_DP = 8

@Suppress("ModifierMissing")
@Composable
fun App(appGraph: AppGraph) {
    val idToken by AdminAuthController.idToken.collectAsStateWithLifecycle()
    var sessionEmail by remember { mutableStateOf<String?>(null) }
    var sessionFailed by remember { mutableStateOf(false) }
    val httpClient = remember {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    LaunchedEffect(idToken) {
        val token = idToken ?: return@LaunchedEffect
        sessionFailed = false
        // 同一オリジン配信のため相対 URL で届く。失敗はサーバー検証拒否 or バックエンド不在(dev server)
        val me = recoverOrElse<MeResponse?>(
            {
                httpClient.get("/api/me") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }.body()
            },
        ) { null }
        if (me != null) {
            sessionEmail = me.email
            appGraph.interactionLog.i("Auth", "verified as ${me.email}")
        } else {
            sessionFailed = true
            appGraph.interactionLog.w("Auth", "server verification failed")
        }
    }

    CompositionLocalProvider(
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
    ) {
        AdminTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavDisplay(
                        interactionLog = appGraph.interactionLog,
                        navKeySerializers = appGraph.navKeySerializers,
                    )
                    SessionStatus(
                        email = sessionEmail,
                        failed = sessionFailed,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(STATUS_PADDING_DP.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionStatus(
    email: String?,
    failed: Boolean,
    modifier: Modifier = Modifier,
) {
    val text = when {
        email != null -> "Signed in: $email"
        failed -> "Verification failed"
        else -> return
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (failed) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = modifier,
    )
}
