#!/usr/bin/env bash
# PreToolUse(Bash) hook: block `git push` unless `./gradlew detekt` passes cleanly.
# Exit 0 allows the tool call; exit 2 blocks it and surfaces stderr to Claude.
set -u

payload=$(cat)

# Substring heuristic keeps the hook dependency-free (no jq); anything not pushing passes through.
case "$payload" in
  *"git push"*) ;;
  *) exit 0 ;;
esac

cd "${CLAUDE_PROJECT_DIR:?}" || exit 2

# Content-level snapshot: porcelain alone misses autoCorrect edits to an already-dirty file.
snapshot() { { git status --porcelain; git diff; } | shasum; }

before=$(snapshot)

./gradlew detekt --quiet
first_result=$?
after=$(snapshot)

if [ "$first_result" -eq 0 ] && [ "$before" = "$after" ]; then
  exit 0
fi

# A push sends commits, not the working tree — any autoCorrect reformat means the committed
# code still fails detekt on CI (autoCorrect is disabled there), so every remaining path blocks.
if [ "$first_result" -eq 0 ]; then
  echo "detekt reformatted files via autoCorrect. Review, commit the formatting fix, then push again." >&2
  exit 2
fi

if [ "$before" = "$after" ]; then
  echo "detekt failed with no auto-correctable changes. Fix the reported issues before pushing." >&2
  exit 2
fi

if ./gradlew detekt --quiet; then
  echo "detekt auto-corrected formatting (passes now), but the committed code still fails on CI. Commit the formatting fix, then push again." >&2
else
  echo "detekt still fails after the autoCorrect rerun. Fix the reported issues before pushing." >&2
fi
exit 2
