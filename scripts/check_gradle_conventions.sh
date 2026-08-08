#!/usr/bin/env bash
# Enforces the mechanically checkable rules in .claude/rules/gradle.md, none of which the build
# itself rejects:
#   - `api()` is prohibited; every dependency is declared with `implementation()` so a module's
#     build file states exactly what it depends on.
#   - the deprecated `compose.dependencies.*` Gradle accessors are prohibited; artifacts are
#     referenced through the version catalog.
set -u

repo=$(git rev-parse --show-toplevel 2>/dev/null) || {
  echo "ERROR: not inside a git repository" >&2
  exit 1
}
cd "$repo" || exit 1

status=0

# Untracked build files count too — a new module is exactly when this matters.
build_files=$(git ls-files --cached --others --exclude-standard -- '*.gradle.kts' 'build-logic/*.kt')
[ -n "$build_files" ] || build_files=/dev/null

api_hits=$(printf '%s\n' "$build_files" | xargs grep -nE '(^|[^A-Za-z])api\(' 2>/dev/null)
if [ -n "$api_hits" ]; then
  echo "ERROR: api() is prohibited — declare dependencies with implementation():" >&2
  printf '%s\n' "$api_hits" >&2
  status=1
fi

accessor_hits=$(printf '%s\n' "$build_files" | xargs grep -nE 'compose\.dependencies\.' 2>/dev/null)
if [ -n "$accessor_hits" ]; then
  echo "ERROR: the deprecated compose.dependencies.* accessors are prohibited —" >&2
  echo "       reference the artifact through gradle/libs.versions.toml instead:" >&2
  printf '%s\n' "$accessor_hits" >&2
  status=1
fi

[ "$status" -eq 0 ] && echo "gradle conventions check passed"
exit "$status"
