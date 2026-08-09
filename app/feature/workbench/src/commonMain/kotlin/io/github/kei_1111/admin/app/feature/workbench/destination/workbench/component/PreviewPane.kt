@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.dashedBorder
import kotlinx.coroutines.delay
import kotlin.math.min

private const val REFRESH_DEBOUNCE_MILLIS = 600L

/**
 * Preview ペイン。中身は本番コンポーネント(WorksPreview / ProfilePreview)の
 * 共有モジュール化待ちのプレースホルダー。ヘッダの反映状態は本実装と同じ振る舞いをする。
 */
@Composable
internal fun PreviewPane(
    componentName: String,
    contentFingerprint: Any?,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = KeiTheme.colors
    var upToDate by remember { mutableStateOf(true) }

    LaunchedEffect(contentFingerprint) {
        upToDate = false
        delay(REFRESH_DEBOUNCE_MILLIS)
        upToDate = true
    }

    Column(
        modifier = modifier
            .clip(KeiTheme.shapes.card)
            .background(colors.islandDark)
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Preview — $componentName",
                style = KeiTheme.typography.chrome.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary,
                ),
                modifier = Modifier.weight(1f),
            )
            LanguageSegment(modifier = Modifier.padding(end = 8.dp))
            if (upToDate) {
                Text(
                    text = "◉ up to date",
                    style = KeiTheme.typography.chrome.copy(fontSize = 10.sp, color = colors.androidGreen),
                )
            } else {
                Text(
                    text = "⚠ Out of date",
                    style = KeiTheme.typography.chrome.copy(fontSize = 10.sp, color = colors.logcatWarning),
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        if (content != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                content()
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(KeiTheme.shapes.chip)
                    .dashedBorder(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "本番コンポーネント接続待ち",
                        style = KeiTheme.typography.cardJp.copy(fontSize = 12.sp, color = colors.mutedHigh),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "$componentName は本体サイトの\n共有モジュール化後に実物を埋め込む",
                        style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = colors.muted),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * 本家カード(固定寸法)をペインに収まるよう等倍縮小して中央表示する。
 * モックを別実装せず「実物のまま縮小表示」するための土台。
 */
@Composable
internal fun ScaledCard(
    cardWidth: Dp,
    cardHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val scale = min(maxWidth / cardWidth, maxHeight / cardHeight)
        Box(
            modifier = Modifier
                .requiredSize(cardWidth, cardHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewPanePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(420.dp, 640.dp).background(KeiTheme.colors.desk).padding(8.dp)) {
            PreviewPane(componentName = "WorksPreview", contentFingerprint = null)
        }
    }
}
