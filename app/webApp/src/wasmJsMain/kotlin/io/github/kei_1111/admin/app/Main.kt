package io.github.kei_1111.admin.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.zacsweers.metro.createGraph
import io.github.kei_1111.admin.app.auth.installGoogleSignIn
import io.github.kei_1111.admin.app.core.common.auth.AdminAuthController
import io.github.kei_1111.admin.app.di.AppGraph
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appGraph = createGraph<AppGraph>()
    // ローカル開発専用(?devauth): sign-in をスキップする。本番ではサーバーが本物のトークンを
    // 要求するため、このバイパスでは API に到達できない(DEV_AUTH_BYPASS サーバーとの組で使う)
    if (window.location.search.contains("devauth")) {
        AdminAuthController.receiveIdToken("dev-bypass")
        (document.getElementById("signin-overlay") as? HTMLElement)?.style?.display = "none"
    } else {
        installGoogleSignIn { token -> AdminAuthController.receiveIdToken(token) }
    }
    installWorkbenchShortcutListener()
    installBeforeUnloadGuard()

    // body 直マウントは既存の DOM(サインインオーバーレイ)ごと消されるため専用 div に載せる
    ComposeViewport(document.getElementById("compose-root")!!) {
        App(appGraph = appGraph)
    }
}
