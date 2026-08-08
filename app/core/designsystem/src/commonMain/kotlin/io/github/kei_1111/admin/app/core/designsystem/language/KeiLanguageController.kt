package io.github.kei_1111.admin.app.core.designsystem.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * リソース解決と document.lang を App ルートから横断して同期するため、
 * 画面ローカルの MVI ではなくアプリスコープのシングルトンで保持する。
 */
object KeiLanguageController {
    var language: KeiLanguage by mutableStateOf(KeiLanguage.Ja)
        private set

    /** 起動時にブラウザロケールから検出した初期言語を設定する（webApp の main から呼ぶ）。 */
    fun initialize(language: KeiLanguage) {
        this.language = language
    }

    /** 選択は永続化されず、リロードでブラウザロケール検出値に戻る。 */
    fun toggle() {
        language = if (language == KeiLanguage.Ja) KeiLanguage.En else KeiLanguage.Ja
    }
}
