package io.github.kei_1111.admin.app.core.utils

private fun userAgent(): String = js("navigator.userAgent")

actual fun visitorDeviceLabel(): String {
    val ua = userAgent()
    return listOfNotNull(detectBrowser(ua), detectOs(ua)?.let { "($it)" }, "wasmJs").joinToString(" ")
}

/** 派生ブラウザが Chrome/Safari を名乗るため、固有トークンを先に判定する。 */
private fun detectBrowser(ua: String): String = when {
    "Edg/" in ua -> versionedName(ua, "Edge", "Edg/")
    "OPR/" in ua -> versionedName(ua, "Opera", "OPR/")
    "SamsungBrowser/" in ua -> versionedName(ua, "Samsung Internet", "SamsungBrowser/")
    "Chrome/" in ua -> versionedName(ua, "Chrome", "Chrome/")
    "Firefox/" in ua -> versionedName(ua, "Firefox", "Firefox/")
    "Safari/" in ua && "Version/" in ua -> versionedName(ua, "Safari", "Version/")
    else -> "Web Browser"
}

private fun versionedName(ua: String, name: String, token: String): String {
    val major = Regex("${Regex.escape(token)}(\\d+)").find(ua)?.groupValues?.get(1)
    return if (major != null) "$name $major" else name
}

private fun detectOs(ua: String): String? = when {
    "Windows" in ua -> "Windows"
    "CrOS" in ua -> "ChromeOS"
    "iPhone" in ua || "iPod" in ua -> "iOS"
    "iPad" in ua -> "iPadOS"
    // Android は端末モデル名が取れればそれを出す（実 AS のデバイス名に相当）
    "Android" in ua -> androidModel(ua) ?: "Android"
    "Macintosh" in ua || "Mac OS X" in ua -> "macOS"
    "Linux" in ua -> "Linux"
    else -> null
}

private fun androidModel(ua: String): String? =
    Regex("Android [^;)]+; ([^;)]+)\\)").find(ua)
        ?.groupValues?.get(1)
        ?.substringBefore(" Build")
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
