package io.github.kei_1111.admin.app.auth

private const val GOOGLE_OAUTH_CLIENT_ID =
    "672756196519-j3g8mu17pknvbdoiluomhui37fpm6uua.apps.googleusercontent.com"

fun installGoogleSignIn(onCredential: (String) -> Unit) {
    initGoogleSignIn(GOOGLE_OAUTH_CLIENT_ID, onCredential)
}

// GIS スクリプト(index.html で async ロード)の到着を待ってから初期化する。
// callback はオーバーレイを閉じてから ID トークン(response.credential)を Kotlin 側へ渡す。
// パラメータは js() ブロック内で参照される(detekt には見えない)。
@Suppress("UnusedParameter")
private fun initGoogleSignIn(clientId: String, onCredential: (String) -> Unit): Unit = js(
    """{
        const start = () => {
            const g = window.google;
            if (!g || !g.accounts || !g.accounts.id) { window.setTimeout(start, 100); return; }
            g.accounts.id.initialize({
                client_id: clientId,
                callback: (response) => {
                    const overlay = document.getElementById('signin-overlay');
                    if (overlay) overlay.style.display = 'none';
                    onCredential(response.credential);
                }
            });
            const button = document.getElementById('signin-button');
            if (button) g.accounts.id.renderButton(button, { theme: 'filled_black', size: 'large' });
        };
        start();
    }""",
)
