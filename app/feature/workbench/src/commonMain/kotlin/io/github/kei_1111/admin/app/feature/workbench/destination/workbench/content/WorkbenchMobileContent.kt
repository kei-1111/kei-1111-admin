package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState

/** Mobile レイアウト。ナビ行は Screen 側(TitleBar 直下)に置くため、ここは編集アイランドのみ。 */
@Composable
internal fun WorkbenchMobileContent(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    WorkbenchMainIsland(
        state = state,
        onIntent = onIntent,
        isMobile = true,
        modifier = modifier.fillMaxSize(),
    )
}
