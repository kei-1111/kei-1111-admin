package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.content

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.NavTree
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions

/** Desktop レイアウト。Intent への変換はこの層まで — 配下のコンポーネントは値+コールバックのみ受け取る。 */
@Composable
internal fun WorkbenchDesktopContent(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        NavTree(
            state = state,
            onSelectNode = { onIntent(WorkbenchIntent.SelectNode(it)) },
            onClickCreateWork = { onIntent(WorkbenchIntent.CreateWork) },
            modifier = Modifier
                .width(WorkbenchDimensions.TreeWidth)
                .fillMaxSize(),
        )
        Spacer(modifier = Modifier.width(WorkbenchDimensions.IslandGap))
        WorkbenchMainIsland(
            state = state,
            onIntent = onIntent,
            isMobile = false,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        )
    }
}
