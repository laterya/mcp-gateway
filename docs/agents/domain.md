# Domain Documentation Layout

This project uses a **single-context layout** for domain documentation.

## Structure

```
CONTEXT.md          ← Single source of truth for domain language and concepts
docs/adr/           ← Architecture Decision Records
```

## CONTEXT.md

The `CONTEXT.md` file serves as the project's domain glossary and context map. It should contain:

- **Ubiquitous Language**: Key domain terms and their definitions
- **Bounded Contexts**: Description of each bounded context and its responsibilities
- **Context Map**: Relationships between bounded contexts (partnership, customer-supplier, etc.)
- **Key Concepts**: Business rules, invariants, and domain constraints

## ADR (Architecture Decision Records)

Each ADR in `docs/adr/` documents a significant architectural decision:

- **Format**: Markdown files named `NNNN-title-with-dashes.md`
- **Template**: Title, Status, Context, Decision, Consequences
- **When to write**: For any decision that affects architecture, not for routine implementation choices

## Updating

Agent skills like `grill-with-docs` and `improve-codebase-architecture` will update these files as decisions crystallize during grilling sessions and architecture reviews.
