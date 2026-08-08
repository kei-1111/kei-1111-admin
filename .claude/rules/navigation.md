---
paths:
  - "app/webApp/**/navigation/**/*.kt"
  - "app/feature/**/navigation/**/*.kt"
  - "app/core/navigation/**/*.kt"
---

# Navigation Guide

Navigation 3 (`androidx.navigation3`): a single `NavDisplay` + single flat `NavBackStack`, owned by `AppNavDisplay` (`app/webApp/src/commonMain/kotlin/.../navigation/AppNavDisplay.kt`).

## Per-Feature File Layout

| File | Role |
|---|---|
| `navigation/{Feature}NavigationRoute.kt` | `@Serializable data object Xxx : NavKey` definition(s), any result type produced by those destinations, and the feature's contributed NavKey `SerializersModule` fragment (CRITICAL below) |
| `navigation/{Feature}NavigationExtensions.kt` | `fun NavBackStack<NavKey>.navigateXxx() = add(Xxx)` extensions. Omit when nothing navigates to the feature's destinations |
| `navigation/{Feature}Navigation.kt` | `EntryProviderScope<NavKey>.{feature}Entries()` registering the feature's destinations; the `ViewModel` is obtained **inside** the `entry<...> { }` block via `metroViewModel()` — never constructed manually or passed in |

Current example: `app/feature/workbench` (`Workbench`, `workbenchEntries()`).

## AppNavDisplay

`AppNavDisplay` calls every feature's entries function into one `entryProvider`; the back stack is flat, so back handling is a single guarded `if (backStack.size > 1) backStack.removeLastOrNull()`. Base transitions are set globally via `transitionSpec` / `popTransitionSpec`; dialog presentation is declared per entry through metadata.

## Cross-Feature Navigation

Passed as a plain lambda parameter on `{feature}Entries()` — a feature never depends on another feature module or a shared navigation module.

## CRITICAL: Contribute Every NavKey to the SerializersModule Set

wasmJs has no reflection, so the open-polymorphic `NavKey` back stack cannot restore itself automatically. Each `{Feature}NavigationRoute.kt` contributes its keys beside their declarations: a `@BindingContainer @ContributesTo(AppScope::class)` interface whose companion `@Provides @IntoSet` function returns a `SerializersModule` with `polymorphic(NavKey::class) { subclass(Xxx::class, Xxx.serializer()) }`. Metro aggregates them as `AppGraph.navKeySerializers: Set<SerializersModule>`, and `AppNavDisplay` merges them into its `SavedStateConfiguration`. **A new `NavKey` must be added to the fragment in its own file** — the app compiles without it but back-stack save/restore silently breaks (or crashes) on that destination.

## Adding a New Destination

1. Add the `NavKey` and any result type to `{Feature}NavigationRoute.kt`, and its `navigate{Destination}` extension to `{Feature}NavigationExtensions.kt`.
2. Register the entry in `{Feature}Navigation.kt`'s `{feature}Entries()`, obtaining the `ViewModel` via `metroViewModel()` inside the `entry<...> { }` block.
3. Add the new `NavKey` to the same file's contributed `SerializersModule` fragment (a new feature creates one) — do not skip this (CRITICAL above).
4. For a new feature module, wire its `{feature}Entries()` into `AppNavDisplay`'s `entryProvider { ... }`, passing any cross-feature navigation lambdas.

## Dialog Destinations and Cross-Destination Results

Dialogs and command palettes are destinations, not ad-hoc UI state, whenever the user navigates to and backs out of them. A dialog declares itself with `entry<X>(metadata = dialogTransition())` and is rendered by `app:core:navigation`'s `InlineDialogSceneStrategy` in the same Compose scene as the entry beneath it — the strategy owns the overlay, centering, dialog semantics, Escape, and outside-click dismissal. Omitting the metadata compiles and then renders full-window, so verify in a browser.

One-shot results travel over `ResultEventBus` (`app:core:navigation`), supplied by `AppNavDisplay` through `LocalResultEventBus`, keyed by reified `typeOf<T>()` with the result type declared beside the producing `NavKey`. The sender's Root calls `sendResult` then navigates back; the receiver's `entry<>` block uses `ResultEffect<T>` to dispatch an existing Intent. Navigation 3 1.2's `androidx.navigation3.runtime.result` supersedes this hand-rolled bus once the KMP artifact is stable.
