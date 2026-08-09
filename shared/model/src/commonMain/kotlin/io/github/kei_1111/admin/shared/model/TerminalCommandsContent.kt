package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

/** サーバー定義のテキスト出力コマンド。ターミナル文言は IDE クロームのため英語固定。 */
@Serializable
data class TerminalTextCommand(
    val keyword: String,
    val lines: List<String> = emptyList(),
)

@Serializable
data class TerminalCommandsContent(
    val commands: List<TerminalTextCommand> = emptyList(),
)
