# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. It is intentionally thin: detailed conventions live in `.claude/rules/*.md` (path-scoped, loaded automatically) and the project guide for all coding agents is `AGENTS.md`, which references `.claude/rules/*.md` for canonical detail.

## Project

portfolio-admin is the admin console for kei-1111.github.io: a Compose Multiplatform (wasmJs) UI plus a Ktor admin server that manage the images and text content the portfolio delivers, backed by a GCS bucket. Auth is a single allowlisted Google account (ID token verified server-side).

- Module trees mirroring kei-1111.github.io: `app/` (wasmJs admin UI — `app:webApp` entry/wiring, `app:core:*`, `app:feature:*`), `server/` (Ktor CIO admin API, Cloud Run), `shared/model/` (DTOs, wasmJs + jvm).
- Same infrastructure as kei-1111.github.io: convention plugins (`kei_1111.*` in `build-logic/`), detekt, MVI (`MviViewModel`), Metro DI, Navigation 3, Android target for Preview/host tests only. Versions are kept in sync with that repository unless there is a reason to diverge.

## Top-Level Rules

- Run independent read-only investigations concurrently rather than sequentially.
- Before any non-trivial edit or assertion, read the files involved and verify what you reference — API/class existence, the resolved dependency version, the running build (canonical: `AGENTS.md` Working Agreement).
- Escalate when stuck: after a few failed attempts without a confirmed root cause, consult the user instead of applying speculative fixes.
- Goal-driven execution: define verifiable success criteria before a non-trivial change and validate against them before reporting completion.

## Before Editing

- Inspect the current implementation and its nearest analogous code (here first, then kei-1111.github.io as the reference codebase).
- Read the applicable `.claude/rules/*.md` for the area being changed.
- Refer to `docs/ArchitectureOverview.md` / `docs/ModuleOverview.md` (and `AGENTS.md`) when needed.
- Treat current source code as authoritative when documentation has drifted.

## Working Principles

- Follow the Working Agreement in `AGENTS.md` (smallest coherent change, comment policy, documentation concision).
- Run the narrowest relevant validation (`./gradlew :app:feature:<name>:compileKotlinWasmJs`, `./gradlew :<module>:testAndroidHostTest`, `./gradlew :server:test`, `./gradlew detekt` — rerun detekt once if autoCorrect reformats; never fix import ordering manually).
- Commit messages and GitHub-authored text are written in English (see `.claude/rules/git-workflow.md`).
