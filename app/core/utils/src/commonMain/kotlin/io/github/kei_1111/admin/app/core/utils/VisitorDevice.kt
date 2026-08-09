package io.github.kei_1111.admin.app.core.utils

/**
 * Logcat のデバイスセレクタに表示する、サイトを開いている環境のラベル。
 * wasmJs では User-Agent から「ブラウザ 主バージョン (OS) wasmJs」を組み立てる。
 */
expect fun visitorDeviceLabel(): String
