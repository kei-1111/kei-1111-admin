---
paths:
  - "server/src/**"
---

# Admin Server

Ktor (CIO) JVM server in `server/`, deployed to Cloud Run. Reference implementation for structure and testing: the `server/` tree in kei-1111.github.io.

## Layer Responsibilities (as the server grows)

- `routing/` — HTTP translation only, no policy: call the service, map its result to a response
- `service/` — owns storage and validation policy for images and content JSON, and the JSON (de)serialization of content documents (the `Json` instance lives here)
- `storage/` — `ContentStorage` is a raw blob (`String`/`ByteArray`) interface over the backends (GCS in production, local files under `DEV_AUTH_BYPASS`); it stays typed-model-free so both backends share one contract. This deliberately diverges from kei-1111.github.io, whose single GCS client owns typed (de)serialization
- Auth — verify the Google ID token (audience = OAuth client ID, email allowlist) in a Ktor plugin/interceptor before any admin route runs; `/health` stays public

## Conventions

- Credentials come from Application Default Credentials (the Cloud Run service account); never commit key files, never read tokens from source
- When catching broadly around suspend I/O, call `currentCoroutineContext().ensureActive()` before swallowing the exception — a cancelled request must not look like a normal failure
- Tests: JUnit 5 + kotlin.test assertions, Ktor `testApplication` for route-level tests; never hit real GCS or Google auth in tests — inject fakes/stubs at the wiring entry point
- Run with `./gradlew :server:test`
