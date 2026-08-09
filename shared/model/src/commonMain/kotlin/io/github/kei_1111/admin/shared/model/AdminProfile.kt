package io.github.kei_1111.admin.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class SocialLink(
    val service: String,
    val url: String,
)

/** GitHub から取得した Pinned リポジトリの表示設定。内容は編集不可、表示 ON/OFF と並び順のみ管理する。 */
@Serializable
data class PinnedRepoSetting(
    val name: String,
    val description: String = "",
    val visible: Boolean = true,
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
