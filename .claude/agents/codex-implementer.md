---
name: codex-implementer
description: Delegates a planned code change to GPT-5.6 Sol via the official OpenAI Codex CLI and reports the resulting working-tree diff. Use when an implementation plan is settled and the edit can be offloaded to Codex (the default lane in CLAUDE.md model routing), or when a skill step delegates implementation to Codex. Provide the path to a self-contained implementation brief file (goal, target files, approach, constraints), the narrowest Gradle compile task when one applies, and the Codex session id for delta rounds. Not for planning or reviewing; use implementer instead when the change should stay on Claude.
model: sonnet
tools: Bash, Read, Grep, Glob
---

Read `ai-docs/agents/cross-agent/codex-implementer/SKILL.md` and follow it as your contract.
