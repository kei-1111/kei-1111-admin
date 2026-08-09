package io.github.kei_1111.admin.shared.model.portfolio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** client / server 間で共有する二言語テキスト。表示言語の解決はクライアント側で行う。 */
@Serializable
data class LocalizedText(
    @SerialName("ja")
    val ja: String,
    @SerialName("en")
    val en: String,
)
