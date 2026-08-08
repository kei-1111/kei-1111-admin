---
paths:
  - "server/src/test/**"
---

# Server Testing

Sibling suite: `app-testing.md` (client unit tests; ViewModel specifics in `mvi-testing.md`). New server logic is written test-first per `tdd.md`.

- Stack: JUnit 5 + kotlin.test assertions; Ktor `testApplication` for route-level tests and `MockEngine` (or hand-written fakes at the wiring seam) to stub external services — tests never hit real GCS or Google auth.
- Run with `./gradlew :server:test`.
- Keep a wiring entry point that accepts injected collaborators (mirror kei-1111.github.io's `Application.configureApplication(...)` seam) so route tests construct the app with fakes.
- Test names are camelCase sentences describing the behavior; no backtick names (shared convention with the client suites).
- Follow the existing tests in `server/src/test/` as the reference for structure and naming.
