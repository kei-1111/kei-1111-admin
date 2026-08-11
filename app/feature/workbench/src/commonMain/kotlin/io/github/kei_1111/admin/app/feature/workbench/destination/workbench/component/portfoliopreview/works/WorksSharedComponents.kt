@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.portfoliopreview.works

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.core.designsystem.theme.brandColor
import io.github.kei_1111.admin.app.core.designsystem.theme.icon
import io.github.kei_1111.admin.app.core.ui.rememberHoverState
import io.github.kei_1111.admin.app.core.utils.appOrigin
import io.github.kei_1111.admin.shared.model.portfolio.LinkServiceType
import io.github.kei_1111.admin.shared.model.portfolio.WorkTag
import kei_1111_admin.app.feature.workbench.generated.resources.Res
import kei_1111_admin.app.feature.workbench.generated.resources.ic_play_store
import org.jetbrains.compose.resources.painterResource

// admin アップロード規約のパス。それ以外の相対パスは本体サイト同梱アセットとして本番オリジンから読む
private val adminUploadedAssetPattern = Regex("^images/(?:works/[^/]+|profile)/.+")

private const val PORTFOLIO_SITE_ORIGIN = "https://kei-1111.github.io"

/** 相対パスの画像を実際に配信しているオリジンの絶対 URL へ解決する。http(s) はそのまま。 */
internal fun resolveWorksAssetUrl(url: String): String {
    val path = url.trimStart('/')
    return when {
        url.startsWith("http") -> url
        adminUploadedAssetPattern.matches(path) -> "${appOrigin()}/$path"
        else -> "$PORTFOLIO_SITE_ORIGIN/$path"
    }
}

/**
 * 作品画像の共通ローダー。Coil の既定はレイアウトサイズへ縮小デコードするため、Preview の
 * ズーム拡大や Retina 表示でぼやける。原寸のままデコードし、高品質フィルタで描画する。
 */
@Composable
internal fun WorksAsyncImage(
    url: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = worksImageRequest(url),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.High,
        modifier = modifier,
    )
}

/** [WorksAsyncImage] と同じ原寸デコード設定の painter 版（読み込み後の実比率を参照したい呼び出し側用）。 */
@Composable
internal fun rememberWorksAsyncPainter(url: String): AsyncImagePainter =
    rememberAsyncImagePainter(model = worksImageRequest(url), filterQuality = FilterQuality.High)

@Composable
private fun worksImageRequest(url: String): ImageRequest =
    ImageRequest.Builder(LocalPlatformContext.current)
        .data(resolveWorksAssetUrl(url))
        .size(Size.ORIGINAL)
        .build()

/**
 * WorksPreviewCard / WorksDetailSheet の両方が使う、タグチップとリンクボタン。
 * accent タグ（言語・UI系）は緑、それ以外は textSecondary で塗り分ける。
 */
@Composable
internal fun WorksTagChip(
    tag: WorkTag,
    modifier: Modifier = Modifier,
) {
    val textColor = if (tag.accent) KeiTheme.colors.androidGreen else KeiTheme.colors.textSecondary
    Box(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(KeiTheme.colors.gitHubItem)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = tag.name,
            style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = textColor),
        )
    }
}

/** カードのチップ行だけが使う「+n」オーバーフローチップ。クリック不可（全量はシートで見せる）。 */
@Composable
internal fun WorksTagOverflowChip(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(KeiTheme.shapes.pill)
            .background(KeiTheme.colors.gitHubItem)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = "+$count",
            style = KeiTheme.typography.chrome.copy(fontSize = 7.sp, color = KeiTheme.colors.textSecondary),
        )
    }
}

/**
 * Google Play リンク。GitHubPreviewCard の LinkTile と同じ様式（gitHubItem 面 + ブランドアイコン +
 * 太字ラベル、ホバーでブランド色ボーダー）。公式 Play ロゴは公式カラーのまま描く。
 */
@Composable
internal fun WorksStoreButton(
    url: String,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    WorksLinkButton(
        label = "Google Play",
        hoverBorderColor = KeiTheme.colors.androidGreen,
        url = url,
        onClickUrl = onClickUrl,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_play_store),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = Color.Unspecified,
        )
    }
}

/** ソースリポジトリへのリンク。アイコン・ホバー色とも LinkTile の GitHub と同じブランド色。 */
@Composable
internal fun WorksSourceButton(
    url: String,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val brandColor = LinkServiceType.GitHub.brandColor(KeiTheme.colors)
    WorksLinkButton(
        label = "Source",
        hoverBorderColor = brandColor,
        url = url,
        onClickUrl = onClickUrl,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(LinkServiceType.GitHub.icon(KeiTheme.colors)),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = brandColor,
        )
    }
}

@Composable
private fun WorksLinkButton(
    label: String,
    hoverBorderColor: Color,
    url: String,
    onClickUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    val hoverState = rememberHoverState()
    val borderColor = if (hoverState.hovered) hoverBorderColor else Color.Transparent
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(KeiTheme.shapes.linkTile)
            .background(KeiTheme.colors.gitHubItem)
            .border(1.dp, borderColor, KeiTheme.shapes.linkTile)
            .hoverable(hoverState.interactionSource)
            .clickable(interactionSource = hoverState.interactionSource, indication = null) { onClickUrl(url) },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.size(7.dp))
            Text(
                text = label,
                style = KeiTheme.typography.githubJp.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Preview
@Composable
private fun WorksSharedComponentsPreview() {
    KeiTheme {
        Box(modifier = Modifier.background(KeiTheme.colors.cardBackground).padding(8.dp)) {
            Row {
                WorksTagChip(tag = WorkTag(name = "Kotlin", accent = true))
                Spacer(modifier = Modifier.size(6.dp))
                WorksTagChip(tag = WorkTag(name = "detekt"))
                Spacer(modifier = Modifier.size(6.dp))
                WorksTagOverflowChip(count = 2)
                Spacer(modifier = Modifier.size(6.dp))
                WorksStoreButton(url = "https://example.com", onClickUrl = {})
                Spacer(modifier = Modifier.size(6.dp))
                WorksSourceButton(url = "https://example.com", onClickUrl = {})
            }
        }
    }
}
