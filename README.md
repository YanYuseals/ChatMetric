# ChatMetric

ChatMetric：面向业务指标的 AI 智能问数系统（语义检索 + SQL 查询 + 大模型润色输出）。

本仓库包含：
- `backend/`：Spring Boot + Spring AI（DashScope + Milvus）后端
- `frontend/`：Vue 3 前端（参考通义千问布局：会话列表 + 聊天窗口 + 管理页）
- `docs/`：数据库表结构与说明

## 快速开始（本地）

1) 初始化 MySQL（执行 `docs/schema.sql`）
2) 启动 Milvus（本地或容器均可）
3) 配置后端：
   - `backend/src/main/resources/application.yml`
   - 设置环境变量 `DASHSCOPE_API_KEY`
4) 启动后端：在 `backend/` 下运行 `mvn spring-boot:run`
5) 启动前端：在 `frontend/` 下运行 `npm install`、`npm run dev`

## 说明

- Spring Boot `3.x` 在 2026-04-27 仍要求 Java `17+`；如果你本机确实只有 Java 8，需要把后端降到 Spring Boot 2.x（同时 Spring AI 与 Milvus 集成方案也要随之调整）。
- 当前为“演示型鉴权”：机构维查询依赖请求头 `X-User-Id`，会从 `users` 表读取 `org_code`。
