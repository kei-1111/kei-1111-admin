@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchIntent
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab

@Composable
internal fun MainIsland(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.island)
            .background(KeiTheme.colors.island),
    ) {
        TabsRow(
            state = state,
            onIntent = onIntent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val tab = state.activeTab) {
                is WorkbenchTab.WorksList -> WorksListPage(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
                is WorkbenchTab.WorkEditor -> WorkEditorPage(
                    workId = tab.workId,
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
                is WorkbenchTab.ProfileEditor -> ProfileEditorPage(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TabsRow(
    state: WorkbenchState,
    onIntent: (WorkbenchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.openTabs.forEach { tab ->
            TabPill(
                label = tab.label(state),
                active = state.activeTab == tab,
                closable = tab != WorkbenchTab.WorksList || state.openTabs.size > 1,
                onClick = { onIntent(WorkbenchIntent.ActivateTab(tab)) },
                onClose = { onIntent(WorkbenchIntent.CloseTab(tab)) },
            )
        }
    }
}

private fun WorkbenchTab.label(state: WorkbenchState): String = when (this) {
    is WorkbenchTab.WorksList -> "Works"
    is WorkbenchTab.WorkEditor -> state.works.firstOrNull { it.id == workId }?.name ?: workId
    is WorkbenchTab.ProfileEditor -> "Profile"
}

@Composable
private fun TabPill(
    label: String,
    active: Boolean,
    closable: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    val (interactionSource, hovered) = hoverInteraction()
    val background = when {
        active -> colors.selectionPill
        hovered -> colors.chip
        else -> Color.Transparent
    }
    Row(
        modifier = modifier
            .height(WorkbenchDimensions.TabHeight)
            .clip(KeiTheme.shapes.pill)
            .background(background)
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = KeiTheme.typography.cardJp.copy(
                fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                color = colors.textPrimary,
            ),
        )
        if (closable) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(WorkbenchDimensions.ChromeIconSize)
                    .clip(KeiTheme.shapes.chip)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                KeiIcon(
                    icon = KeiTheme.icons.closeSmall,
                    contentDescription = "$label を閉じる",
                )
            }
        }
    }
}
