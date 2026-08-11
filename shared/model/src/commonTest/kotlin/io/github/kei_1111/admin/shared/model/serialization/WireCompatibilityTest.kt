package io.github.kei_1111.admin.shared.model.serialization

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.Work
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class WireCompatibilityTest {

    @Test
    fun workMissingOptionalFieldsUsesDefaults() {
        val decoded = Json.decodeFromString(
            Work.serializer(),
            """
            {
              "id": "portfolio",
              "name": "Portfolio",
              "kind": "Website",
              "period": "2025–",
              "description": { "ja": "説明", "en": "Description" }
            }
            """.trimIndent(),
        )

        assertEquals(
            Work(
                id = "portfolio",
                name = "Portfolio",
                kind = "Website",
                period = "2025–",
                description = LocalizedText(ja = "説明", en = "Description"),
                tags = persistentListOf(),
                roles = persistentListOf(),
                screenshots = persistentListOf(),
            ),
            decoded,
        )
    }

    @Test
    fun adminProfileMissingOptionalFieldsUsesDefaults() {
        val decoded = Json.decodeFromString(AdminProfile.serializer(), """{"displayName":"Kei"}""")

        assertEquals(AdminProfile(displayName = "Kei"), decoded)
    }
}
