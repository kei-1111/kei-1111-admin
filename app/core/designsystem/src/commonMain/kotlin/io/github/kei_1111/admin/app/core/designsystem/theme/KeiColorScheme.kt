@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Contributions ヒートマップの段階数（Less → More）。[KeiColorScheme.contributionLevels] のサイズ不変条件。 */
private const val CONTRIBUTION_LEVEL_COUNT = 5

/**
 * 実インスタンスは [KeiDarkColorScheme] / [KeiLightColorScheme] としてそれぞれ定義される。
 */
@Immutable
data class KeiColorScheme(
    /** テーマ依存のアイコン・文言分岐はこれを参照する。 */
    val isDark: Boolean,

    val desk: Color,
    val deskGlow: Color,
    val island: Color,
    val islandDark: Color,
    /** ポップアップ（Search Everywhere）の面。Islands の `Popup.background` で、島（[island]）とは別トークン。 */
    val popup: Color,
    /** ポップアップの外枠と内部の区切り線（Islands の `Popup.borderColor` / `Separator.separatorColor`）。 */
    val popupBorder: Color,
    val outline: Color,
    val selectionPill: Color,
    val tabSelected: Color,
    val tabSelectedBorder: Color,
    /** ポップアップのリスト選択行（Islands の `selection-bg-active`）。エディタタブの [tabSelected] とは別値。 */
    val popupSelection: Color,
    /** フォーカス中の入力欄の枠線（Islands の `control-brand-border`）。明暗で不変。 */
    val focusBorder: Color,
    val chip: Color,
    val deskChip: Color,
    /** ライセンスシートのスクリム（LicenseSheetOverlay で使用）。 */
    val scrim: Color,
    val scrollbarThumb: Color,
    val scrollbarThumbHover: Color,
    /** スケルトンシマーの下地（island を基準に少し明度をずらした値）。 */
    val skeletonBone: Color,
    /** スケルトンシマーの掃引ハイライト帯（[skeletonBone] よりさらに明るい値）。 */
    val skeletonHighlight: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textCode: Color,

    val muted: Color,
    val mutedHigh: Color,

    /** 押せない装飾クローム要素の透過率（明暗で不変）。`Modifier.alpha` で適用する。 */
    val nonClickableAlpha: Float,

    val editorCaretRow: Color,

    val syntaxKeyword: Color,
    val syntaxAnnotation: Color,
    val syntaxFunction: Color,
    val syntaxComposableCall: Color,
    val syntaxEnumEntry: Color,
    val syntaxString: Color,
    val syntaxNumber: Color,
    val syntaxNamedArg: Color,
    val syntaxComment: Color,
    val syntaxLink: Color,
    /** `// TODO:` コメントの文字色。実 AS の TODO ハイライト（イタリック）に対応する。 */
    val syntaxTodo: Color,

    val logcatDebug: Color,
    val logcatInfo: Color,
    val logcatWarning: Color,
    val logcatError: Color,
    /** レベルバッジの地色。実 AS 同様、ダークはベタ塗り・ライトは D/I を淡色にする。 */
    val logcatDebugBadge: Color,
    val logcatInfoBadge: Color,
    val logcatWarningBadge: Color,
    val logcatErrorBadge: Color,
    /** レベルバッジの文字色。ダークは島色の白抜き風、ライトはレベル色。 */
    val logcatDebugBadgeText: Color,
    val logcatInfoBadgeText: Color,
    val logcatWarningBadgeText: Color,
    val logcatErrorBadgeText: Color,
    /** タグ列の色。実 AS 同様、タグごとにパレットから決定的に割り当てる。 */
    val logcatTagColors: List<Color>,

    val androidGreen: Color,

    val cardBackground: Color,

    val gitHubItem: Color,
    val gitHubItemHover: Color,
    val brandQiita: Color,
    val langKotlin: Color,
    val langSwift: Color,
    val langShell: Color,

    val licenseBadge: Color,

    /** Less → More の順の5段階。 */
    val contributionLevels: List<Color>,

    val splashDesk: Color,
    val splashCard: Color,
    val splashCardBorder: Color,
    val splashTextTitle: Color,
    val splashTextDim: Color,
    val splashTextLog: Color,
    val splashProgressTrack: Color,
    val splashProgressBar: Color,
    val splashProgressBarFailed: Color,
    val splashStatusRunning: Color,
    val splashStatusDone: Color,
    val splashStatusFailed: Color,
) {
    init {
        require(contributionLevels.size == CONTRIBUTION_LEVEL_COUNT) {
            "contributionLevels は $CONTRIBUTION_LEVEL_COUNT 段階（Less→More）でなければなりません（実際: ${contributionLevels.size}）"
        }
    }
}

/** Android Studio "New UI / Islands Dark" の実測値（公式 Islands テーマ定義と照合済み）。 */
val KeiDarkColorScheme = KeiColorScheme(
    isDark = true,

    desk = Color(0xFF26282C),
    deskGlow = Color(0xFF584E4A),
    island = Color(0xFF1E1F22),
    islandDark = Color(0xFF191A1C),
    popup = Color(0xFF26282C),
    popupBorder = Color(0xFF33353B),
    outline = Color(0xFF26282C),
    selectionPill = Color(0xFF33353A),
    tabSelected = Color(0xFF233558),
    tabSelectedBorder = Color(0xFF2E4D89),
    popupSelection = Color(0xFF2A4371),
    focusBorder = Color(0xFF3871E1),
    chip = Color(0x17FFFFFF),
    deskChip = Color(0x17FFFFFF),
    scrim = Color(0x85000000),
    scrollbarThumb = Color(0x59808080), // 公式 Islands Dark テーマ定義値（ScrollBar.thumbColor / hoverThumbColor）
    scrollbarThumbHover = Color(0x8C808080), // 公式 Islands Dark テーマ定義値（ScrollBar.thumbColor / hoverThumbColor）
    skeletonBone = Color(0xFF28292D), // island を1トーン明るくした値（desk / editorCaretRow と同系統）
    skeletonHighlight = Color(0xFF35373C), // skeletonBone よりさらに明るい掃引帯

    textPrimary = Color(0xFFD1D3D9),
    textSecondary = Color(0xFF9FA2A8),
    textCode = Color(0xFFBCBEC4),

    muted = Color(0xFF4B5059),
    mutedHigh = Color(0xFFC3C5CB),

    nonClickableAlpha = 0.45f,

    editorCaretRow = Color(0xFF26282D),

    // 実 AS スクリーンショット実測値
    syntaxKeyword = Color(0xFFCF8E6D),
    syntaxAnnotation = Color(0xFFB3AE60),
    syntaxFunction = Color(0xFF56A8F5),
    syntaxComposableCall = Color(0xFF6CB28B),
    syntaxEnumEntry = Color(0xFFC77DBB),
    syntaxString = Color(0xFF6AAB73),
    syntaxNumber = Color(0xFF2AACB8),
    syntaxNamedArg = Color(0xFF56C1D6),
    syntaxComment = Color(0xFF7A7E85),
    syntaxLink = Color(0xFF56A8F5),
    // 実機 AS 同梱の Islands Dark スキーム定義（IslandSchemeDark.xml の TODO_DEFAULT_ATTRIBUTES）に従う。
    syntaxTodo = Color(0xFF8BB33D),

    // 実 AS New UI の Logcat 配色: D=ティール / I=ブルー
    logcatDebug = Color(0xFF2AACB8),
    logcatInfo = Color(0xFF56A8F5),
    logcatWarning = Color(0xFFBBB529),
    logcatError = Color(0xFFFF6B68),
    logcatDebugBadge = Color(0xFF2AACB8),
    logcatInfoBadge = Color(0xFF56A8F5),
    logcatWarningBadge = Color(0xFFBBB529),
    logcatErrorBadge = Color(0xFFFF6B68),
    logcatDebugBadgeText = Color(0xFF1E1F22),
    logcatInfoBadgeText = Color(0xFF1E1F22),
    logcatWarningBadgeText = Color(0xFF1E1F22),
    logcatErrorBadgeText = Color(0xFF1E1F22),
    logcatTagColors = listOf(
        Color(0xFF56A8F5),
        Color(0xFF6CB28B),
        Color(0xFF2AACB8),
        Color(0xFFC77DBB),
        Color(0xFFD5AE57),
        Color(0xFFCF8E6D),
    ),

    androidGreen = Color(0xFF3DDC84),

    cardBackground = Color(0xFF1A1B1E),

    gitHubItem = Color(0xFF1F2124),
    gitHubItemHover = Color(0xFF292B2F),
    brandQiita = Color(0xFF55C500),
    langKotlin = Color(0xFFA97BFF),
    langSwift = Color(0xFFF05138),
    langShell = Color(0xFF89E051),

    licenseBadge = Color(0xFF1D2F24),

    contributionLevels = listOf(
        Color(0xFF1F2124),
        Color(0xFF173527),
        Color(0xFF1D4429),
        Color(0xFF2C6B3F),
        Color(0xFF3DDC84),
    ),

    splashDesk = Color(0xFF141419),
    splashCard = Color(0xFF1E1F25),
    splashCardBorder = Color(0xFF26272F),
    splashTextTitle = Color(0xFFDFE0EA),
    splashTextDim = Color(0xFF6C6D78),
    splashTextLog = Color(0xFF9C9DAA),
    splashProgressTrack = Color(0xFF26272F),
    splashProgressBar = Color(0xFF3DDC84),
    splashProgressBarFailed = Color(0xFFDB5C5C),
    splashStatusRunning = Color(0xFFD5AE57),
    splashStatusDone = Color(0xFF57965C),
    splashStatusFailed = Color(0xFFDB5C5C),
)

/**
 * 値は実際の Android Studio (Islands Light) の実測値（公式 Islands テーマ定義と照合済み）に合わせている。
 */
val KeiLightColorScheme = KeiColorScheme(
    isDark = false,

    desk = Color(0xFFE9EAEE),
    deskGlow = Color(0xFFD5D2D2),
    island = Color(0xFFFFFFFF),
    islandDark = Color(0xFFFFFFFF),
    popup = Color(0xFFFFFFFF),
    popupBorder = Color(0xFFE9EAEE),
    outline = Color(0xFFE9EAEE),
    selectionPill = Color(0xFFE9EAEE),
    tabSelected = Color(0xFFE3EBFE),
    tabSelectedBorder = Color(0xFFA7C5FF),
    popupSelection = Color(0xFFD0DFFE),
    focusBorder = Color(0xFF3871E1),
    chip = Color(0x12000000),
    deskChip = Color(0x12000000),
    scrim = Color(0x85000000),
    scrollbarThumb = Color(0x33000000), // Islands Light は ScrollBar 未定義のため macOS プラットフォーム既定値
    scrollbarThumbHover = Color(0x80000000), // Islands Light は ScrollBar 未定義のため macOS プラットフォーム既定値
    skeletonBone = Color(0xFFEDEEF1), // island（白）を少し暗くした値
    skeletonHighlight = Color(0xFFF8F9FB), // skeletonBone より明るい掃引帯（白に近い）

    textPrimary = Color(0xFF000000),
    textSecondary = Color(0xFF5F6269),
    textCode = Color(0xFF000000),

    muted = Color(0xFFAEB3C2),
    mutedHigh = Color(0xFF6C707E),

    nonClickableAlpha = 0.45f,

    editorCaretRow = Color(0xFFF6F8FD),

    // IntelliJ Light 既定スキーム
    syntaxKeyword = Color(0xFF0033B3),
    syntaxAnnotation = Color(0xFF9E880D),
    syntaxFunction = Color(0xFF00627A),
    syntaxComposableCall = Color(0xFF009900),
    syntaxEnumEntry = Color(0xFF871094),
    syntaxString = Color(0xFF067D17),
    syntaxNumber = Color(0xFF1750EB),
    syntaxNamedArg = Color(0xFF4A86E8),
    syntaxComment = Color(0xFF8C8C8C),
    syntaxLink = Color(0xFF0033B3),
    // TODO 色は IntelliJ Light デフォルトスキーム（TODO_DEFAULT_ATTRIBUTES）に従う。
    syntaxTodo = Color(0xFF0073BF),

    // 実 AS Light: D/I とも青系メッセージ、バッジは淡色地 + レベル色文字。W はベタ塗りの琥珀
    logcatDebug = Color(0xFF2C64C8),
    logcatInfo = Color(0xFF1750EB),
    logcatWarning = Color(0xFF9E880D),
    logcatError = Color(0xFFCC0000),
    logcatDebugBadge = Color(0xFFD9E7F7),
    logcatInfoBadge = Color(0xFFD9E3FC),
    logcatWarningBadge = Color(0xFFE9C558),
    logcatErrorBadge = Color(0xFFCC0000),
    logcatDebugBadgeText = Color(0xFF2C64C8),
    logcatInfoBadgeText = Color(0xFF1750EB),
    logcatWarningBadgeText = Color(0xFF4A3B00),
    logcatErrorBadgeText = Color(0xFFFFFFFF),
    logcatTagColors = listOf(
        Color(0xFF0033B3),
        Color(0xFF009900),
        Color(0xFF00838F),
        Color(0xFF871094),
        Color(0xFF9E880D),
        Color(0xFFB3591C),
    ),

    // 明暗で不変
    androidGreen = Color(0xFF3DDC84),

    cardBackground = Color(0xFFFFFFFF),

    gitHubItem = Color(0xFFF6F8FA),
    gitHubItemHover = Color(0xFFEEF1F4),
    brandQiita = Color(0xFF55C500),
    langKotlin = Color(0xFFA97BFF),
    langSwift = Color(0xFFF05138),
    langShell = Color(0xFF89E051),

    licenseBadge = Color(0xFFE3F2E8),

    contributionLevels = listOf(
        Color(0xFFEBEDF0),
        Color(0xFF9BE9A8),
        Color(0xFF40C463),
        Color(0xFF30A14E),
        Color(0xFF216E39),
    ),

    // スプラッシュ（テーマ永続化によりライト起動があるため、ダーク版の相対関係を白ベースへ写像した専用値）
    splashDesk = Color(0xFFE9EAEE),
    splashCard = Color(0xFFFFFFFF),
    splashCardBorder = Color(0xFFDCDFE6),
    splashTextTitle = Color(0xFF1D1E26),
    splashTextDim = Color(0xFF9495A0),
    splashTextLog = Color(0xFF63646E),
    splashProgressTrack = Color(0xFFDCDFE6),
    splashProgressBar = Color(0xFF3DDC84),
    splashProgressBarFailed = Color(0xFFC94848),
    splashStatusRunning = Color(0xFFB08C3A),
    splashStatusDone = Color(0xFF3E8A4C),
    splashStatusFailed = Color(0xFFC94848),
)
