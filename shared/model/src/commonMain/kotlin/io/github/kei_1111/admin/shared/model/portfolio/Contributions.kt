package io.github.kei_1111.admin.shared.model.portfolio

import io.github.kei_1111.admin.shared.model.portfolio.serialization.ImmutableListSerializer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContributionDay(
    @SerialName("date")
    val date: String,
    @SerialName("count")
    val count: Int,
    @SerialName("level")
    val level: Int,
)

@Serializable
data class ContributionCalendar(
    @SerialName("totalLastYear")
    val totalLastYear: Int,
    @SerialName("days")
    @Serializable(with = ImmutableListSerializer::class)
    val days: ImmutableList<ContributionDay>,
)
