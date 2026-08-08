package io.github.kei_1111.admin.app.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun <E> MviEffect(
    effect: E?,
    onConsume: () -> Unit,
    onHandle: (E) -> Unit,
) {
    val currentOnConsume by rememberUpdatedState(onConsume)
    val currentOnHandle by rememberUpdatedState(onHandle)

    effect?.let { currentEffect ->
        LaunchedEffect(currentEffect) {
            currentOnHandle(currentEffect)
            currentOnConsume()
        }
    }
}
