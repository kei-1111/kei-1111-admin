---
paths:
  - "shared/model/**/*.kt"
---

# Shared Wire Contract

`shared/model` is compiled for both wasmJs (the admin client) and JVM (`:server`), and its
`@Serializable` types are the JSON contract between them. The client is bundled into the server's
fat jar, so both usually deploy together — but the same models also describe the JSON written into
the GCS bucket, which **outlives any deployment** and is read by the portfolio site's own server.
Treat every model as long-lived stored data, not just a request body.

- Add a field only with a default value, so previously stored JSON still decodes.
- Treat a serialized-name change, a field or enum-constant deletion, and a field-type change as
  breaking for already-stored content; do not make one without a migration plan for the bucket. A
  Kotlin-only rename is safe while its `@SerialName` stays fixed.
- An enum addition is safe for the writer but not for older readers. Fields whose values may grow
  go through a tolerant list serializer (`TolerantEnumListSerializers`), which drops unknown
  entries instead of failing the whole document; keep that unknown-value behavior covered by tests
  when changing such a field.
- `ImmutableListSerializer` exists so models can expose immutable collections without changing the
  wire shape — it must stay a transparent list on the wire.

Minimum validation for a model or serializer change is canonical in
`.claude/rules/working-agreement.md` — Build And Validation.
