---
paths:
  - "app/feature/**/*.kt"
  - "app/core/designsystem/**/*.kt"
---

# Naming Conventions

## Intent / Effect

Name based on **intent (what to do)**, not on operation (what was clicked). Operation-based names such as `OnSaveButtonClick` are prohibited.

| Category | Pattern | Example |
|----------|---------|---------|
| State update | `Update{Target}` | `UpdateMemo(memo)` |
| Toggle | `Toggle{Target}` | `ToggleTheme` |
| Result reception | `Receive{Target}` | `ReceiveUploadFinished(result)` |
| Visibility notification | `Update{Target}Visibility` | `UpdatePageVisibility(isVisible)` |
| Open (Intent and matching Effect) | `Open{Target}` | `OpenUrl(url)` |
| Navigation (Effect only) | `Navigate{Destination}` | `NavigateHome` |
| Consume (fixed) | `ConsumeEffect` | every effect-bearing `XxxIntent` ends with `data object ConsumeEffect` |

`Receive{Target}` is only for results the UI did not directly request. A result-driven Intent whose requested action is clear keeps its action-based name.

## Composable

- Feature components (`destination/<name>/component/`) are purpose-named with no prefix.
- Shared components and infrastructure in `app/core/designsystem` use the `Kei` prefix (`KeiTheme`, `KeiIcons`, `KeiLanguageController`).
- Callbacks: `on + Action + Target` — `Click` for taps (`onClickSave: () -> Unit`), `Change` for value changes (`onChangeMemo: (String) -> Unit`). Exception: form components that wrap a Material3 input keep the wrapped API's idiomatic name (`onValueChange`, `onCheckedChange`, `onStatusChange`) so their surface mirrors what they wrap.
- Below the Content layer, components receive plain values and callbacks — **never** an `Intent` (see `.claude/rules/ui-implementation.md`).

## testTag

No E2E suite exists yet. When it lands, mirror kei-1111.github.io: kebab-case `feature-component-role[-key]` ids defined once in a shared tags module, referenced by both `Modifier.testTag(...)` and the Playwright locator — never inline literals on either side.

## Packages

| Module kind | Pattern | Real example |
|-------------|---------|---------------|
| `app/feature/<name>` screen | `io.github.kei_1111.admin.app.feature.<name>.destination.<name>...` | `io.github.kei_1111.admin.app.feature.home.destination.home` |
| `app/core/<module>` | `io.github.kei_1111.admin.app.core.<module>...` | `io.github.kei_1111.admin.app.core.mvi` |
| `shared/model` | `io.github.kei_1111.admin.shared.model...` | `io.github.kei_1111.admin.shared.model` |
| `server` | `io.github.kei_1111.admin.server.<layer>...` | `io.github.kei_1111.admin.server.routing` |

`destination/<name>/` directory names are lowercase single words, matching the screen name.

## Text Content

Admin UI text is Japanese — the console has a single operator, so there is no localization layer and no string resources: literals sit inline in the composable that shows them. Identifiers, comments in code shared with the reference repository's conventions, and all GitHub-authored text stay English (`.claude/rules/git-workflow.md`). Introduce a localization mechanism only if a second operator appears.
