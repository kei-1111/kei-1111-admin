# AGENTS.md — shared/

Rules for `shared/model`; its `@Serializable` types form both the client/server JSON contract and
the shape of the content stored in the GCS bucket. The root `AGENTS.md` still applies.

- Compatibility requirements and the required validation are canonical in
  `.claude/rules/shared-model.md`; read it before changing an `@Serializable` model or its
  serializer.
- The module targets wasmJs and JVM — keep it free of platform APIs so both consumers compile.
