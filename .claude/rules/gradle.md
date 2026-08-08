# Gradle & Build Configuration

## Version Catalog (mandatory)

- Declare ALL dependencies and plugins in `gradle/libs.versions.toml`; reference via `implementation(libs.xxx)` / `alias(libs.plugins.xxx)`
- `api()` is prohibited — every dependency is declared with `implementation()` so a build file states exactly what its module depends on
- Do NOT use the deprecated `compose.dependencies.*` Gradle accessors

## Dependency Updates

- Bump versions only in `gradle/libs.versions.toml`; Kotlin is the anchor for coupled versions (Compose Multiplatform, Ktor plugin)
- Keep the version base in sync with kei-1111.github.io unless there is a reason to diverge
- One upgrade per branch/PR
- Validate: `./gradlew :composeApp:wasmJsBrowserDistribution :server:test`

## Modules

- `composeApp` — wasmJs only, Compose Multiplatform; entry point `Main.kt` (`ComposeViewport`)
- `server` — Kotlin/JVM + Ktor plugin; `mainClass` `io.github.kei_1111.admin.server.ApplicationKt`, fat jar `server-all.jar`
- `shared` — KMP `jvm()` + `wasmJs()`, DTOs only
- New module: add `include(":<name>")` to `settings.gradle.kts`, reference with typesafe project accessors (`projects.<name>`)
- No convention plugins yet — if per-module configuration starts repeating, introduce build-logic convention plugins mirroring kei-1111.github.io

## Build Commands

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # UI dev server
./gradlew :composeApp:wasmJsBrowserDistribution     # UI production build
./gradlew :server:run                               # Admin server (http://localhost:8082; Cloud Run injects PORT)
./gradlew :server:buildFatJar                       # server/build/libs/server-all.jar
./gradlew :server:test                              # server tests
```
