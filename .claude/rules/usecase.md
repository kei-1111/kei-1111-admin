---
paths:
  - "app/core/domain/**/*.kt"
---

# UseCase

`app/core/domain` is the boundary feature modules depend on; they never see a repository type.

- A UseCase is a single-method interface with `suspend operator fun invoke(...)`, named for the
  action (`GetWorksDraftUseCase`, `SaveProfileDraftUseCase`, `PublishContentUseCase`).
- Implementations are `internal class`, `@ContributesBinding(AppScope::class)` + `@Inject`,
  delegating to one repository method. A UseCase adds no error handling, caching, or wrapping —
  keep it a pass-through so the failure boundary stays in the ViewModel
  (`.claude/rules/error-handling.md`).
- Business rules that are genuinely domain logic (not screen state) belong here rather than in the
  ViewModel — but do not invent one speculatively; a pass-through UseCase exists to keep the
  dependency direction, and that is reason enough.
- Related UseCases live together in one file per server area (`ContentUseCases.kt`,
  `ImageUseCases.kt`) with interfaces and their impls adjacent — split the file when an area grows
  its own vocabulary, never by declaration count.
- A ViewModel that needs many of them injects them individually; that is expected for an editor
  screen and is not a reason to introduce a facade.

Tested with hand-written fake repositories per `.claude/rules/app-testing.md`.
