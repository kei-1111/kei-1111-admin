---
paths:
  - "app/core/mvi/**/*.kt"
  - "app/feature/**/*ViewModel.kt"
  - "app/feature/**/*State.kt"
  - "app/feature/**/*Intent.kt"
  - "app/feature/**/*Effect.kt"
  - "app/feature/**/*ViewModelState.kt"
---

# MVI Architecture Guide

Base types live in `app/core/mvi` (ported from kei-1111.github.io): `MviViewModel<VS, S, I>`, the `Intent` / `State` / `ViewModelState<S>` marker interfaces, and the `MviEffect` composable.

## Core Components

| Component | Role |
|---|---|
| `Intent` | User action passed to the ViewModel; marker `interface Intent` |
| `State` | Screen rendering state exposed to the UI; carries `effect` when the screen has one-shot side effects; marker `interface State` |
| `ViewModelState` | Internal ViewModel state; `interface ViewModelState<S : State> { fun toState(): S }` |
| `Effect` | One-shot side effect (navigation, opening a URL); a plain `sealed interface`, not an `app/core/mvi` type |

No `statusType` concept — loading/error phases are the custom `Result<T>` stored directly on `ViewModelState` (see `.claude/rules/error-handling.md`).

## ViewModel Pattern (Metro)

All destination ViewModels extend `MviViewModel<VS, S, I>`: `state` is derived from the internal `MutableStateFlow` via `toState()` with `WhileSubscribed(5_000)`; subclasses implement `createInitialViewModelState()` / `createInitialState()` / `onIntent` and mutate via `updateViewModelState { copy(...) }`.

- Declare `internal class`, annotated class-level `@Inject`, `@ViewModelKey`, `@ContributesIntoMap(AppScope::class, binding<ViewModel>())` — `binding<ViewModel>()` is required because `MviViewModel<...>` is the sole declared supertype but the multibinding map expects `ViewModel`.
- Obtained in a navigation entry via `metroViewModel()`, never constructed manually.
- Unit-tested per `.claude/rules/mvi-testing.md` (Android host tests, public-contract-only assertions).

### onIntent Policy

Write branch logic **inline** in the `when (intent)` — no private per-intent handler functions. Private helpers are allowed for init/observe-style flows. `@Suppress("CyclomaticComplexMethod")` on `onIntent` is acceptable when the inline `when` grows large. Every sealed-type `when` branch takes `is` — `data object` branches included.

Never re-dispatch another Intent from inside an `onIntent` branch. A state transformation shared by two or more state-update sites may be extracted as a private function only if it is a pure leaf `ViewModelState → ViewModelState` transformation (no dependency reads, logging, launches, or `updateViewModelState` calls) — `MutableStateFlow.update {}` may re-run the lambda on contention, so purity is correctness, not style.

## File Structure

MVI files sit at the `destination/<name>/` top level next to `XxxScreenRoot.kt` / `XxxScreen.kt` (directory layout: `.claude/rules/ui-implementation.md`):

| File | Content |
|---|---|
| `XxxViewModelState.kt` | `internal data class`, implements `ViewModelState<XxxState>`; may hold detail the UI doesn't need; includes `effect: XxxEffect?` when the screen has effects |
| `XxxState.kt` | `internal data class`, implements `State`; exposed via `viewModel.state` |
| `XxxIntent.kt` | `internal sealed interface : Intent`; when the screen has effects, always includes a `data object ConsumeEffect` |
| `XxxEffect.kt` | `internal sealed interface`; cleared back to `null` once handled; omit while the screen has no effects |
| `XxxViewModel.kt` | `internal class`, extends `MviViewModel<XxxViewModelState, XxxState, XxxIntent>()` |

Reference shape: `app/feature/home/.../destination/home/` (no effects yet — the first screen that needs one adds the `Effect` file plus `ConsumeEffect` and becomes the reference).

## Effect Handling

Consume an Effect only through the `MviEffect` composable (`app/core/mvi/.../MviEffect.kt`): for a non-null `effect` it runs the handler inside `LaunchedEffect(effect)` and then fires `onConsume` automatically.

```kt
MviEffect(
    effect = state.effect,
    onConsume = { viewModel.onIntent(XxxIntent.ConsumeEffect) },
) { effect ->
    when (effect) {
        is XxxEffect.OpenUrl -> openUrl(effect.url)
    }
}
```

Never handle an Effect without also wiring `ConsumeEffect`, or it will keep re-firing on recomposition.

## Data Flow

UI dispatches an `Intent` → `ViewModel.onIntent` updates the internal state with `updateViewModelState { copy(...) }` → `ViewModelState.toState()` derives the public `State` and the UI recomposes → `MviEffect` handles a non-null `effect`, then automatically dispatches `ConsumeEffect`.
