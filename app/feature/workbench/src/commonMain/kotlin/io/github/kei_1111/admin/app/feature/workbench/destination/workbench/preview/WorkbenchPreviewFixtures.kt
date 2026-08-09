package io.github.kei_1111.admin.app.feature.workbench.destination.workbench.preview

import io.github.kei_1111.admin.app.feature.workbench.destination.workbench.WorkbenchState
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.SocialLink
import io.github.kei_1111.admin.shared.model.Work

// Preview 専用のサンプル State。実配信内容とは独立で、レイアウト確認に足る最小構成にする。
internal val PreviewAdminWorks: List<Work> = listOf(
    Work(
        id = "withmo",
        name = "withmo",
        type = "Android Launcher App",
        period = "2024–",
        about = "デジタルフィギュア × ランチャーがコンセプトの Android ランチャーアプリ。",
        aboutEn = "An Android launcher app built on the digital figure × launcher concept.",
        techStack = listOf("Kotlin", "Jetpack Compose", "Unity as a Library"),
        roles = listOf("Android 側の実装を担当", "Unity 連携のブリッジ設計"),
        rolesEn = listOf("In charge of the Android-side implementation", "Unity-bridge architecture"),
        status = ContentStatus.Published,
        updatedAt = "2026-08-08",
    ),
    Work(
        id = "kei-1111-github-io",
        name = "kei-1111.github.io",
        type = "Portfolio Website",
        period = "2025–",
        about = "Android Studio New UI を模したポートフォリオサイト。",
        techStack = listOf("Kotlin/Wasm", "Compose Multiplatform", "Ktor"),
        status = ContentStatus.Draft,
        updatedAt = "2026-08-08",
    ),
)

internal val PreviewAdminProfile = AdminProfile(
    displayName = "けい",
    displayNameEn = "Kei",
    role = "Student Developer",
    location = "Japan",
    socialLinks = listOf(SocialLink(service = "GitHub", url = "https://github.com/kei-1111")),
    lastSyncedAt = "2026-08-08 12:00",
)

internal val PreviewWorkbenchState = WorkbenchState(
    works = PreviewAdminWorks,
    profile = PreviewAdminProfile,
    lastDeploy = "2026-08-08 12:34",
    loading = false,
)
