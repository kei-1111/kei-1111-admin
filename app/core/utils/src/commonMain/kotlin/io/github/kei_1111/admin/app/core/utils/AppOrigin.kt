package io.github.kei_1111.admin.app.core.utils

/**
 * アプリを配信しているオリジン（例: `https://kei-1111.github.io`）。
 * 配布物に同梱した静的アセットの相対パスを絶対 URL へ解決するために使う。
 */
expect fun appOrigin(): String
