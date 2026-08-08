# Git Workflow

Same conventions as kei-1111.github.io, trimmed to what exists in this repository today.

## Commits

- [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/), written entirely in English: `<type>: <description>` or `<type>(scope): <description>`
- Types: feat, fix, docs, refactor, perf, test, build, ci, chore
- Scopes as they emerge: `app` (composeApp), `server`, `shared`, `deps`
- Description: imperative mood, one concise line, no trailing period
- Granularity: one self-contained logical change per commit

## Branches

- Name: `<type>/#<issue-number>` where type mirrors the Issue type: `feature` | `fix` | `refactor` | `docs` | `research` | `perf` | `test` | `ci` | `chore`
- Keep branches short-lived and synced with `main`

## Issues / Pull Requests

- Issue title `[<Type>]: <title>`; title and body in English; one responsibility per Issue
- PR title: the corresponding Issue title verbatim; base branch is always `main`
- PR body: `## Summary` / `## Related Issue` / `## Checklist`; keep PRs reviewable (up to ~500 lines)

## CI/CD

Not set up yet. Planned: build + test on PR; GitHub Pages deploy for `composeApp` and Cloud Run deploy for `server` on push to `main`, mirroring kei-1111.github.io's workflow structure. Update this section when workflows land.

## Prohibited

- Direct push to the `main` branch
- Force push on shared branches
- Massive file changes in a single PR
- Meaningless commit messages
