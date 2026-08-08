package io.github.kei_1111.admin.app.feature.workbench.preview

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.PinnedRepoSetting
import io.github.kei_1111.admin.shared.model.SocialLink
import io.github.kei_1111.admin.shared.model.Work

/** サーバー接続前の初期表示・Preview 用のサンプルデータ。 */
internal val PreviewWorks = listOf(
    Work(
        id = "withmo",
        name = "withmo",
        type = "Android アプリ",
        period = "2023.10 - ",
        about = "3D モデルと一緒に暮らせる Android ホームアプリ。",
        techStack = listOf("Kotlin", "Jetpack Compose", "Unity as a Library", "Firebase"),
        roles = listOf("Android アプリ開発", "UI 設計"),
        googlePlayUrl = "https://play.google.com/store/apps/details?id=com.example.withmo",
        sourceUrl = "https://github.com/team-withmo/withmo",
        status = ContentStatus.Published,
        updatedAt = "2h ago",
    ),
    Work(
        id = "timelog",
        name = "TimeLog",
        type = "Android アプリ",
        period = "2024.04 - 2024.09",
        about = "時間の使い方を記録・可視化するタイムトラッキングアプリ。",
        techStack = listOf("Kotlin", "Jetpack Compose", "Room"),
        roles = listOf("個人開発"),
        sourceUrl = "https://github.com/kei-1111/TimeLog",
        status = ContentStatus.Published,
        updatedAt = "3d ago",
    ),
    Work(
        id = "pixeldiary",
        name = "PixelDiary",
        type = "Android アプリ",
        period = "2025.01 - ",
        about = "1 日 1 ピクセルアートで記録する日記アプリ。",
        techStack = listOf("Kotlin", "Compose Multiplatform"),
        roles = listOf("個人開発"),
        status = ContentStatus.Draft,
        updatedAt = "1w ago",
    ),
)

internal val PreviewProfile = AdminProfile(
    displayName = "けい",
    role = "Android Engineer",
    location = "Japan",
    xUrl = "https://x.com/kei_1111_",
    pinnedRepos = listOf(
        PinnedRepoSetting("withmo", "3D モデルと暮らせるホームアプリ", visible = true),
        PinnedRepoSetting("kei-1111.github.io", "Compose Multiplatform 製ポートフォリオ", visible = true),
        PinnedRepoSetting("TimeLog", "タイムトラッキングアプリ", visible = false),
    ),
    socialLinks = listOf(
        SocialLink("GitHub", "https://github.com/kei-1111"),
        SocialLink("Qiita", "https://qiita.com/kei-1111"),
    ),
    gitHubLogin = "kei-1111",
    lastSyncedAt = "4h ago",
)
