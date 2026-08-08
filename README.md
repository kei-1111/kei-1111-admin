# portfolio-admin

Admin console for [kei-1111.github.io](https://github.com/kei-1111/kei-1111.github.io): manages the images and text content that the portfolio server delivers, backed by Google Cloud Storage.

## Architecture

```mermaid
flowchart TB
    ui["Admin UI<br>Compose Multiplatform (wasmJs)<br>GitHub Pages"]
    admin["Admin server<br>Ktor on Cloud Run<br>verifies the allowlisted Google account"]
    gcs[("GCS bucket<br>images + content JSON")]
    portfolio["Portfolio server<br>kei-1111.github.io"]

    ui -- "Google ID token (Bearer)" --> admin
    admin -- "read / write" --> gcs
    portfolio -- "read" --> gcs
```

- **Auth**: Google Identity Services in the admin UI issues an ID token; the admin server verifies it and allows a single allowlisted account.
- **Storage**: one GCS bucket holds both image assets and text content (JSON). The portfolio server reads the same bucket, so content changes go live without a redeploy.

## Modules

| Module | Description |
|---|---|
| `app:webApp` | Admin UI entry point — Metro DI graph, Navigation 3 `AppNavDisplay` (wasmJs only) |
| `app:core:common` | `Result<T>` / suppression helpers / `InteractionLog` |
| `app:core:designsystem` | Theme (`AdminTheme`) |
| `app:core:mvi` | `MviViewModel` base + MVI marker interfaces |
| `app:core:navigation` | Dialog scene strategy, result bus, transitions |
| `app:core:testing` | Test infrastructure for commonTest (host tests) |
| `app:feature:home` | Home screen (MVI reference shape) |
| `shared:model` | DTOs shared between UI and server (wasmJs + jvm) |
| `server` | Admin API — Ktor (CIO), deployed to Cloud Run |

Details: `docs/ModuleOverview.md` / `docs/ArchitectureOverview.md` (Japanese).

## Commands

```bash
./gradlew :app:webApp:wasmJsBrowserDevelopmentRun   # UI dev server (the :app:webApp: prefix is required)
./gradlew :app:webApp:wasmJsBrowserDistribution     # UI production build
./gradlew :server:run                               # Admin server (http://localhost:8082; Cloud Run injects PORT)
./gradlew :server:buildFatJar                       # server/build/libs/server-all.jar
./gradlew :server:test                              # server tests
```

## Setup status

- [x] Project scaffold (Kotlin 2.4.0 / Compose Multiplatform 1.11.1 / Ktor 3.5.1 / Gradle 9.6.1)
- [x] Infrastructure parity with kei-1111.github.io: convention plugins, detekt, MVI + Metro DI + Navigation 3, host tests
- [ ] GCS bucket + service account
- [ ] Google OAuth client (Identity Services) + ID-token verification on the server
- [ ] Image upload / list / delete API + UI
- [ ] Text content (JSON) edit API + UI
- [ ] CI (build + test) and CD (GitHub Pages / Cloud Run)
- [ ] Portfolio server reads content from the GCS bucket
