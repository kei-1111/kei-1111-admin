package io.github.kei_1111.admin.server.service

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlinx.serialization.json.Json

/** GCS に下書きがまだ無いときの初期値(リポジトリ同梱の resources/seed/)。 */
internal object ContentSeeds {

    fun works(json: Json): WorksContent =
        read("works.json")?.let { json.decodeFromString(WorksContent.serializer(), it) } ?: WorksContent()

    fun profile(json: Json): AdminProfile =
        read("profile.json")?.let { json.decodeFromString(AdminProfile.serializer(), it) } ?: AdminProfile()

    fun terminal(json: Json): TerminalCommandsContent =
        read("terminal-commands.json")
            ?.let { json.decodeFromString(TerminalCommandsContent.serializer(), it) }
            ?: TerminalCommandsContent()

    fun readme(json: Json): ReadmeContent =
        read("readme.json")?.let { json.decodeFromString(ReadmeContent.serializer(), it) } ?: ReadmeContent()

    private fun read(name: String): String? =
        ContentSeeds::class.java.classLoader
            ?.getResourceAsStream("seed/$name")
            ?.use { it.readAllBytes().decodeToString() }
}
