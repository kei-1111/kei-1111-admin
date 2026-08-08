---
paths:
  - "app/feature/**/*.kt"
  - "app/core/designsystem/**/*.kt"
---

# UI Implementation Guide

## Screen Structure (MVI Layering)

Screens follow a ScreenRoot → Screen → Content → Component layering; raw `Intent`-dispatch access never goes below the Content layer. Small screens may collapse Screen/Content into one file until a form-factor split is needed (Home does today).

| Layer | Role | File |
|-------|------|------|
| ScreenRoot | Takes the `ViewModel`, collects `state` via `collectAsStateWithLifecycle()`, handles one-shot Effects via the `MviEffect` composable | `XxxScreenRoot.kt` |
| Screen | `internal` pure-UI layer; forwards `state` + `onIntent` down | `XxxScreen.kt` |
| Content | Layout per form factor when needed. Takes `state` and `onIntent` — no `ViewModel` reference | `content/*.kt` |
| Component | Pure UI rendering. Plain value + callback params — **never** an `Intent` | `component/*.kt` |

## `destination/<name>/` Directory Layout

Each screen lives under `destination/<name>/` in its feature module. The top level holds only the destination contract and orchestration files — `XxxScreenRoot.kt`, `XxxScreen.kt`, and the MVI files; everything else goes into purpose-named subpackages: `content/`, `model/`, `component/`, `preview/`, `theme/`. Route/entries files live under `navigation/`.

### Destination Isolation — MUST

Nothing under `destination/<a>/` may be referenced from `destination/<b>/`, components most of all. Two destinations needing the same UI element either each keep their own, or get a real shared component in `app:core:designsystem` — never an import across destinations. Only types and non-component helpers may be promoted out of a destination, and only when every consumer changes them for the same reason. Promoted types must not depend on `destination.*`; the sole exception is `navigation/`, which composes destinations by referencing their Roots and ViewModels.

## Component Responsibilities

- Pure view: render what it receives, notify events via callbacks. Components never call an Api/Repository — that boundary belongs to the ViewModel.
- Do not hold sync-relevant state internally (hoist it to `ViewModelState`/`State`), and do not fetch or decide how to obtain data.
- Single level of abstraction (SLA) at **every** container level, recursively: a container's direct children are either all leaf composables or all named components (`Spacer`/dividers exempt). Name components for their purpose, not what they display.
- Split component files by cohesion, never declaration count: one section file holds its whole SLA tree as `private` sub-components plus its `@Preview`. A separate file only for pieces genuinely shared across sections or an independently-evolving unit.
- Padding: the parent container sets internal padding to secure spacing — do not add padding to child components as if it were a margin.

## Theme

`AdminTheme` (`app:core:designsystem`) wraps Material3's `darkColorScheme()` for now — use `MaterialTheme.typography` / `colorScheme` tokens through it. If the admin UI grows a visual identity, grow the design system module instead of hardcoding colors in features.

## Compose Pitfalls (verified in kei-1111.github.io)

- Hit testing prunes a child's pointer regions outside the parent's bounds — an interactive child pushed outside its parent silently receives no pointer events there; reserve real layout width instead.
- Dialog content that fills the viewport leaves no "outside", so outside-click dismissal stops working — keep dismissable dialog content smaller than the viewport or handle dismissal explicitly.
- Deferred lambdas (`Modifier.offset { }` / `Modifier.layout { }`) run outside the composable body and bypass its early-return guards — re-guard state-dependent computations inside the lambda.

## Browser Smoke Test

After a user-visible UI change, verify runtime behavior in a browser — compilation proves nothing; never claim browser verification from compilation alone. Prefer a headless Playwright session against the dev server (`./gradlew :app:webApp:wasmJsBrowserDevelopmentRun` → http://localhost:8080). Two verified traps: editing sources mid-verification live-reloads the app (redo the checks after any edit), and a backgrounded tab throttles frames — re-screenshot before trusting a broken-looking state. Report which checks were performed and call out anything left unverified.
