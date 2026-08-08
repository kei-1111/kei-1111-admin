# AGENTS.md

This file gives coding agents project-specific guidance for working in this repository.

## Instruction Priority

When guidance conflicts, use this order:

1. The user's current request
2. This `AGENTS.md`
3. Current source code and build configuration
4. `README.md` (architecture) and `.claude/rules/*.md`

Treat source code as authoritative when documentation has drifted.

## Project Overview

portfolio-admin is the admin console for [kei-1111.github.io](https://github.com/kei-1111/kei-1111.github.io). It manages the images and text content (JSON) that the portfolio serves, stored in a GCS bucket that the portfolio server reads. See `README.md` for the architecture diagram and setup status.

Tech stack:

- Kotlin / Compose Multiplatform — **wasmJs** is the only client target (GitHub Pages); `app/` mirrors kei-1111.github.io's tree (`app:webApp` entry/wiring, `app:core:*`, `app:feature:*`)
- **Ktor server** (`server/`, JVM, CIO) — admin API on Cloud Run; reads/writes the GCS bucket
- `shared/model/` — DTOs shared by both (wasmJs + jvm)
- kotlinx.serialization
- Auth: Google Identity Services ID token from the UI, verified by the server against a single allowlisted account

kei-1111.github.io is the reference codebase: when adding infrastructure that exists there (detekt, convention plugins, CI workflows, test conventions), mirror its approach and versions rather than inventing a new one.

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
| Cross-module UI change (`app:*`, `shared:model`) | `./gradlew :app:webApp:compileKotlinWasmJs` |
| Server Kotlin | `./gradlew :server:test` (compiles and runs the server test suite) |
| Gradle or cross-module wiring | `./gradlew :app:webApp:wasmJsBrowserDistribution :server:test` |

Full command list (dev server, production build, server run, fat jar): `README.md` — Commands.

## Git And PR Rules

Canonical detail: `.claude/rules/git-workflow.md`.

- Commit messages: Conventional Commits in concise imperative English; branch names `<type>/#<issue-number>`; Issue and PR titles/bodies in English.
- Do not push directly to `main`; do not force-push a shared branch.
- Do not commit, push, create an Issue, or open a PR unless the user asks for that action.

## Safety And Maintenance

- Never expose secrets, credentials, tokens, or machine-specific configuration. GCS credentials come from the Cloud Run service account (ADC) — never commit key files.
- Declare all dependencies in `gradle/libs.versions.toml` and reference them via the version catalog; `implementation()` only, `api()` is prohibited.
- Do not add heavy dependencies without approval.
- When generated templates or docs disagree with current source code, the source wins.
- Keep this file updated when agent-level instructions change.
