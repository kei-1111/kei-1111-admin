# Module Overview

各モジュールの責務と依存関係。kei-1111.github.io と同じ木構造を採用している。

## 依存グラフ

```mermaid
flowchart TB
    webApp["app:webApp<br>エントリポイント / DI / Navigation"]
    home["app:feature:home"]
    common["app:core:common"]
    designsystem["app:core:designsystem"]
    mvi["app:core:mvi"]
    navigation["app:core:navigation"]
    model["shared:model"]
    server["server"]

    webApp --> home
    webApp --> common
    webApp --> designsystem
    webApp --> navigation
    webApp --> model
    home --> common
    home --> designsystem
    home --> mvi
    home --> navigation
    home --> model
    mvi --> common
    navigation --> designsystem
    server --> model
```

`app:core:testing` は各モジュールの commonTest からのみ参照されるテスト基盤(`ViewModelTestBase` / `startCollecting`)のため、グラフからは省略。

## モジュール責務

| モジュール | 責務 |
|---|---|
| `app:webApp` | エントリポイント。Metro `AppGraph`、`AppNavDisplay`(NavDisplay + back stack)、`AdminTheme` 適用。wasmJs のみ、`binaries.executable()` |
| `app:feature:home` | ホーム画面。MVI 一式(`destination/home/`)と navigation entries |
| `app:core:common` | 独自 `Result<T>` / `asResult()`、キャンセル安全な抑制ヘルパー(`recoverOrElse` / `runBestEffort`)、`InteractionLog`、Dispatcher バインディング |
| `app:core:designsystem` | `AdminTheme`(現状 Material3 darkColorScheme のラッパー)とテーマトークン |
| `app:core:mvi` | `MviViewModel` 基底、`Intent` / `State` / `ViewModelState` マーカー、`MviEffect` composable |
| `app:core:navigation` | `InlineDialogSceneStrategy`、`ResultEventBus`、遷移アニメーション拡張 |
| `app:core:testing` | commonTest 用基盤: `ViewModelTestBase`(Main dispatcher 差し替え)、`startCollecting` |
| `shared:model` | UI とサーバーで共有する DTO(jvm + wasmJs) |
| `server` | 管理 API(Ktor CIO、Cloud Run)。GCS 読み書き・Google ID トークン検証(予定) |

## 依存ルール

- feature → core / shared のみ。feature 間依存は禁止(画面間の連携は `app:webApp` の `AppNavDisplay` がラムダで配線)
- Metro の `@ContributesBinding` / `@ContributesIntoMap` は推移的依存から集約されないため、コントリビュートするモジュールは `app:webApp` の直接依存に置く
- モジュール設定は build-logic の convention plugin(`kei_1111.*`)経由(`.claude/rules/gradle.md`)
