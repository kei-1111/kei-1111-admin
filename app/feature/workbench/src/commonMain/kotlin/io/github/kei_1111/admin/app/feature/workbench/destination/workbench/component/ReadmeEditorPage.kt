@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguage
import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguageController
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form.FieldBox
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview.PreviewWorkbenchState
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions
import io.github.kei_1111.admin.app.feature.workbench.model.parseReadmeMarkdown
import io.github.kei_1111.admin.app.feature.workbench.model.readmeMarkdownSource
import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.ReadmeInline

/**
 * README の制限付き Markdown エディタ。編集は言語切替式で、入力のたびにブロック構造へ
 * パースして draft に反映する(配信されるのはブロック構造の方)。
 */
@Composable
internal fun ReadmeEditorPage(
    state: WorkbenchState,
    isMobile: Boolean,
    onChangeReadme: (ReadmeContent) -> Unit,
    onClickDiscardDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val editingJa = KeiLanguageController.language == KeiLanguage.Ja
    Row(modifier = modifier.padding(WorkbenchDimensions.IslandPadding)) {
        ReadmeForm(
            readme = state.readme,
            editingJa = editingJa,
            onChangeReadme = onChangeReadme,
            onClickDiscardDraft = onClickDiscardDraft,
            modifier = Modifier.weight(1f).fillMaxSize(),
        )
        if (!isMobile) {
            Spacer(modifier = Modifier.width(WorkbenchDimensions.IslandGap))
            PreviewPane(
                componentName = "ReadmePage",
                contentFingerprint = state.readme,
                modifier = Modifier.weight(1f).fillMaxSize(),
            ) {
                ReadmeBlocksPreview(
                    blocks = if (editingJa) state.readme.ja else state.readme.en,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ReadmeForm(
    readme: ReadmeContent,
    editingJa: Boolean,
    onChangeReadme: (ReadmeContent) -> Unit,
    onClickDiscardDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(text = "EDIT LANGUAGE")
            Spacer(modifier = Modifier.weight(1f))
            DiscardDraftTextButton(onClick = onClickDiscardDraft)
            Spacer(modifier = Modifier.width(8.dp))
            LanguageSegment()
        }
        Spacer(modifier = Modifier.height(10.dp))
        SectionLabel(text = if (editingJa) "README (日本語)" else "README (ENGLISH)")
        // 入力バッファは編集言語ごとに保持し、パース結果だけを draft に流す
        // (blocks から都度再導出するとカーソル位置と空白が揺れるため)
        var source by remember(editingJa) {
            mutableStateOf(readmeMarkdownSource(if (editingJa) readme.ja else readme.en))
        }
        // 非同期ロードや下書き破棄などバッファ外からの変更だけ再シードする
        // (自分の入力由来なら parse(buffer) == blocks となり何もしない)
        val currentBlocks = if (editingJa) readme.ja else readme.en
        LaunchedEffect(currentBlocks) {
            if (currentBlocks != parseReadmeMarkdown(source)) {
                source = readmeMarkdownSource(currentBlocks)
            }
        }
        FieldBox(
            value = source,
            onValueChange = { value ->
                source = value
                val blocks = parseReadmeMarkdown(value)
                onChangeReadme(if (editingJa) readme.copy(ja = blocks) else readme.copy(en = blocks))
            },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            singleLine = false,
            minLines = 18,
            mono = true,
        )
        Text(
            text = "対応記法: # 見出し / - 箇条書き / `code` / [text](url)",
            style = KeiTheme.typography.cardJp.copy(fontSize = 10.sp, color = KeiTheme.colors.muted),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/** ブロック構造の簡易レンダラ。本体サイトの README 描画の受け側確認用。 */
@Composable
private fun ReadmeBlocksPreview(
    blocks: List<ReadmeBlock>,
    modifier: Modifier = Modifier,
) {
    val colors = KeiTheme.colors
    Column(
        modifier = modifier
            .background(colors.island)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        blocks.forEach { block ->
            when (block) {
                is ReadmeBlock.Heading -> Text(
                    text = inlineText(block.inlines),
                    style = KeiTheme.typography.githubJp.copy(
                        fontSize = when (block.level) {
                            1 -> 20.sp
                            2 -> 16.sp
                            else -> 14.sp
                        },
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    ),
                )

                is ReadmeBlock.Paragraph -> Text(
                    text = inlineText(block.inlines),
                    style = KeiTheme.typography.githubJp.copy(fontSize = 12.sp, color = colors.textSecondary),
                )

                is ReadmeBlock.BulletList -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    block.items.forEach { item ->
                        Row {
                            Text(
                                text = "• ",
                                style = KeiTheme.typography.githubJp.copy(
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                ),
                            )
                            Text(
                                text = inlineText(item),
                                style = KeiTheme.typography.githubJp.copy(
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun inlineText(inlines: List<ReadmeInline>) = buildAnnotatedString {
    val colors = KeiTheme.colors
    inlines.forEach { inline ->
        when (inline) {
            is ReadmeInline.PlainText -> append(inline.text)
            is ReadmeInline.InlineCode -> withStyle(
                SpanStyle(
                    fontFamily = KeiTheme.typography.code.fontFamily,
                    background = colors.chip,
                ),
            ) { append(inline.text) }

            is ReadmeInline.Link -> withStyle(SpanStyle(color = colors.syntaxLink)) { append(inline.text) }
        }
    }
}

@Preview
@Composable
private fun ReadmeEditorPagePreview() {
    KeiTheme {
        Box(modifier = Modifier.size(1000.dp, 700.dp).background(KeiTheme.colors.island)) {
            ReadmeEditorPage(
                state = PreviewWorkbenchState,
                isMobile = false,
                onChangeReadme = {},
                onClickDiscardDraft = {},
            )
        }
    }
}
