# Issue 跟踪：GitHub

本仓库的 Issue 和 PRD 存放于 GitHub Issues（laterya/mcp-gateway）。所有操作通过 `gh` CLI 完成。

## 约定

- **创建 Issue**：`gh issue create --title "..." --body "..."`。多行正文使用 heredoc。
- **查看 Issue**：`gh issue view <number> --comments`，通过 `jq` 过滤评论和标签。
- **列出 Issue**：`gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`，配合 `--label` 和 `--state` 过滤器使用。
- **评论 Issue**：`gh issue comment <number> --body "..."`
- **添加/移除标签**：`gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **关闭 Issue**：`gh issue close <number> --comment "..."`

仓库从 `git remote -v` 自动推断 — 在仓库克隆目录内运行 `gh` 时自动生效。

## 当 skill 要求"发布到 issue tracker"时

创建一个 GitHub Issue。

## 当 skill 要求"获取相关 ticket"时

运行 `gh issue view <number> --comments`。
