# chatmetric-frontend

## 安装与启动

在 `frontend/` 下：

```bash
npm install
npm run dev
```

默认会将 `/api` 代理到 `http://127.0.0.1:8080`（见 `frontend/vite.config.js`）。

## 页面

- `/`：对话页（左侧会话列表 + 右侧聊天窗口，支持 Markdown）
- `/admin`：管理页（同步指标向量、查看元数据）

