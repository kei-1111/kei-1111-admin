package io.github.kei_1111.admin.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.zacsweers.metro.createGraph
import io.github.kei_1111.admin.app.auth.installGoogleSignIn
import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.app.di.AppGraph
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appGraph = createGraph<AppGraph>()
    installGoogleSignIn { token -> AdminAuthController.receiveIdToken(token) }
    installWorkbenchShortcutListener()

    // body 直マウントは既存の DOM(サインインオーバーレイ)ごと消されるため専用 div に載せる
    ComposeViewport(document.getElementById("compose-root")!!) {
        App(appGraph = appGraph)
    }
}
