# AgentScope 2.0 Java 自学项目

这是一个学习 [AgentScope 2.0](https://github.com/agentscope-ai/agentscope) Java 版的个人自学项目，从零开始体验"用 Java 写智能体"。

很多人觉得智能体开发是 Python 的天下，但我相信：**在智能体框架这件事上，Java 不会落后**。成熟的工程化能力、类型系统、并发生态、企业级基础设施，恰恰是 Agent 从 Demo 走向生产环境最需要的东西。这个项目就是我验证这个判断的过程。

## 环境要求

| 依赖 | 版本 |
|---|---|
| JDK | 17+ |
| Maven | 3.x |
| agentscope-harness | 2.0.4 |
| agentscope-extensions-model-dashscope | 2.0.1 |
| 模型 | 阿里云百炼 `qwen-plus`（OpenAI 兼容） |

运行前需要配置环境变量：

```bash
export DASHSCOPE_API_KEY=sk-xxxx   # Windows: set DASHSCOPE_API_KEY=sk-xxxx
```

## 模块一览

每个类都有独立的 `main` 方法，可单独运行（建议直接在 IDEA 中跑）：

| 类 | 学习点 |
|---|---|
| `QuickStart` | 最小可运行 Agent：`HarnessAgent.builder()`、`sysPrompt`、`call()`、`RuntimeContext` |
| `SteamAgent` | 流式输出：`streamEvents()` + `AgentEventType` 事件分发；自定义工具 `get_current_time` |
| `ToolAgent` | 工具进阶：`@Tool` / `@ToolParam`、计算器工具、自定义 POJO 上下文注入 |
| `StructuredAgent` | 结构化输出：record 定义 schema、`getStructuredData()`、DashScope 原生 `response_format` |

也可以用命令行运行单个模块：

```bash
mvn compile exec:java -Dexec.mainClass=org.agentscope.agentscope2.QuickStart
```

## 踩坑记录（学习笔记）

这些是动手过程中真实踩过的坑，也是这个项目最有价值的部分：

### 1. 不要复用 `HarnessAgent.Builder` 多次 `build()`

`build()` 内部会往共享的 inner builder 追加默认中间件（如 `AgentTraceMiddleware`）。复用同一个 Builder 第二次 `build()`，新 Agent 的 trace 中间件会注册两份，日志整体翻倍。正确做法：每次调用工厂方法新建 Builder。

### 2. 工具参数的两条通道，规则完全相反

- 标注 `@ToolParam` 的参数：进入 JSON Schema 暴露给模型，由模型填值。**不加注解的参数不会出现在 Schema 里**（`ToolSchemaGenerator` 只处理带注解的参数），模型调用时框架取不到值，工具必然报错。
- 不加注解的自定义 POJO/record 参数：由框架从 `RuntimeContext` 按类型注入（`ctx.put(UserContext.class, ...)`），**不暴露给模型**，适合传递用户身份等敏感信息。

一句话：模型填的必须加 `@ToolParam`，代码注入的必须不加。另外别忘了把 `ctx` 传进 `call(msg, ctx)`，否则注入的是 null。

### 3. `name` 与 `agentId` 的区别

`name` 是人类可读的显示名（日志前缀、sessionId 兜底）；`agentId` 是持久化命名空间 key（状态目录 `~/.agentscope/state/<agentId>/<userId>/`、workspace 路径）。不设置 `agentId` 时自动回退 `name`，单 Agent 场景同名即可。

### 4. 结构化输出需要显式开启原生模式

`model("dashscope:qwen-plus")` 字符串方式创建的模型默认关闭原生 `response_format`，框架会走降级路径（注入合成工具 `generate_response` 让模型调用）。模型没按约定调用该工具时，返回的 Msg 就没有 `_structured_output` 元数据，`getStructuredData()` 会抛异常。解决：显式构建 `DashScopeChatModel` 并设置 `nativeStructuredOutput(true)`。

### 5. 会话重置

- `agent.clearContext(ctx)`：清空对话上下文，但保留长期记忆
- 换 `sessionId`：开新会话，旧记录保留
- 删除 `.agentscope/` 与 `~/.agentscope/`：彻底清除持久化状态

## 运行时数据

`.agentscope/workspace/`（会话记录、记忆、Agent 生成的工作区文件）和 `~/.agentscope/state/`（按 agentId/userId 隔离的持久化状态）都是运行时自动生成的，不参与编译，建议加入 `.gitignore`。


这个项目还会继续更新。

