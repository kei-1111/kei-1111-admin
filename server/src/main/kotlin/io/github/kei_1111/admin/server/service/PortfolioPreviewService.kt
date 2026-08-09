package io.github.kei_1111.admin.server.service

/**
 * Preview カードが使う GitHub 由来データ(プロフィール統計・Contributions)を
 * 本体サイトの公開 API から取り寄せる。fetch は失敗を null に畳む契約
 * (実装はネットワーク層が持ち、テストはここへフェイクを注入する)。
 */
class PortfolioPreviewService(
    private val fetchJson: suspend (path: String) -> String?,
) {
    suspend fun profileJson(): String? = fetchJson("/api/profile")

    suspend fun contributionsJson(): String? = fetchJson("/api/contributions")
}
