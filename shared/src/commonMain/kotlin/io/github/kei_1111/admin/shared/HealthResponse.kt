package io.github.kei_1111.admin.shared

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
)
