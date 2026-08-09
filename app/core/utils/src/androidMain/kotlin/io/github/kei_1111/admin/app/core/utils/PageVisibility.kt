package io.github.kei_1111.admin.app.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// Android ターゲットは IDE の Compose Preview 描画専用（配布物は wasmJs のみ）。
// 静的プレビューは常に表示状態として扱ってよいため、恒真の State を返す。
@Composable
actual fun rememberIsPageVisible(): State<Boolean> = remember { mutableStateOf(true) }
