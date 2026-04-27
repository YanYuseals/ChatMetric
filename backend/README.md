# chatmetric-backend

## 配置

- MySQL：修改 `backend/src/main/resources/application.yml` 的 `spring.datasource.*`
- DashScope：设置环境变量 `DASHSCOPE_API_KEY`（或配置 `chatmetric.dashscope.api-key`）
- Milvus：修改 `chatmetric.milvus.*`

## 启动

在 `backend/` 下：

```bash
mvn spring-boot:run
```

说明：Spring Boot 3.x 需要 Java 17+。

## API

### 1) 同步指标元数据到 Milvus（任务 A）

`POST /api/admin/metrics/sync`

### 2) 查看指标元数据（管理页展示用）

`GET /api/admin/metrics/list`

### 3) 机构维智能查询（任务 B + D）

`POST /api/chat/org`

请求体：
```json
{ "question": "本机构存款余额是多少？", "dataDate": "2026-04-01" }
```

请求头（模拟登录）：
`X-User-Id: 1`

### 4) 客户维智能查询（任务 C + D）

`POST /api/chat/cust`

请求体：
```json
{ "question": "客户A的资产规模是多少？", "dt": "2026-04-01" }
```

