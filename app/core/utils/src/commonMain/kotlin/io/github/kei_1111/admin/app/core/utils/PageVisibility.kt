package io.github.kei_1111.admin.app.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * 非表示タブでは Chrome が requestAnimationFrame を停止しリコンポジションも止まるため、
 * Boolean を直接返すと非表示への変化が呼び出し側へ伝播しない。イベントリスナーが直接
 * 書き込む [State] を返すことで、リコンポジションを介さず snapshotFlow から購読できる。
 */
@Composable
expect fun rememberIsPageVisible(): State<Boolean>
