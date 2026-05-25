# Issue Tracker: GitHub

本仓库的 Issue 和 PRD 都存放在 GitHub Issues 中。所有操作通过 `gh` CLI 进行。

## 约定

- **创建 Issue**：`gh issue create --title "..." --body "..."`。多行正文使用 heredoc。
- **查看 Issue**：`gh issue view <number> --comments`，通过 `jq` 过滤评论并获取标签。
- **列出 Issue**：`gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'`，可按需添加 `--label` 和 `--state` 过滤。
- **评论 Issue**：`gh issue comment <number> --body "..."`
- **添加/移除标签**：`gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **关闭 Issue**：`gh issue close <number> --comment "..."`

仓库信息通过 `git remote -v` 推断 — `gh` 在 clone 目录内会自动完成此操作。

## 当技能提示"发布到 Issue 跟踪器"

创建 GitHub Issue。

## 当技能提示"获取相关工单"

运行 `gh issue view <number> --comments`。
