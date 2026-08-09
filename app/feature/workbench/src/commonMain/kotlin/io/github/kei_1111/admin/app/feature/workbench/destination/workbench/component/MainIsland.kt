@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.WorkbenchTab
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.Work

@Composable
@Suppress("LongParameterList")
internal fun MainIsland(
    state: WorkbenchState,
    isMobile: Boolean,
    onClickTab: (WorkbenchTab) -> Unit,
    onCloseTab: (WorkbenchTab) -> Unit,
    onSelectWork: (String) -> Unit,
    onClickCreateWork: () -> Unit,
    onClickDeleteWork: (String) -> Unit,
    onChangeWork: (Work) -> Unit,
    onClickAddScreenshot: (String) -> Unit,
    onChangeProfile: (AdminProfile) -> Unit,
    onClickRetryPreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.island)
            .background(KeiTheme.colors.island),
    ) {
        TabsRow(
            state = state,
            onClickTab = onClickTab,
            onCloseTab = onCloseTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val tab = state.activeTab) {
                is WorkbenchTab.WorksList -> WorksListPage(
                    state = state,
                    isMobile = isMobile,
                    onSelectWork = onSelectWork,
                    onClickCreateWork = onClickCreateWork,
                    onClickDeleteWork = onClickDeleteWork,
                    modifier = Modifier.fillMaxSize(),
                )
                is WorkbenchTab.WorkEditor -> WorkEditorPage(
                    workId = tab.workId,
                    state = state,
                    isMobile = isMobile,
                    onChangeWork = onChangeWork,
                    onClickAddScreenshot = onClickAddScreenshot,
                    modifier = Modifier.fillMaxSize(),
                )
                is WorkbenchTab.ProfileEditor -> ProfileEditorPage(
                    state = state,
                    isMobile = isMobile,
                    onChangeProfile = onChangeProfile,
                    onClickRetryPreview = onClickRetryPreview,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TabsRow(
    state: WorkbenchState,
    onClickTab: (WorkbenchTab) -> Unit,
    onCloseTab: (WorkbenchTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.openTabs.forEach { tab ->
            TabPill(
                tab = tab,
                label = tab.label(state),
                active = state.activeTab == tab,
                closable = tab != WorkbenchTab.WorksList || state.openTabs.size > 1,
                onClick = { onClickTab(tab) },
                onClose = { onCloseTab(tab) },
            )
        }
    }
}

private fun WorkbenchTab.label(state: WorkbenchState): String = when (this) {
    is WorkbenchTab.WorksList -> "works"
    is WorkbenchTab.WorkEditor -> state.works.firstOrNull { it.id == workId }?.name ?: workId
    is WorkbenchTab.ProfileEditor -> "profile"
}

/** 本家 EditorTab と同じ様式: 選択タブは tabSelected + ボーダー、✕ は選択中かホバー時のみ。 */
@Suppress("LongParameterList")
@Composable
private fun TabPill(
    tab: WorkbenchTab,
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
        active -> colors.tabSelected
        hovered -> colors.chip
        else -> Color.Transparent
    }
    Row(
        modifier = modifier
            .clip(KeiTheme.shapes.row)
            .background(background)
            .then(
                if (active) {
                    Modifier.border(1.dp, colors.tabSelectedBorder, KeiTheme.shapes.row)
                } else {
                    Modifier
                },
            )
            .hoverable(interactionSource)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeiIcon(
                icon = tab.fileIcon(),
                contentDescription = null,
                modifier = Modifier.size(WorkbenchDimensions.ChromeIconSize),
            )
            Text(
                text = label,
                style = KeiTheme.typography.chrome.copy(
                    fontSize = WorkbenchDimensions.ChromeLabelFontSize,
                    color = if (active) colors.textPrimary else colors.textSecondary,
                ),
            )
        }
        if (closable && (active || hovered)) {
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
        } else {
            Spacer(modifier = Modifier.size(WorkbenchDimensions.ChromeIconSize))
        }
    }
}

@Composable
private fun WorkbenchTab.fileIcon() = when (this) {
    is WorkbenchTab.WorksList -> KeiTheme.icons.markdown
    is WorkbenchTab.WorkEditor -> KeiTheme.icons.kotlin
    is WorkbenchTab.ProfileEditor -> KeiTheme.icons.properties
}

@Preview
@Composable
private fun MainIslandPreview() {
    KeiTheme {
        Box(modifier = Modifier.size(960.dp, 640.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            MainIsland(
                state = PreviewWorkbenchState,
                isMobile = false,
                onClickTab = {},
                onCloseTab = {},
                onSelectWork = {},
                onClickCreateWork = {},
                onClickDeleteWork = {},
                onChangeWork = {},
                onClickAddScreenshot = {},
                onChangeProfile = {},
                onClickRetryPreview = {},
            )
        }
    }
}
