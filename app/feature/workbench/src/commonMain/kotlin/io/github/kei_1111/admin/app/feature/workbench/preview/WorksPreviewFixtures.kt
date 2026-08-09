package io.github.kei_1111.admin.app.feature.workbench.preview

import io.github.kei_1111.admin.shared.model.portfolio.LocalizedText
import io.github.kei_1111.admin.shared.model.portfolio.Work
import io.github.kei_1111.admin.shared.model.portfolio.WorkTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// server/.../content/WorksContent.kt（DefaultWorks）の複製。feature モジュールは app:core:data に
// 依存できないため、実データ変更時は両方を揃えて編集する（ProfilePreviewFixtures と同じ運用）。
internal val PreviewWorks: ImmutableList<Work> = persistentListOf(
    Work(
        id = "withmo",
        name = "withmo",
        kind = "Android Launcher App",
        period = "2024–",
        description = LocalizedText(
            ja = "デジタルフィギュア × ランチャーがコンセプトの Android ランチャーアプリ。" +
                "お気に入りの3Dモデルとともにスマホを使える。ホーム画面上でモデルが時間帯や操作に反応する。",
            en = "An Android launcher app built on the digital figure × launcher concept. " +
                "Use your phone together with your favorite 3D model, which reacts to the time of day " +
                "and your actions on the home screen.",
        ),
        tags = persistentListOf(
            WorkTag(name = "Kotlin", accent = true),
            WorkTag(name = "Jetpack Compose", accent = true),
            WorkTag(name = "Unity as a Library"),
            WorkTag(name = "GitHub Actions"),
            WorkTag(name = "detekt"),
        ),
        roles = persistentListOf(
            LocalizedText(ja = "Android 側の実装を担当", en = "In charge of the Android-side implementation"),
            LocalizedText(
                ja = "Unity 連携のブリッジ設計、CI/CD 構築",
                en = "Unity-bridge architecture and CI/CD setup",
            ),
        ),
        iconUrl = "images/works/withmo-icon.webp",
        screenshots = persistentListOf(
            "images/works/withmo-1.webp",
            "images/works/withmo-2.webp",
            "images/works/withmo-3.webp",
        ),
        // Play 掲載は現在非公開のためリンクを持たない（再公開時に復元する）
        storeUrl = null,
        sourceUrl = null,
    ),
    Work(
        id = "kei-1111-github-io",
        name = "kei-1111.github.io",
        kind = "Portfolio Website",
        period = "2025–",
        description = LocalizedText(
            ja = "Android Studio の UI を再現したポートフォリオサイト。" +
                "Compose Multiplatform を WebAssembly にコンパイルし GitHub Pages で配信。",
            en = "A portfolio site that recreates the Android Studio UI, built with Compose Multiplatform " +
                "compiled to WebAssembly and served on GitHub Pages.",
        ),
        tags = persistentListOf(
            WorkTag(name = "Kotlin/Wasm", accent = true),
            WorkTag(name = "Compose Multiplatform", accent = true),
            WorkTag(name = "Ktor"),
            WorkTag(name = "Cloud Run"),
            WorkTag(name = "Playwright"),
        ),
        roles = persistentListOf(
            LocalizedText(ja = "設計・実装（個人開発）", en = "Design & implementation (solo project)"),
            LocalizedText(
                ja = "Ktor サーバーと Cloud Run / GitHub Actions の CI/CD 構築",
                en = "Ktor server plus Cloud Run / GitHub Actions CI/CD",
            ),
        ),
        iconUrl = "images/works/portfolio-icon.webp",
        screenshots = persistentListOf(
            "images/works/portfolio-1.webp",
            "images/works/portfolio-2.webp",
            "images/works/portfolio-3.webp",
            "images/works/portfolio-4.webp",
        ),
        storeUrl = null,
        sourceUrl = "https://github.com/kei-1111/kei-1111.github.io",
    ),
)
