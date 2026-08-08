#!/usr/bin/env bash
# Enforces the destination isolation rule (.claude/rules/ui-implementation.md, AGENTS.md):
# a destination must not reference anything under a sibling destination. Every symbol in a
# feature module is `internal`, so the Kotlin compiler accepts such a reference — this check is
# what actually catches it.
set -u

repo=$(git rev-parse --show-toplevel 2>/dev/null) || {
  echo "ERROR: not inside a git repository" >&2
  exit 1
}
cd "$repo" || exit 1

# Untracked files must be checked too — a brand-new destination is exactly when this matters.
# --exclude-standard keeps build outputs (gitignored) out.
cross=$(
  git ls-files --cached --others --exclude-standard -- 'app/*.kt' | while IFS= read -r file; do
    [ -f "$file" ] || continue
    owner=$(printf '%s\n' "$file" | sed -n 's|.*/destination/\([^/]*\)/.*|\1|p')
    [ -n "$owner" ] || continue
    grep -nE '^import [a-zA-Z0-9_.]*\.destination\.[a-zA-Z0-9_]+\.' "$file" |
      while IFS= read -r hit; do
        target=$(printf '%s\n' "$hit" | sed -n 's|.*\.destination\.\([a-zA-Z0-9_]*\)\..*|\1|p')
        [ "$target" = "$owner" ] && continue
        printf '  %s:%s\n      %s\n' "$file" "${hit%%:*}" "${hit#*:}"
      done
  done
)

if [ -n "$cross" ]; then
  echo "ERROR: destination isolation violated — a destination references a sibling destination:" >&2
  printf '%s\n' "$cross" >&2
  echo "  Promote the shared type (feature model/ or theme/, or app/core/designsystem), or give" >&2
  echo "  each destination its own. Never import across destinations." >&2
  exit 1
fi

echo "destination isolation check passed"
