package io.github.kei_1111.admin.app.core.utils

// Android ターゲットは Preview / ホストテスト専用で UI からは呼ばれない
actual suspend fun pickImageFile(): PickedImageFile? = null
