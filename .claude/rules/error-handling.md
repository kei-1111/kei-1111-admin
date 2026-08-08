---
paths:
  - "app/core/common/**/result/**/*.kt"
  - "app/core/common/**/coroutines/**/*.kt"
  - "app/feature/**/*ViewModel.kt"
  - "app/feature/**/*ViewModelState.kt"
---

# Error Handling Patterns

## Result + asResult() Layering

Same layering as kei-1111.github.io — applies as soon as a data layer exists here:

| Layer | Rule |
|---|---|
| Repository | Return plain `Flow<T>` — no `runCatching`, no `Result` wrapping |
| UseCase | Pass-through `Flow<T>` + `.distinctUntilChanged()` — still no `Result` wrapping |
| ViewModel | Apply `.asResult()` at the subscription point, store the whole `Result` in `ViewModelState`, handle with a `when (result)` expression |

This app also writes (uploads, content saves). Write operations return results explicitly through the API layer's typed responses — define the convention with the first write path and record it here; do not scatter ad-hoc `runCatching` + `onSuccess`/`onFailure`.

## Result Type

The custom sealed interface `Result<T>` (`Success(data)` / `Error(exception)` / `Loading`) and `Flow<T>.asResult()` live in `app/core/common/src/commonMain/kotlin/.../result/` — **not** `kotlin.Result`. `asResult()` maps emissions to `Success`, prepends `Loading` via `onStart`, and catches into `Error`.

Because data flows can throw, **every** ViewModel collector guards with `.asResult()` — via the `MviViewModel` helpers `collectAsResult()` / `prefetchAsResult()`, or directly when a side effect must ride along. A bare `collect`/`launchIn` lets the exception kill the coroutine scope.

- `toState()` unwraps `Success` into the data fields (`Loading` surfaces as `null` = "no data yet") and derives failure flags from `Error`.
- There is no `statusType` enum — do not introduce one.

## Cancellation-Safe Suppression Helpers

`recoverOrElse(block, onFailure)` and `runBestEffort(block)` (`app/core/common/src/commonMain/kotlin/.../coroutines/Suppression.kt`) encode the "swallow the failure but always propagate coroutine cancellation" policy once (`ensureActive()` before recovering). Suppression sites must use them — no hand-written broad `try/catch`. The helpers' existence does not authorize new suppression sites; document each one here as it is added.

## Prohibited Patterns

| Pattern | Alternative |
|---|---|
| `runCatching` inside a Repository `Flow` | Return plain `Flow<T>`; let `.asResult()` handle it at the ViewModel boundary |
| `kotlin.Result` in Repository/UseCase signatures | The custom `result.Result` at the ViewModel boundary only |
| Swallowing an exception anywhere else | Not permitted without a documented site using the suppression helpers |
