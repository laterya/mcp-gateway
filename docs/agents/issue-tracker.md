# Issue Tracker Configuration

## Platform

- **Provider**: GitHub
- **Repository**: `laterya/mcp-gateway`
- **CLI**: `gh` (GitHub CLI)

## Workflow

- Create issues with `gh issue create`
- List issues with `gh issue list`
- View issue details with `gh issue view <number>`
- Issues use labels for triage state (see `docs/agents/triage-labels.md`)

## Agent Skills Integration

The following skills interact with the issue tracker:

| Skill | Action |
|-------|--------|
| `triage` | Labels and routes incoming issues |
| `to-issues` | Creates implementation tickets from plans |
| `to-prd` | Publishes PRD as a new issue |

## Labels

Standard triage labels are configured in the repo. See `docs/agents/triage-labels.md` for the full label workflow.
