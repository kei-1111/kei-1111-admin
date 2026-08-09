@file:Suppress("MagicNumber", "UnusedPrivateMember")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.preview

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiIcon
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.utils.prefersReducedMotion
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchAnimations
import io.github.kei_1111.admin.app.feature.workbench.preview.PreviewContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.ContributionDay
import kotlin.math.roundToInt

private const val DAYS_PER_WEEK = 7
private const val DEFAULT_WEEKS = 53
private const val MAX_LEVEL = 4

/** GitHub 実物と同じ 週×7日 のコントリビューショングリッド＋凡例。 */
@Composable
internal fun ContributionsSection(
    calendar: ContributionCalendar?,
    failed: Boolean,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        SectionLabel(text = "CONTRIBUTIONS — LAST YEAR")
        ContributionGrid(days = calendar?.days.orEmpty(), isLoading = calendar == null && !failed, failed = failed)
        ContributionFooter(calendar = calendar, failed = failed, onClickRetry = onClickRetry)
    }
}

@Composable
private fun ContributionFooter(
    calendar: ContributionCalendar?,
    failed: Boolean,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            calendar != null -> ContributionCount(total = calendar.totalLastYear)
            failed -> ContributionFailedText(onClickRetry = onClickRetry)
            else -> ContributionLoadingText()
        }
        Spacer(modifier = Modifier.weight(1f))
        LegendRow()
    }
}

@Composable
private fun ContributionFailedText(
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        KeiIcon(
            icon = KeiTheme.icons.warning,
            contentDescription = null,
            modifier = Modifier.size(9.dp),
        )
        Spacer(modifier = Modifier.size(3.dp))
        Text(
            text = "failed — ",
            style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
        )
        Text(
            text = "retry",
            modifier = Modifier.clickable(
                onClickLabel = "コントリビューションの再取得",
                onClick = onClickRetry,
            ),
            style = KeiTheme.typography.chrome.copy(
                fontSize = 7.sp,
                color = KeiTheme.colors.syntaxLink,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@Composable
private fun ContributionCount(
    total: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "${total.withThousandsSeparator()} contributions in the last year",
        style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
    )
}

@Composable
private fun ContributionLoadingText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Fetching contributions…",
        style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
    )
}

private fun Int.withThousandsSeparator(): String =
    toString().reversed().chunked(3).joinToString(",").reversed()

@Composable
private fun ContributionGrid(
    days: List<ContributionDay>,
    isLoading: Boolean,
    failed: Boolean,
    modifier: Modifier = Modifier,
) {
    var hoveredIndex by remember(days) { mutableStateOf(-1) }
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val weeks = if (days.isEmpty()) {
            DEFAULT_WEEKS
        } else {
            (days.size + DAYS_PER_WEEK - 1) / DAYS_PER_WEEK
        }
        val step = maxWidth / weeks
        val density = LocalDensity.current
        val stepPx = with(density) { step.toPx() }
        val pulseAlpha = contributionsPulseAlpha(isLoading = isLoading, failed = failed)
        ContributionCells(
            days = days,
            stepPx = stepPx,
            height = step * DAYS_PER_WEEK,
            onChangeHoveredIndex = { hoveredIndex = it },
            modifier = Modifier.graphicsLayer { alpha = pulseAlpha.value },
        )
        days.getOrNull(hoveredIndex)?.let { day ->
            CellTooltip(day = day, hoveredIndex = hoveredIndex, stepPx = stepPx)
        }
    }
}

/** 毎フレームの再コンポーズを避けるため State のまま返し、graphicsLayer の描画時に読む。 */
@Composable
private fun contributionsPulseAlpha(isLoading: Boolean, failed: Boolean): State<Float> {
    if (!isLoading && !failed) return rememberUpdatedState(1f)
    val isReducedMotion = remember { prefersReducedMotion() }
    return if (failed || isReducedMotion) {
        rememberUpdatedState(0.7f)
    } else {
        rememberInfiniteTransition(label = "ContributionsPulse").animateFloat(
            initialValue = 1f,
            targetValue = 0.45f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = WorkbenchAnimations.ContributionsPulseMillis,
                    easing = EaseInOut,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "ContributionsPulseAlpha",
        )
    }
}

@Composable
private fun ContributionCells(
    days: List<ContributionDay>,
    stepPx: Float,
    height: Dp,
    onChangeHoveredIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val levelColors = KeiTheme.colors.contributionLevels
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(days, stepPx) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Move, PointerEventType.Enter -> {
                                val position = event.changes.first().position
                                val col = (position.x / stepPx).toInt()
                                val row = (position.y / stepPx).toInt()
                                val index = col * DAYS_PER_WEEK + row
                                onChangeHoveredIndex(
                                    if (row in 0 until DAYS_PER_WEEK && index in days.indices) index else -1,
                                )
                            }

                            PointerEventType.Exit -> onChangeHoveredIndex(-1)
                        }
                    }
                }
            },
    ) {
        val cellSize = stepPx * 0.72f
        val cornerRadius = CornerRadius(cellSize * 0.3f)
        val total = if (days.isEmpty()) DEFAULT_WEEKS * DAYS_PER_WEEK else days.size
        for (index in 0 until total) {
            val level = days.getOrNull(index)?.level?.coerceIn(0, MAX_LEVEL) ?: 0
            drawRoundRect(
                color = levelColors[level],
                topLeft = Offset(
                    x = index / DAYS_PER_WEEK * stepPx,
                    y = index % DAYS_PER_WEEK * stepPx,
                ),
                size = Size(cellSize, cellSize),
                cornerRadius = cornerRadius,
            )
        }
    }
}

@Composable
private fun CellTooltip(
    day: ContributionDay,
    hoveredIndex: Int,
    stepPx: Float,
) {
    val density = LocalDensity.current
    val gapPx = with(density) { 4.dp.toPx() }
    Popup(
        offset = IntOffset(
            x = (hoveredIndex / DAYS_PER_WEEK * stepPx).roundToInt(),
            y = ((hoveredIndex % DAYS_PER_WEEK + 1) * stepPx + gapPx).roundToInt(),
        ),
    ) {
        Box(
            modifier = Modifier
                .clip(KeiTheme.shapes.chip)
                .background(KeiTheme.colors.selectionPill)
                .padding(horizontal = 6.dp, vertical = 3.dp),
        ) {
            Text(
                text = "${day.count} contributions on ${day.date}",
                style = KeiTheme.typography.chrome.copy(fontSize = 8.sp, color = KeiTheme.colors.textPrimary),
            )
        }
    }
}

@Composable
private fun LegendRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Less",
            style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
        )
        KeiTheme.colors.contributionLevels.forEach { color ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(color),
            )
        }
        Text(
            text = "More",
            style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

@Preview
@Composable
private fun ContributionsSectionPreview() {
    KeiTheme {
        Box(
            modifier = Modifier
                .background(KeiTheme.colors.cardBackground)
                .padding(20.dp),
        ) {
            ContributionsSection(calendar = PreviewContributionCalendar, failed = false, onClickRetry = {})
        }
    }
}

@Preview
@Composable
private fun ContributionsSectionLoadingPreview() {
    KeiTheme {
        Box(
            modifier = Modifier
                .background(KeiTheme.colors.cardBackground)
                .padding(20.dp),
        ) {
            ContributionsSection(calendar = null, failed = false, onClickRetry = {})
        }
    }
}

@Preview
@Composable
private fun ContributionsSectionFailedPreview() {
    KeiTheme {
        Box(
            modifier = Modifier
                .background(KeiTheme.colors.cardBackground)
                .padding(20.dp),
        ) {
            ContributionsSection(calendar = null, failed = true, onClickRetry = {})
        }
    }
}
