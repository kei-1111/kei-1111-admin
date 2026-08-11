---
paths:
  - "app/core/common/**/result/**/*.kt"
  - "app/core/common/**/coroutines/**/*.kt"
  - "app/feature/**/*ViewModel.kt"
  - "app/feature/**/*ViewModelState.kt"
---

# Error Handling Patterns

## Established Layering (suspend + exceptions)

This repository's data layer is write-heavy request/response, so it deliberately diverges from kei-1111.github.io's `Flow` + `asResult()` read pipeline. The established convention (see `AdminContentRepository` and `WorkbenchViewModel`):

| Layer | Rule |
|---|---|
| Repository | `suspend fun` per operation; failures propagate as exceptions — no `runCatching`, no `Result` wrapping, no `Flow` for request/response calls |
| UseCase | Thin `suspend` pass-through — still no wrapping |
| ViewModel | The **only** boundary that absorbs failures: wrap each call site with `recoverOrElse(block) { fallback }` and fold the outcome into `ViewModelState` flags (e.g. `syncError: SyncErrorKind?`) |

Do not scatter ad-hoc `runCatching` + `onSuccess`/`onFailure` — the boundary is the ViewModel, once per call site.

## Result Type (available, not yet in use)

The custom sealed interface `Result<T>` (`Success(data)` / `Error(exception)` / `Loading`) and `Flow<T>.asResult()` live in `app/core/common/src/commonMain/kotlin/.../result/` — **not** `kotlin.Result`. `MviViewModel` carries the matching `collectAsResult()` / `prefetchAsResult()` helpers. This machinery is ported from kei-1111.github.io for observe-style read paths (continuous `Flow` sources); no such path exists here yet, so current code does not wrap with `Result` — adopt it only when a genuinely stream-shaped data source appears, and record that adoption here. If adopted, a bare `collect`/`launchIn` is prohibited (an exception would kill the coroutine scope): every collector guards with `.asResult()`, `toState()` unwraps `Success` into data fields, and failure flags derive from `Error`.

- There is no `statusType` enum — do not introduce one.

## Cancellation-Safe Suppression Helpers

`recoverOrElse(block, onFailure)` and `runBestEffort(block)` (`app/core/common/src/commonMain/kotlin/.../coroutines/Suppression.kt`) encode the "swallow the failure but always propagate coroutine cancellation" policy once (`ensureActive()` before recovering). Suppression sites must use them — no hand-written broad `try/catch`. The helpers' existence does not authorize new suppression sites; the sanctioned ones are the ViewModel data-call boundaries described above.

## Prohibited Patterns

| Pattern | Alternative |
|---|---|
| `runCatching` / broad `try/catch` in Repository or UseCase | Let the exception propagate; the ViewModel boundary handles it |
| `kotlin.Result` in any signature | Exceptions + `recoverOrElse` (or the custom `result.Result` if a Flow path is ever adopted) |
| Swallowing an exception outside a documented boundary | Not permitted without a documented site using the suppression helpers |
