# Git Workflow

Same conventions as kei-1111.github.io, trimmed to what exists in this repository today.

## Commits

- [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/), written entirely in English: `<type>: <description>` or `<type>(scope): <description>`
- Types: feat, fix, docs, refactor, perf, test, build, ci, chore
- Scopes as they emerge: `app` (composeApp), `server`, `shared`, `deps`
- Description: imperative mood, one concise line, no trailing period
- Granularity: one self-contained logical change per commit

## Branches

- Name: `<type>/#<issue-number>` where type mirrors the Issue type: `feature` | `fix` | `refactor` | `docs` | `research` | `perf` | `test` | `ci` | `chore`
- Keep branches short-lived and synced with `main`

## Issues / Pull Requests

- Issue title `[<Type>]: <title>`; title and body in English; one responsibility per Issue
- PR title: the corresponding Issue title verbatim; base branch is always `main`
- PR body: `## Summary` / `## Related Issue` / `## Checklist`; keep PRs reviewable (up to ~500 lines)

## CI/CD

Mirrors kei-1111.github.io's structure at this repository's scale (JDK 21 temurin; autoCorrect disabled on CI):

- CI — 5 independent workflows on every PR to `main`: `detekt.yml` (`./gradlew detekt`), `compile-wasm.yml` (`:app:webApp:compileKotlinWasmJs`), `compile-android.yml` (`compileAndroidMain`), `app-test.yml` (the `testAndroidHostTest` tasks of `app:core:common` / `app:core:mvi` / `app:feature:home` — extend the list when a module gains unit tests), `server-test.yml` (`:server:test`).
- CD — not wired yet: a single Cloud Run deploy (fat jar via `:server:buildFatJar -PbundleWebApp`, bundling the admin UI) lands once GCP (WIF) is set up. GitHub Pages is deliberately not used — the admin UI must not sit on a public static URL.
- Docs-only gate: every gated workflow calls the reusable `detect-docs-only.yml` (PR files API on `pull_request`, `before...after` compare on `push`) and skips the heavy job when every changed file is documentation (`*.md`, `docs/**`, `.claude/**`). Unresolvable cases fail open; the gated jobs run under `!cancelled() && outputs.code != 'false'` so a failed gate also falls open. A skipped-by-`if:` job still satisfies required status checks.

## Prohibited

- Direct push to the `main` branch
- Force push on shared branches
- Massive file changes in a single PR
- Meaningless commit messages
