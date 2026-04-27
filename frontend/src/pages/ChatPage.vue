<template>
  <div class="chat-layout">
    <aside class="sidebar card">
      <div class="side-top">
        <button class="btn primary" @click="newSession">+ 新会话</button>
        <div class="side-field">
          <div class="side-label">模拟登录</div>
          <input class="input" v-model="userId" placeholder="X-User-Id（例如 1）" />
        </div>
      </div>

      <div class="sessions">
        <div
          v-for="s in sessions"
          :key="s.id"
          class="session"
          :class="{ active: s.id === activeId }"
          @click="activeId = s.id"
        >
          <div class="session-title">{{ s.title || "未命名" }}</div>
          <div class="session-sub muted">{{ s.messages?.length || 0 }} 条消息</div>
        </div>
      </div>
    </aside>

    <section class="chat card">
      <header class="chat-top">
        <div class="chat-title">{{ activeSession?.title || "ChatMetric" }}</div>
        <div class="chat-controls">
          <select class="select" v-model="mode">
            <option value="org">机构维</option>
            <option value="cust">客户维</option>
          </select>
          <input class="input" type="date" v-model="dateStr" />
        </div>
      </header>

      <div class="chat-body" ref="bodyRef">
        <div v-if="!activeSession" class="empty muted">点击左侧“新会话”开始。</div>
        <template v-else>
          <div v-for="(m, idx) in activeSession.messages" :key="idx" class="msg" :class="m.role">
            <div class="bubble">
              <div v-if="m.role === 'assistant'" class="md" v-html="renderMarkdown(m.content)"></div>
              <div v-else>{{ m.content }}</div>
              <div v-if="m.sql" class="sql">
                <div class="muted">SQL</div>
                <pre><code>{{ m.sql }}</code></pre>
              </div>
            </div>
          </div>
          <div v-if="sending" class="msg assistant">
            <div class="bubble muted">正在思考中…</div>
          </div>
        </template>
      </div>

      <footer class="chat-input">
        <input
          class="input"
          v-model="text"
          placeholder="请输入你的问题，例如：2026-04-01 本机构的存款余额是多少？"
          @keydown.enter.exact.prevent="send"
        />
        <button class="btn primary" :disabled="sending || !text.trim() || !activeSession" @click="send">发送</button>
      </footer>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref, watch } from "vue";
import MarkdownIt from "markdown-it";
import { api } from "../api";

const md = new MarkdownIt({ linkify: true, breaks: true });
const bodyRef = ref(null);

const userId = ref(localStorage.getItem("cm_userId") || "1");
watch(userId, (v) => localStorage.setItem("cm_userId", v || ""));

const mode = ref("org");
const dateStr = ref(new Date().toISOString().slice(0, 10));
const text = ref("");
const sending = ref(false);

const sessions = ref(loadSessions());
const activeId = ref(sessions.value[0]?.id || "");

const activeSession = computed(() => sessions.value.find((s) => s.id === activeId.value));

function loadSessions() {
  try {
    const raw = localStorage.getItem("cm_sessions");
    const list = raw ? JSON.parse(raw) : [];
    return Array.isArray(list) ? list : [];
  } catch {
    return [];
  }
}

function persist() {
  localStorage.setItem("cm_sessions", JSON.stringify(sessions.value));
}

function newSession() {
  const id = crypto.randomUUID();
  sessions.value.unshift({ id, title: "新会话", messages: [] });
  activeId.value = id;
  persist();
}

function renderMarkdown(src) {
  return md.render(src || "");
}

async function send() {
  if (!activeSession.value) return;
  const q = text.value.trim();
  if (!q) return;
  text.value = "";
  const s = activeSession.value;
  s.messages.push({ role: "user", content: q });
  s.title = s.title === "新会话" ? q.slice(0, 16) : s.title;
  persist();

  sending.value = true;
  try {
    const date = dateStr.value;
    const uid = userId.value?.trim();
    const url = mode.value === "org" ? "/api/chat/org" : "/api/chat/cust";
    const payload =
      mode.value === "org" ? { question: q, dataDate: date } : { question: q, dt: date };
    const res = await api.post(url, payload, { userId: uid || undefined });
    s.messages.push({ role: "assistant", content: res.answer, sql: res.sql });
  } catch (e) {
    s.messages.push({ role: "assistant", content: `抱歉，出错了：${e?.message || e}` });
  } finally {
    sending.value = false;
    persist();
    await nextTick();
    bodyRef.value?.scrollTo?.({ top: bodyRef.value.scrollHeight, behavior: "smooth" });
  }
}

onMounted(() => {
  if (!sessions.value.length) newSession();
});
</script>

<style scoped>
.chat-layout {
  height: 100%;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 14px;
  padding: 14px;
}
.sidebar {
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}
.side-top { padding: 12px; border-bottom: 1px solid var(--border); }
.side-field { margin-top: 10px; }
.side-label { font-size: 12px; color: var(--muted); margin-bottom: 6px; }
.sessions { padding: 8px; overflow: auto; }
.session {
  padding: 10px;
  border: 1px solid transparent;
  border-radius: 12px;
  cursor: pointer;
}
.session:hover { background: rgba(255,255,255,0.06); }
.session.active { border-color: rgba(122,162,255,0.35); background: rgba(122,162,255,0.10); }
.session-title { font-weight: 600; }
.session-sub { font-size: 12px; margin-top: 4px; }

.chat {
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.chat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-bottom: 1px solid var(--border);
}
.chat-title { font-weight: 700; }
.chat-controls { display: grid; grid-template-columns: 110px 160px; gap: 10px; }

.chat-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 16px 14px;
}
.empty { padding: 18px; }
.msg { display: flex; margin: 10px 0; }
.msg.user { justify-content: flex-end; }
.bubble {
  max-width: 780px;
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 10px 12px;
  background: rgba(0,0,0,0.20);
}
.msg.user .bubble {
  background: rgba(25,195,125,0.14);
  border-color: rgba(25,195,125,0.25);
}
.md :deep(p) { margin: 0 0 10px; }
.md :deep(p:last-child) { margin-bottom: 0; }
.md :deep(a) { color: var(--brand); }
.sql { margin-top: 10px; }

.chat-input {
  display: grid;
  grid-template-columns: 1fr 90px;
  gap: 10px;
  padding: 12px 14px;
  border-top: 1px solid var(--border);
}
</style>

