# ai-docs

AI-tooling assets for portfolio-admin, laid out like kei-1111.github.io.

- `agents/<group>/<name>/SKILL.md` — canonical agent procedures; `.claude/agents/*.md` are thin wrappers pointing here.
- Workflow skills are **not** duplicated in this repository: they live in kei-1111.github.io (`ai-docs/skills/`) until the planned extraction into a shared repository consumed by both products via submodule + symlinks. Until then, run workflow skills from a kei-1111.github.io session, or copy a skill here deliberately when it must diverge.
- Structure rule: content is canonical here; tool-specific directories hold only symlinks or thin wrappers, never diverging copies.
