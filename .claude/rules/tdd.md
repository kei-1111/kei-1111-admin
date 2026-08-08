---
paths:
  - "app/feature/**/*ViewModel.kt"
  - "app/**/src/commonTest/**"
  - "server/src/**/*.kt"
---

# TDD Process

New logic on both the client and `:server` is developed test-first; the rule is layer-agnostic. In a layer whose suite does not exist yet, the new logic introduces the `commonTest` coverage itself, and its conventions land in `app-testing.md` with that first test. Suite conventions: `app-testing.md` (client) / `server-testing.md` (server).

## The Cycle ([Canon TDD](https://newsletter.kentbeck.com/p/canon-tdd))

1. Write a test list: the expected behaviors (including edge cases), implementing none of them. Behaviors discovered while working go onto the list, not straight into code.
2. Turn exactly ONE list item into a test and run it — write only enough test code to produce the next failure (a compile failure counts). Observe the red and confirm it fails for the intended behavioral reason before writing any production code.
3. Write the minimum production code that makes it (and all previous tests) pass.
4. Refactor, keeping everything green. Repeat from step 2 until the list is empty.

- Do not convert the whole list into test code up front.
- Tests written after the implementation to confirm it are not TDD.
- Do not retroactively backfill tests for pre-existing code as a side effect of an unrelated change.

## Process Anti-Patterns (Prohibited)

Test-after masquerading as TDD; tautological tests (expected value derived with the same logic as the implementation). Suite-level anti-patterns: `app-testing.md` / `server-testing.md`.
