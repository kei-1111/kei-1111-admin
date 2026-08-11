# AGENTS.md

This file gives coding agents project-specific guidance for working in this repository.

## Working Agreement

Apply `.claude/rules/working-agreement.md` in full — instruction priority, editing and comment
policy, per-change validation, hand-off checklist, and safety rules all live there as the single
source of truth. This file adds only the project orientation below; per-tree invariants live in
`app/AGENTS.md`, `server/AGENTS.md`, and `shared/AGENTS.md`.

## Project Overview

kei-1111-admin is the admin console for [kei-1111.github.io](https://github.com/kei-1111/kei-1111.github.io). It manages the images and text content (JSON) that the portfolio serves, stored in a GCS bucket that the portfolio server reads. See `README.md` for the architecture diagram and setup status.

Tech stack (mirrors kei-1111.github.io):

- Kotlin / Compose Multiplatform — **wasmJs** is the only distribution target for the client, bundled into the server's fat jar (`-PbundleWebApp`) and served by the admin server itself (same origin, no CORS; deliberately not GitHub Pages). The **Android** target exists for exactly two roles: rendering commonMain `@Preview` and running the client unit tests on the local JVM (host tests) — never shipped
- **Ktor server** (`server/`, JVM, CIO) — admin API on Cloud Run; reads/writes the GCS bucket
- Multimodule Clean Architecture + MVI using `MviViewModel<ViewModelState, State, Intent>` (`app:core:mvi`)
- Metro DI (`@ContributesBinding` / `@ContributesIntoMap` / `@Inject`), `metrox-viewmodel` (`metroViewModel()`)
- Navigation 3 (`androidx.navigation3`), a single `NavDisplay` + `NavKey` back stack
- kotlinx.serialization; detekt (autoCorrect enabled locally, disabled on CI)
- `KeiTheme` (`app:core:designsystem`) — custom design system tokens (`KeiTheme.colors` / `typography` / `shapes` / `icons`)
- Auth: Google Identity Services ID token from the UI, verified by the server against a single allowlisted account

kei-1111.github.io is the reference codebase: when adding infrastructure that exists there (CI workflows, E2E, data-layer conventions), mirror its approach and versions rather than inventing a new one. Workflow skills are not duplicated here — they live in kei-1111.github.io until the planned shared-repository extraction.

## Read First

- `.claude/rules/working-agreement.md` — the working rules every agent applies
- `docs/ArchitectureOverview.md` — data flow, DI, navigation (Japanese; English mirror `.en.md`)
- `docs/ModuleOverview.md` — module dependency graph and per-module responsibilities (Japanese; English mirror `.en.md`)
- `.claude/rules/*.md` — per-area conventions; the canonical homes this file points to

## Build And Validation

Per-change validation is canonical in `.claude/rules/working-agreement.md` — Build And Validation;
the full command list is canonical in `.claude/rules/gradle.md` — Build Commands. New logic on both
the client and `:server` follows TDD per `.claude/rules/tdd.md`.

## Git And PR Rules

Canonical detail: `.claude/rules/git-workflow.md`.

- Commit messages: Conventional Commits in concise imperative English; Issue and PR titles/bodies in English.
- Trunk-based by owner decision (2026-08-08): commit verified logical changes directly to `main`; branches + PRs (named `<type>/#<issue-number>`) only for changes the owner explicitly wants reviewed. Never force-push `main`, and never push without running the relevant validation first.
- Do not commit, push, create an Issue, or open a PR unless the user asks for that action.

## Safety And Maintenance

Canonical: `.claude/rules/working-agreement.md` — Safety And Maintenance. The invariants worth
restating at the entry point:

- Never expose secrets, credentials, tokens, or machine-specific configuration. GCS credentials come from the Cloud Run service account (ADC) — never commit key files.
- The Android target has two roles only — Preview rendering and host tests: androidMain actuals may be no-op stubs; never add Android-specific runtime features or network calls there.
- Every dependency is declared in `gradle/libs.versions.toml` and referenced through the catalog, with `implementation()` only — `api()` is prohibited.
- Keep this file and `.claude/rules/working-agreement.md` updated together when agent-level instructions change.
