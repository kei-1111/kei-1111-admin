# AGENTS.md

This file gives coding agents project-specific guidance for working in this repository.

## Instruction Priority

When guidance conflicts, use this order:

1. The user's current request
2. This `AGENTS.md`
3. Current source code and build configuration
4. `docs/ArchitectureOverview.md` / `docs/ModuleOverview.md` and `.claude/rules/*.md`

Treat source code as authoritative when documentation has drifted.

## Project Overview

portfolio-admin is the admin console for [kei-1111.github.io](https://github.com/kei-1111/kei-1111.github.io). It manages the images and text content (JSON) that the portfolio serves, stored in a GCS bucket that the portfolio server reads. See `README.md` for the architecture diagram and setup status.

Tech stack (mirrors kei-1111.github.io):

- Kotlin / Compose Multiplatform — **wasmJs** is the only distribution target for the client, bundled into the server's fat jar (`-PbundleWebApp`) and served by the admin server itself (same origin, no CORS; deliberately not GitHub Pages). The **Android** target exists for exactly two roles: rendering commonMain `@Preview` and running the client unit tests on the local JVM (host tests) — never shipped
- **Ktor server** (`server/`, JVM, CIO) — admin API on Cloud Run; reads/writes the GCS bucket
- Multimodule Clean Architecture + MVI using `MviViewModel<ViewModelState, State, Intent>` (`app:core:mvi`)
- Metro DI (`@ContributesBinding` / `@ContributesIntoMap` / `@Inject`), `metrox-viewmodel` (`metroViewModel()`)
- Navigation 3 (`androidx.navigation3`), a single `NavDisplay` + `NavKey` back stack
- kotlinx.serialization; detekt (autoCorrect enabled locally, disabled on CI)
- `AdminTheme` (`app:core:designsystem`) wrapping Material3 — no custom design system yet
- Auth: Google Identity Services ID token from the UI, verified by the server against a single allowlisted account

kei-1111.github.io is the reference codebase: when adding infrastructure that exists there (CI workflows, E2E, data-layer conventions), mirror its approach and versions rather than inventing a new one. Workflow skills are not duplicated here — they live in kei-1111.github.io until the planned shared-repository extraction.

## Read First

- `docs/ArchitectureOverview.md` — data flow, DI, navigation (Japanese)
- `docs/ModuleOverview.md` — module dependency graph and per-module responsibilities (Japanese)
- `.claude/rules/*.md` — per-area conventions; the canonical homes this file's rule sections summarize and point to

## Working Agreement

Before editing:

- Inspect the files being changed and their nearest analogous implementation.
- Check `git status`; preserve user changes and avoid unrelated cleanup.
- Verify referenced APIs, tasks, modules, and paths in the current checkout instead of relying on documentation alone.
- For a non-trivial change, define verifiable success criteria first and validate against them before reporting completion.

While editing:

- Make the smallest coherent change that satisfies the request.
- Follow existing module boundaries and naming before introducing a new abstraction.
- Keep refactors separate from behavior changes unless the refactor is required.
- A comment may state only what cannot be learned from the code itself — rationale, external constraints, workarounds, non-obvious semantics. Delete comments that restate a name, signature, or the adjacent code.
- Keep documentation concise and proportional.
- Escalate when stuck: after a few failed attempts without a confirmed root cause, stop and consult the user instead of applying speculative fixes.

Before handing off:

- Review the final diff for accidental or unrelated changes.
- Verify before asserting: check API existence and behavior against the resolved dependency version; confirm the running build actually contains the change; separate observation from speculation when reporting.
- Run the narrowest relevant validation and report what changed, what was validated, and anything not validated.

## Build And Validation

| Change | Minimum validation |
|---|---|
| Kotlin in one UI module | `./gradlew :app:feature:<name>:compileKotlinWasmJs` (or the changed module) |
| Unit-tested logic (`app:core:common` helpers, `app:core:mvi`, feature ViewModels) | `./gradlew :<module>:testAndroidHostTest` |
| Compose UI or Preview | Feature wasm compile + `./gradlew :app:feature:<name>:compileAndroidMain` |
| Navigation, DI, Gradle, or app wiring | `./gradlew :app:webApp:wasmJsBrowserDistribution` |
| Server Kotlin | `./gradlew :server:test` (compiles and runs the server test suite) |
| Formatting or lint-sensitive Kotlin | `./gradlew detekt`; rerun if auto-correct changed files |

Full command list: `.claude/rules/gradle.md` — Build Commands (canonical home). New logic on both the client and `:server` follows TDD per `.claude/rules/tdd.md`. Do not claim browser behavior was verified when only compilation ran (`.claude/rules/ui-implementation.md` — Browser Smoke Test).

## Git And PR Rules

Canonical detail: `.claude/rules/git-workflow.md`.

- Commit messages: Conventional Commits in concise imperative English; branch names `<type>/#<issue-number>`; Issue and PR titles/bodies in English.
- Do not push directly to `main`; do not force-push a shared branch.
- Do not commit, push, create an Issue, or open a PR unless the user asks for that action.

## Safety And Maintenance

- Never expose secrets, credentials, tokens, or machine-specific configuration. GCS credentials come from the Cloud Run service account (ADC) — never commit key files.
- The Android target has two roles only — Preview rendering and host tests: androidMain actuals may be no-op stubs; never add Android-specific runtime features or network calls there.
- Declare all dependencies in `gradle/libs.versions.toml` and reference them via the version catalog, including inside convention plugins (`libs.findLibrary(...)`); `implementation()` only, `api()` is prohibited.
- Prefer the existing convention plugins (`kei_1111.detekt`, `kei_1111.kmp.wasm`, `kei_1111.cmp`, `kei_1111.kmp.feature`, `kei_1111.kmp.shared`, `kei_1111.metro`) over ad hoc Gradle configuration.
- Do not add heavy dependencies without approval.
- When generated templates or docs disagree with current source code, the source wins.
- Keep this file updated when agent-level instructions change.
