# AGENTS.md — server/

Rules for the Ktor admin server (`server/`). The root `AGENTS.md` still applies. Detailed
conventions live in the canonical rules below; keep this file limited to server-scoped invariants
that are useful at the entry point.

## Canonical Rules

- Implementation, layering, and failure policy: `.claude/rules/server.md`
- Testing: `.claude/rules/server-testing.md`, `.claude/rules/tdd.md`
- Deployment and its gates: `.claude/rules/ci-cd.md`

## Server-Scoped Invariants

- The Cloud Run service is deployed `--allow-unauthenticated` so it can serve the UI and `/health`;
  every admin route is protected in the application layer instead. Adding a route outside the
  authenticated block is a security decision, not a routing detail.
- Auth fails closed: missing `GOOGLE_OAUTH_CLIENT_ID` / `ADMIN_ALLOWED_EMAIL` must reject every
  token rather than accept any.
- `DEV_AUTH_BYPASS` is local-only, must stay off in every deployed configuration, and must keep
  logging a warning when on.
- Broad catches around suspend I/O stay cancellation-safe; the mechanism is canonical in
  `.claude/rules/server.md`.
- Content writes overwrite whole documents — a partial or empty write is data loss, so validation
  belongs before the write, in the service layer.
