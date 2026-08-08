# Git Workflow

Same conventions as kei-1111.github.io, trimmed to what exists in this repository today.

## Commits

- [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/), written entirely in English: `<type>: <description>` or `<type>(scope): <description>`
- Types: feat, fix, docs, refactor, perf, test, build, ci, chore
- Scopes as they emerge: `app` (composeApp), `server`, `shared`, `deps`
- Description: imperative mood, one concise line, no trailing period
- Granularity: one self-contained logical change per commit

## Branches / Pull Requests

Trunk-based by owner decision (2026-08-08): commit directly to `main`, one verified logical change per commit — no Issue/PR ceremony required. Branches + PRs remain available for changes the owner explicitly wants reviewed; then name branches `<type>/#<issue-number>` and follow kei-1111.github.io's Issue/PR formats.

## CI/CD

Mirrors kei-1111.github.io's structure at this repository's scale (JDK 21 temurin; autoCorrect disabled on CI):

- CI — 5 independent workflows on every PR to `main` **and every push to `main`** (trunk-based flow means pushes are the primary trigger): `detekt.yml` (`./gradlew detekt`), `compile-wasm.yml` (`:app:webApp:compileKotlinWasmJs`), `compile-android.yml` (`compileAndroidMain`), `app-test.yml` (the `testAndroidHostTest` tasks of `app:core:common` / `app:core:mvi` / `app:feature:home` — extend the list when a module gains unit tests), `server-test.yml` (`:server:test`).
- CD — `deploy-server.yml` on push to `main` (docs-only gated): builds the fat jar with the bundled admin UI (`:server:buildFatJar -PbundleWebApp`), pushes the image to Artifact Registry (`kei-1111`), and deploys Cloud Run service `kei-1111-admin` (project `kei-1111`, asia-northeast1, runtime SA `kei-1111-admin-runtime@`, WIF via the shared `github` pool). GitHub Pages is deliberately not used — the admin UI must not sit on a public static URL.
- Docs-only gate: every gated workflow calls the reusable `detect-docs-only.yml` (PR files API on `pull_request`, `before...after` compare on `push`) and skips the heavy job when every changed file is documentation (`*.md`, `docs/**`, `.claude/**`). Unresolvable cases fail open; the gated jobs run under `!cancelled() && outputs.code != 'false'` so a failed gate also falls open. A skipped-by-`if:` job still satisfies required status checks.

## Prohibited

- Pushing to `main` without running the relevant validation first (the pre-push detekt hook is a backstop, not the validation)
- Force push on `main`
- Meaningless commit messages
