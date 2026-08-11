---
paths:
  - "app/core/data/**/*.kt"
---

# Data Layer

`app/core/data` is the only module that talks to the admin server. It is a request/response HTTP
layer, not a stream/cache layer — a deliberate divergence from kei-1111.github.io, whose read-only
portfolio data justifies `Flow` + `SingleFlightCache`. Here every operation is an explicit action
the operator triggered, and stale cached content would be actively harmful when the same operator
is editing it.

## Shape

- One `AdminXxxRepository` interface + `AdminXxxRepositoryImpl` per server area (content, image,
  publish, preview). The interface names the capability; the impl owns the URL and the wire types.
- Operations are `suspend fun`, returning the decoded model. No `Flow`, no `Result`, no
  `runCatching` — failures propagate as exceptions and are absorbed once, at the ViewModel boundary
  (`.claude/rules/error-handling.md`).
- Impls are `internal`, annotated `@ContributesBinding(AppScope::class)` + `@SingleIn(AppScope::class)`
  + `@Inject`; consumers depend on the interface only. Feature modules never inject a repository
  directly — they go through a UseCase (`.claude/rules/usecase.md`).

## HTTP

- The admin UI is served by the admin server itself, so requests use **relative** URLs; never
  hardcode an absolute admin-server origin.
- Every authenticated request goes through the shared `authorize()` / `putJsonAuthorized(...)`
  helpers in `AdminApiClient.kt` — do not attach the bearer header by hand at a call site.
- The shared `HttpClient` is provided once in `di/DataBindings.kt` with `ignoreUnknownKeys = true`,
  so a server that gained a field does not break an older client.

## Testing

Repository impls are unit-tested against Ktor's `MockEngine` (assert the method, path, and decoded
result — not the client internals). Conventions: `.claude/rules/app-testing.md`.
