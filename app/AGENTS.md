# AGENTS.md — app/

Rules for the wasm admin client (`app/*`). The root `AGENTS.md` still applies. Detailed conventions
live in the canonical rules below; keep this file limited to app-scoped invariants that are useful
at the entry point.

## Canonical Rules

- Architecture and MVI: `.claude/rules/mvi-architecture.md`, `.claude/rules/usecase.md`
- Data, DI, and failure boundaries: `.claude/rules/data-layer.md`, `.claude/rules/error-handling.md`
- Navigation: `.claude/rules/navigation.md`
- UI and Preview: `.claude/rules/ui-implementation.md`, `.claude/rules/preview.md`
- Naming: `.claude/rules/naming-conventions.md`
- Testing: `.claude/rules/app-testing.md`, `.claude/rules/mvi-testing.md`, `.claude/rules/tdd.md`

## App-Scoped Invariants

- Feature modules depend on UseCases, never on a Repository or the data layer directly.
- A destination does not reference a sibling destination or its components; `scripts/check_destination_isolation.sh` enforces this in CI.
- The wasm client is served by the admin server itself — requests use relative URLs and there is no separate client origin to configure.
- The Android target exists only for Preview rendering and host tests; androidMain actuals stay no-op stubs.
- Tests use hand-written fakes and assert observable behavior; do not add a mocking framework.
- User-visible wasm changes require the browser smoke-test procedure from `ui-implementation.md`.
