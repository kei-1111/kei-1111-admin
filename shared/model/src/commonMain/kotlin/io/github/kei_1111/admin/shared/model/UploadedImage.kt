package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadedImage(
    /** 配信パス(images/works/...)。Work.screenshots にそのまま入る。 */
    val path: String,
)
