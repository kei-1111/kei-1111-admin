# Gradle & Build Configuration

## Version Catalog (mandatory)

Declare ALL dependencies and plugins in `gradle/libs.versions.toml` and reference them via the catalog:

- Module build files: `implementation(libs.xxx)` / `alias(libs.plugins.kei1111.xxx)`
- Convention plugins (build-logic): `libs.findLibrary("...").get()` / `libs.findPlugin("...")`
- Do **NOT** use the deprecated `compose.dependencies.*` Gradle accessors
- Convention plugin ids are declared as `[plugins]` entries with `version = "unspecified"`

## `api()` is Prohibited

Every dependency is declared with `implementation()`, in module build files and convention plugins alike, so a build file states exactly what its module depends on.

## Dependency Updates

- Bump versions only in `gradle/libs.versions.toml`; Kotlin is the anchor — check Compose Multiplatform / AGP / Metro compatibility before bumping, and bump coupled versions together
- Keep the version base in sync with kei-1111.github.io unless there is a reason to diverge
- One upgrade per branch/PR
- Validate: `./gradlew detekt :app:webApp:wasmJsBrowserDistribution compileAndroidMain :server:test :app:core:common:testAndroidHostTest :app:core:mvi:testAndroidHostTest :app:feature:workbench:testAndroidHostTest`

## Convention Plugins

All module configuration goes through the six convention plugins in `build-logic/convention/src/main/kotlin/` (ported from kei-1111.github.io) — prefer extending them over ad hoc per-module Gradle configuration:

| Plugin id | Source | Responsibility |
|---|---|---|
| `kei_1111.detekt` | `DetektPlugin.kt` | detekt + formatting/compose rule sets, autoCorrect locally (disabled on CI), config from `config/detekt/detekt.yml`, jvmTarget 17 |
| `kei_1111.kmp.wasm` | `KmpWasmPlugin.kt` | KMP targets: `wasmJs { browser() }` + the **non-shipped** `android {}` target (namespace auto-derived from the project path — do not remove it; Compose Preview rendering needs it, and modules with unit tests run them on it as host tests) |
| `kei_1111.cmp` | `CmpPlugin.kt` | Applies the Compose Multiplatform + Compose compiler plugins; wires `compose.ui.tooling` for `@Preview` rendering on Android-target modules |
| `kei_1111.kmp.feature` | `KmpFeaturePlugin.kt` | Applies `kei_1111.kmp.wasm` + `kei_1111.cmp` + serialization + `kei_1111.metro`; enables the Android host test (`withHostTestBuilder`); wires commonMain deps on `app:core:common/designsystem/mvi/navigation` + `shared:model` plus Compose/lifecycle/navigation3/metrox-viewmodel libraries, and commonTest deps on `app:core:testing` + `kotlin-test` + `kotlinx-coroutines-test` |
| `kei_1111.kmp.shared` | `KmpSharedPlugin.kt` | Applies `kei_1111.kmp.wasm` + a `jvm()` target — for `shared:model` (shared with `:server`) |
| `kei_1111.metro` | `MetroPlugin.kt` | Metro DI compiler; `generateContributionProviders = true` keeps `internal` `@ContributesBinding` impls visible cross-module |

## Module Wiring

- A feature module's `build.gradle.kts` is minimal — just two plugin aliases (`kei1111.detekt` + `kei1111.kmp.feature`), no dependencies block. See `app/feature/workbench/build.gradle.kts`
- New module: add `include(":app:feature:<name>")` to `settings.gradle.kts`, then reference it with **typesafe project accessors** (`projects.app.feature.<name>`)
- Metro does not aggregate `@ContributesBinding` contributions from transitive `implementation` dependencies, and `api()` is prohibited — so a contributing module must be a direct dependency of the graph-owning module (`app:webApp`)

## detekt

- Config: `config/detekt/detekt.yml` (`build.maxIssues: 0`); run with `./gradlew detekt`
- `autoCorrect` is enabled locally (disabled on CI) — a first run that reformats can end BUILD FAILED; rerun before judging. Never fix import ordering manually
- Key rules: MaxLineLength 150, trailing commas required, MagicNumber (suppress at file level where UI code needs literals)

## Build Commands

```bash
./gradlew :app:webApp:wasmJsBrowserDevelopmentRun  # dev server (the :app:webApp: prefix is required)
./gradlew :app:webApp:wasmJsBrowserDistribution    # production build (CD)
./gradlew :app:feature:workbench:compileKotlinWasmJs    # single-module wasm compile
./gradlew :app:feature:workbench:compileAndroidMain     # non-shipped Android target compile (Preview rendering)
./gradlew :server:run                              # Admin server (http://localhost:8082; Cloud Run injects PORT)
./gradlew :server:buildFatJar                      # server/build/libs/server-all.jar
./gradlew :server:test                             # server tests
./gradlew :app:feature:workbench:testAndroidHostTest    # client unit tests, local JVM (also :app:core:common / :app:core:mvi)
```
