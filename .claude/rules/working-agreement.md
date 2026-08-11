# Working Agreement

Canonical working rules for every coding agent in this repository. Claude Code loads this rule in
every session; other agents reach it through `AGENTS.md`, which summarizes and points here. When
they drift, this file wins.

## Instruction Priority

When guidance conflicts, use this order:

1. The user's current request
2. The closest applicable agent guidance (this file and the other `.claude/rules/*.md`; for other agents, the closest `AGENTS.md`)
3. Current source code and build configuration
4. `docs/ArchitectureOverview.md` and `docs/ModuleOverview.md`

Treat source code as authoritative when generated documentation, examples, or copied patterns have
drifted. This source-over-document rule does not override applicable agent guidance in
`.claude/rules/*.md`. Preserve this project's established targets, navigation structure, previews,
and validation approach.

kei-1111.github.io is the reference codebase: when adding infrastructure that exists there (CI
workflows, convention plugins, data-layer conventions), mirror its approach and versions rather
than inventing a new one — and record any deliberate divergence in the rule file for that area.

## Before Editing

- Inspect the files being changed and their nearest analogous implementation.
- Check `git status`; preserve user changes and avoid unrelated cleanup.
- Verify referenced APIs, tasks, modules, and paths in the current checkout instead of relying on documentation alone.
- For a non-trivial change, define verifiable success criteria first — the narrowest validation that must pass and, for user-visible UI changes, what to confirm in the browser — and validate against them before reporting completion.

## While Editing

- Make the smallest coherent change that satisfies the request.
- Follow existing module boundaries and naming before introducing a new abstraction.
- Keep refactors separate from behavior changes unless the refactor is required.
- Do not edit generated files or build output.
- Keep documentation concise and proportional; prefer one clear instruction over repeated wording, exhaustive safeguards, or speculative edge cases.
- Volatile facts that would need synchronized edits — commands, flags, versions, thresholds, module/task enumerations — have one canonical source, preferably the code or configuration itself; other documents point to it and add only why or when it matters.
- Escalate when stuck: after a few failed attempts without a confirmed root cause, stop and consult the user instead of applying speculative fixes.

## Comments

- Comments are exceptional: the default for agent-written code is no comments at all.
- The only admissible comment states a constraint the code cannot express — a workaround pinned to an external bug, a non-obvious invariant — and must be individually justifiable. Anything else, including correct-but-derivable rationale, is deleted.
- A comment lives in the file it describes; never describe another file or module from elsewhere.
- A stale comment is deleted or trimmed to its non-derivable part, never rewritten into a cross-reference.

## Scope Of A Request

- Filing an issue or asking for an opinion is not a signal to start implementing. Consultative phrasing requests an opinion, not execution.
- Do not commit, push, create an Issue, or open a PR unless the user asks for that action.

## Build And Validation

Prefer the narrowest command that covers the change. Suggested validation by change type:

| Change | Minimum validation |
|---|---|
| Kotlin in one feature | `./gradlew :app:feature:<name>:compileKotlinWasmJs` |
| Unit-tested logic (Repositories, UseCases, shared helpers, the MVI base, feature ViewModels) | `./gradlew :<module>:testAndroidHostTest` (module list canonical in `.github/workflows/app-test.yml`) |
| `shared:model` models or serializers | `./gradlew :shared:model:jvmTest :shared:model:wasmJsTest :server:test` (both consuming targets plus the server side of the wire contract) |
| Compose UI or Preview | Feature wasm compile + `./gradlew :app:feature:<name>:compileAndroidMain` |
| Core module or cross-module API | Compile every directly affected consumer |
| Navigation, DI, Gradle, or app wiring | `./gradlew :app:webApp:wasmJsBrowserDistribution` |
| Server Kotlin | `./gradlew :server:test` (compiles and runs the server test suite) |
| Formatting or lint-sensitive Kotlin | `./gradlew detekt`; rerun if autoCorrect changed files |
| User-visible wasm UI | Production build and, when practical, the browser smoke test (`.claude/rules/ui-implementation.md`) |

- Use the fully qualified client development-server command from `.claude/rules/gradle.md`; an unqualified task can start a different module's server on the same port.
- Suite conventions live in `.claude/rules/server-testing.md` and `.claude/rules/app-testing.md` (ViewModel specifics in `.claude/rules/mvi-testing.md`). New logic on both the client and `:server` follows TDD per `.claude/rules/tdd.md`.
- Do not claim browser behavior was verified when only compilation or static analysis was run; the procedure is canonical in `.claude/rules/ui-implementation.md` — Browser Smoke Test.

## Before Handing Off

- Review the final diff for accidental or unrelated changes.
- Verify before asserting: check API existence and behavior against the resolved dependency version or official sources; confirm the running build actually contains the change before diagnosing from runtime observations; separate observation from speculation when reporting.
- The independent review lane maps to the `rules-reviewer` and `code-reviewer` agents run independently.
- Run the narrowest relevant validation, expanding to broader checks for cross-module or release-impacting changes.
- Report what changed, what was validated, and anything not validated.

## Safety And Maintenance

- Never expose secrets, credentials, tokens, or machine-specific configuration. GCS credentials come from the Cloud Run service account (ADC) — never commit key files.
- The Android target has two roles only — Preview rendering and client unit-test host runs: androidMain actuals may be no-op stubs, and must never add Android runtime features or network calls.
- Declare all dependencies in `gradle/libs.versions.toml` and reference them via the version catalog, including inside convention plugins (`libs.findLibrary(...)`); `implementation()` only, `api()` is prohibited. Do NOT use the deprecated `compose.dependencies.*` Gradle accessors.
- Prefer the existing `kei_1111.*` convention plugins over ad hoc Gradle configuration; canonical in `.claude/rules/gradle.md` — Convention Plugins.
- Do not add heavy dependencies without approval.
- Do not rewrite large areas, rename public APIs, or move code across modules unless the task requires it.
- Never discard or overwrite unrelated working-tree changes.
- Keep `AGENTS.md` and this file updated together when agent-level instructions change.
