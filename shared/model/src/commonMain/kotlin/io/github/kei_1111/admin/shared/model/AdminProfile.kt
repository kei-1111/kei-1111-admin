package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class SocialLink(
    val service: String,
    val url: String,
)

/**
 * GitHub から取得した Pinned リポジトリの表示設定。表示 ON/OFF に加え、説明文だけは
 * リポジトリ毎に上書きできる(空なら GitHub の説明をそのまま使う)。
 */
@Serializable
data class PinnedRepoSetting(
    val name: String,
    val description: String = "",
    val visible: Boolean = true,
    /** 説明文の上書き(日本語)。空なら GitHub の説明へフォールバック。 */
    val descriptionJa: String = "",
    /** 説明文の上書き(英語)。空なら [descriptionJa]、それも空なら GitHub の説明。 */
    val descriptionEn: String = "",
)

@Serializable
data class AdminProfile(
    val displayName: String = "",
    /** 英語版。空なら配信時に [displayName] へフォールバックする。 */
    val displayNameEn: String = "",
    val role: String = "",
    val location: String = "",
    val xUrl: String = "",
    val avatarUrl: String = "",
    val pinnedRepos: List<PinnedRepoSetting> = emptyList(),
    val socialLinks: List<SocialLink> = emptyList(),
    val gitHubLogin: String = "kei-1111",
    val lastSyncedAt: String = "",
)
