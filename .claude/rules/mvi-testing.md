---
paths:
  - "app/feature/**/src/*Test/**/*.kt"
  - "app/core/mvi/src/*Test/**/*.kt"
  - "app/core/testing/**/*.kt"
---

# MVI ViewModel Testing

ViewModel-specific conventions layered on `app-testing.md` (canonical for the stack, fake policy, naming, and anti-patterns) and `tdd.md` (canonical for the test-first process). Sibling suite: `server-testing.md`.

## Scope

- Unit tests for `MviViewModel` subclasses: the observable outcome of one stimulus — an Intent via `onIntent`, or an emission from a controllable fake boundary — on the public `state` (including `effect`). Rendering and the `MviEffect` composable are out of scope.
- Run: `./gradlew :app:feature:<name>:testAndroidHostTest` — local JVM host test.
- Construct the ViewModel directly with fakes — never through Metro; the DI annotations are inert metadata in tests.
- Placement: `src/commonTest/kotlin`, same package as the production code, file named `XxxViewModelTest.kt`.

## Coroutine Setup — MUST

`viewModelScope` binds to `Dispatchers.Main` — replace it with a test dispatcher **before the ViewModel is constructed** (its `init` may already launch coroutines). Extend `ViewModelTestBase` (`app:core:testing`, wired into every feature's `commonTest` by `KmpFeaturePlugin`): its `@BeforeTest` / `@AfterTest` handle `Dispatchers.setMain(StandardTestDispatcher())` / `resetMain()`.

`runTest` reuses the mocked Main dispatcher's scheduler, so `runCurrent()` advances both. Write tests as expression bodies (`fun x() = runTest { ... }`) — required for the wasmJs target that shares `commonTest`.

## Collect First, Then Intent — MUST

`MviViewModel.state` uses `WhileSubscribed(5_000)`: with no collector the `toState()` mapping never runs and `state.value` stays frozen. Every test that asserts on state calls `startCollecting(viewModel.state)` (`app:core:testing`) before dispatching anything, then `runCurrent()` after every `onIntent` / fake emission before asserting. Reference: `HomeViewModelTest`.

- Use `advanceUntilIdle()` / `advanceTimeBy()` only when the code under test uses `delay`; default to `runCurrent()`.
- To assert intermediate transitions, collect into a list with `state.toList(collected)` (see `MviViewModelTest`).

## Fakes

Policy (fake-only, no mocking library) and placement: `app-testing.md` (canonical). A fake shared by multiple test classes in one feature lives in the feature's `commonTest` `fake/` package as `Fake{Name}`. Flow-returning fakes use `MutableSharedFlow(replay = 1)` + a test-only `emit()`; call `runCurrent()` between emissions.

## Public Contract Only — MUST

Stimulate only through `onIntent` or fake-boundary emissions; assert only through `state`. Never touch `_viewModelState`, the `ViewModelState` type, or private helpers from a test. The one deliberate exception is `MviViewModelTest.keepsPublicStateAtInitialValueWithoutCollector`, a characterization test pinning `WhileSubscribed` behavior — not a template for feature tests.

## Effects

Effect emission and effect consumption are two behaviors — test them separately: one test asserts the Intent sets the expected `state.effect`; another arranges an effect and asserts `ConsumeEffect` clears it back to `null`.

## Time-Dependent Logic

`runTest` virtualizes `delay`, but `TimeSource.Monotonic` readings do **not** follow virtual time. New ViewModel code that reads a clock MUST accept a `TimeSource` constructor parameter defaulting to `TimeSource.Monotonic` so tests can inject `TestTimeSource`.

## Red → Green for a New Intent

The process is `tdd.md`. The VM-specific micro-cycle for a NEW Intent subtype: write the test (compile-failure red is valid) → add only the minimal contract to compile (Intent subtype + no-op branch) → observe the assertion failure → implement the minimal branch (green) → refactor.
