package io.github.kei_1111.admin.app.core.utils

/** ブラウザのファイル選択で得た画像。bytes は元ファイルのバイナリそのまま。 */
data class PickedImageFile(
    val name: String,
    val mimeType: String,
    val bytes: ByteArray,
)

/** 画像ファイル選択ダイアログを開く。未選択・キャンセル時は null。 */
expect suspend fun pickImageFile(): PickedImageFile?
