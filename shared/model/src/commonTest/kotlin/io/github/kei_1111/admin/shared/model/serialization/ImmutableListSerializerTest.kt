package io.github.kei_1111.admin.shared.model.serialization

import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.ContributionDay
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

private val json = Json

class ImmutableListSerializerTest {

    @Test
    fun roundTripsAsAPlainJsonArray() {
        val calendar = ContributionCalendar(
            totalLastYear = 4,
            days = persistentListOf(ContributionDay(date = "2026-08-10", count = 4, level = 2)),
        )

        val encoded = json.encodeToString(ContributionCalendar.serializer(), calendar)
        val decoded = json.decodeFromString(ContributionCalendar.serializer(), encoded)

        assertEquals(
            buildJsonArray {
                add(
                    buildJsonObject {
                        put("date", "2026-08-10")
                        put("count", 4)
                        put("level", 2)
                    },
                )
            },
            json.parseToJsonElement(encoded).jsonObject.getValue("days"),
        )
        assertEquals(calendar, decoded)
    }

    @Test
    fun roundTripsAnEmptyList() {
        val calendar = ContributionCalendar(totalLastYear = 0, days = persistentListOf())

        val decoded = json.decodeFromString(
            ContributionCalendar.serializer(),
            json.encodeToString(ContributionCalendar.serializer(), calendar),
        )

        assertEquals(calendar, decoded)
    }
}
