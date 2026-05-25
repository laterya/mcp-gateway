# 领域文档

工程技能在探索代码库时如何消费本仓库的领域文档。

## 探索前，先读取以下内容

- **`CONTEXT.md`**（位于仓库根目录），如果不存在则检查 `CONTEXT-MAP.md`
- **`CONTEXT-MAP.md`**（位于仓库根目录）— 它指向每个上下文各自的 `CONTEXT.md`。逐一阅读与当前主题相关的每个文件。
- **`docs/adr/`** — 阅读与你要处理的领域相关的 ADR。在多上下文仓库中，还需检查 `src/<context>/docs/adr/` 中的上下文级决策。

如果这些文件都不存在，**静默继续**。不要标记它们不存在，也不要建议提前创建。生产者技能（`/grill-with-docs`）会在术语或决策实际确定后，按需懒加载创建它们。

## 文件结构

单上下文仓库（大多数仓库）：

```
/
├── CONTEXT.md
├── docs/adr/
│   ├── 0001-event-sourced-orders.md
│   └── 0002-postgres-for-write-model.md
└── src/
```

多上下文仓库（根目录存在 `CONTEXT-MAP.md`）：

```
/
├── CONTEXT-MAP.md
├── docs/adr/                          ← 系统级决策
└── src/
    ├── ordering/
    │   ├── CONTEXT.md
    │   └── docs/adr/                  ← 上下文级决策
    └── billing/
        ├── CONTEXT.md
        └── docs/adr/
```

## 使用术语表中的词汇

当你的输出中提到某个领域概念时（Issue 标题、重构建议、假设、测试名称等），请使用 `CONTEXT.md` 中定义的术语。不要偏离到术语表明确回避的同义词。

如果你需要的概念尚未出现在术语表中，这是一个信号 — 要么你在编造项目未使用的语言（重新考虑），要么确实存在空白（记下来留给 `/grill-with-docs`）。

## 标记 ADR 冲突

如果你的输出与现有 ADR 相矛盾，请明确指出来而不是默默覆盖：

> _与 ADR-0007（事件溯源订单）矛盾 — 但值得重新讨论，因为……_
