@file:Suppress("MagicNumber")

package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kei_1111.admin.app.core.designsystem.theme.KeiTheme
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.component.SectionLabel
import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.theme.WorkbenchDimensions

/** ラベル付き入力欄。フォーカス時は青ボーダー(focusBorder)。 */
@Composable
internal fun KeiTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    mono: Boolean = false,
    textColor: Color = KeiTheme.colors.textPrimary,
    placeholder: String = "",
) {
    Column(modifier = modifier) {
        SectionLabel(text = label)
        FieldBox(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            mono = mono,
            textColor = textColor,
            placeholder = placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
    }
}

@Composable
internal fun FieldBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    mono: Boolean = false,
    textColor: Color = KeiTheme.colors.textPrimary,
    placeholder: String = "",
) {
    val colors = KeiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textStyle: TextStyle = (if (mono) KeiTheme.typography.chrome else KeiTheme.typography.cardJp).copy(
        fontSize = WorkbenchDimensions.FieldFontSize,
        color = textColor,
    )
    Box(
        modifier = modifier
            .clip(KeiTheme.shapes.chip)
            .background(colors.islandDark)
            .border(
                width = 1.dp,
                color = if (focused) colors.focusBorder else colors.outline,
                shape = KeiTheme.shapes.chip,
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            textStyle = textStyle,
            cursorBrush = SolidColor(colors.focusBorder),
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (minLines * 18).dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(text = placeholder, style = textStyle.copy(color = colors.muted))
                }
                innerTextField()
            },
        )
    }
}

@Preview
@Composable
private fun KeiTextFieldPreview() {
    KeiTheme {
        Box(modifier = Modifier.width(520.dp).background(KeiTheme.colors.island).padding(8.dp)) {
            KeiTextField(label = "NAME", value = "withmo", onValueChange = {})
        }
    }
}
