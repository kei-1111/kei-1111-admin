---
paths:
  - ".github/**"
---

# CI/CD

Canonical for what the workflows run: the files in `.github/workflows/` themselves — this rule
keeps only the intent and invariants the YAML cannot state. Always-loaded summary:
`.claude/rules/git-workflow.md` — CI/CD.

- One independent workflow file per check. Because this repository is trunk-based, every check
  runs on pushes to `main` as well as on PRs — the push run is the primary gate, not a duplicate.
- The script-check workflows (`check-destination-isolation.yml`, `check-gradle-conventions.yml`)
  are cheap and never docs-only gated; every heavy job is.
- Docs-only gate: gated CI and CD files call the reusable `detect-docs-only.yml` (canonical for the
  documentation path patterns). Any unresolvable case (API failure, empty file list) fails open and
  runs normally — the gate job itself failing also falls open, since gated jobs run under
  `!cancelled() && outputs.code != 'false'` (without a status-check function an implicit `success()`
  would skip them). A skipped-by-`if:` job still satisfies required status checks.
- `deploy-server.yml` additionally narrows itself with a `paths:` filter. `app/**` belongs in that
  filter: the fat jar bundles the wasm admin UI via `-PbundleWebApp`, so a client-only change still
  changes the deployed artifact.
- The deploy job smoke-tests the new revision before finishing: `/health` must answer 200 and an
  admin route must answer 401 without a token. That pair catches both a dead revision and missing
  auth environment variables, which are the two failures a green build cannot rule out.
- A new module with unit tests must be added to `app-test.yml`; a new heavy check gets its own
  workflow file rather than another step in an existing one.
- `shared:model` runs on both of its consuming targets in `shared-test.yml`: JVM and wasmJs.
