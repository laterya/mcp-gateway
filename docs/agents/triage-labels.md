# Triage Labels

Standard five-label triage workflow for GitHub Issues.

## Labels

| Label | Description |
|-------|-------------|
| `needs-triage` | New issue, not yet evaluated |
| `needs-info` | More information required from reporter |
| `ready-for-agent` | Triaged and ready for an agent to pick up |
| `ready-for-human` | Requires human attention/review |
| `wontfix` | Evaluated and decided not to fix |

## State Machine

```
needs-triage ──→ needs-info ──→ needs-triage
     │                               
     ├──→ ready-for-agent ──→ done  
     │                               
     ├──→ ready-for-human ──→ done  
     │                               
     └──→ wontfix                    
```

## Agent Instructions

- New issues start as `needs-triage`
- `triage` skill reads current label and transitions based on evaluation
- After agent work completes, close the issue (remove all triage labels)
- If blocked waiting for human, set `needs-info` or `ready-for-human`
