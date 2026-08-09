package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.app.core.designsystem.language.KeiLanguage
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText

internal fun LocalizedText.forLanguage(language: KeiLanguage): String = when (language) {
    KeiLanguage.Ja -> ja
    KeiLanguage.En -> en
}
