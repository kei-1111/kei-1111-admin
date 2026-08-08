---
paths:
  - "app/feature/**/*.kt"
  - "app/core/designsystem/**/*.kt"
---

# Preview Implementation Guide

## Rules

- Use the unified `androidx.compose.ui.tooling.preview.Preview` annotation (CMP 1.10+, usable directly in `commonMain`), always plain with **no parameters**.
- No shared preview infrastructure (`@ComponentPreviews` / `@PreviewWrapper`) — do not introduce it. Wrap content in `AdminTheme { ... }` by hand.
- The preview is a `private` function named `{ComponentName}Preview` at the bottom of the component's own file, with empty `{}` for callback parameters.
- A component whose layout needs bounded constraints gives its preview a fixed `Modifier.size(...)` box — Preview otherwise measures under infinite constraints.

## State for Screens/Content Previews

Screens and Content that require a `State` build it from sample data in `preview/XxxPreviewFixtures.kt` — **never** a live `ViewModel`.

## Rendering Requirements

Preview rendering relies on the non-shipped Android target from the `kei_1111.kmp.wasm` convention plugin (`android {}`, namespace auto-derived from the project path); the `compose.ui.tooling` dependency is wired by `kei_1111.cmp`. Its only other role is running the client unit tests as host tests (`.claude/rules/app-testing.md`). Do not remove that target. Compile-check a module's previews without opening the IDE:

```bash
./gradlew :app:feature:workbench:compileAndroidMain
```
