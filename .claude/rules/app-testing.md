---
paths:
  - "app/**/src/commonTest/**"
---

# App Unit Testing

Sibling suite: `server-testing.md`. New logic is written test-first per `tdd.md`.

## Test Doubles: Hand-Written Fakes Only

No mocking library is used and none may be added — hand-written fakes are the officially preferred double ([Use test doubles in Android](https://developer.android.com/training/testing/fundamentals/test-doubles)).

- A fake is a private class (or anonymous object) in the test file implementing the dependency's interface and returning canned values (e.g. `flowOf(...)`). A fake shared by multiple test classes within one feature lives in that feature's `commonTest` `fake/` package (`mvi-testing.md` — Fakes).
- A fake complex enough to need its own tests is a design smell in the code under test — do not reach for a mock.

## Structure And Naming

- Arrange-Act-Assert, separated by blank lines.
- Test names are camelCase sentences describing the behavior (`clearsMemoBackToEmpty`), shared with `:server:test`. No backtick names — they are runtime-restricted; one convention keeps every suite uniform.
- One cohesive behavior per test: several assertions on one resulting object are fine; unrelated verifications bolted into one test are not.

## What To Test Per Layer

- **Shared helpers** (`app/core/common/src/commonTest/`): exercise the helper's observable contract directly — for the suppression helpers that means recovery, cancellation propagation, and catch-type selectivity. Reference: `SuppressionTest.kt`.
- **ViewModel** (`app/feature/<name>/src/commonTest/` and the `MviViewModel` base in `app/core/mvi`): stimulate through `onIntent` or a fake-boundary emission and assert the observable `State` / `Effect` outcomes — never internal calls. ViewModel-specific conventions: `mvi-testing.md` (canonical).
- **Repository** (`app/core/data/src/commonTest/`): construct the `internal` implementation with Ktor `MockEngine`; assert the request method,
  relative path, relevant headers, decoded value, and propagated failures.
- **UseCase** (`app/core/domain/src/commonTest/`): construct the `internal` implementation with a hand-written repository fake; assert the delegated
  method, unchanged arguments, and unchanged return value.
- Do not test the dependency's own implementation, the Kotlin stdlib, or coroutines library behavior.

## Anti-Patterns (Prohibited)

Mocking libraries / over-mocking; asserting implementation details instead of observable behavior; unrelated assertions piled into one test; backtick test names. Process-level anti-patterns: `tdd.md`.

## Stack And Running

`kotlin-test` + `kotlinx-coroutines-test` — `runTest {}` with `toList()` for finite cold flows; Turbine is deliberately not a dependency. Shared test infrastructure (`ViewModelTestBase`, `startCollecting`) lives in `app:core:testing`, wired into every feature's `commonTest` by `KmpFeaturePlugin`. Tests run on the non-shipped Android target as host tests — local JVM, no emulator, no Robolectric. The set of modules with host tests is canonical in `.github/workflows/app-test.yml`: run the tasks it lists, and extend that file when a module gains its first unit test.

`shared:model` has no Android target, so its tests run on both consuming targets instead (`.github/workflows/shared-test.yml`).
