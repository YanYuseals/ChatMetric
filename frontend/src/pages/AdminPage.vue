<template>
  <div class="admin-wrap">
    <div class="card panel">
      <div class="row">
        <div>
          <div class="title">指标元数据管理</div>
          <div class="muted sub">同步会把口径说明向量化并写入 Milvus。</div>
        </div>
        <div class="actions">
          <button class="btn" :disabled="loading" @click="reload">刷新</button>
          <button class="btn primary" :disabled="loading" @click="sync">同步到 Milvus</button>
        </div>
      </div>
      <div v-if="err" class="err">{{ err }}</div>
      <div v-if="syncMsg" class="ok">{{ syncMsg }}</div>
    </div>

    <div class="grid">
      <div class="card panel">
        <div class="section-title">机构维指标（org_metric_desc）</div>
        <table class="table">
          <thead>
            <tr>
              <th>metric_id</th>
              <th>metric_name</th>
              <th>table_name</th>
              <th>metric_logic_desc</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in data.org" :key="m.metricId">
              <td>{{ m.metricId }}</td>
              <td>{{ m.metricName }}</td>
              <td>{{ m.tableName }}</td>
              <td class="desc">{{ m.metricLogicDesc }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card panel">
        <div class="section-title">客户维指标（cust_metric_desc）</div>
        <table class="table">
          <thead>
            <tr>
              <th>metric_id</th>
              <th>metric_name</th>
              <th>table_name</th>
              <th>column_name</th>
              <th>metric_logic_desc</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in data.cust" :key="m.metricId">
              <td>{{ m.metricId }}</td>
              <td>{{ m.metricName }}</td>
              <td>{{ m.tableName }}</td>
              <td>{{ m.columnName }}</td>
              <td class="desc">{{ m.metricLogicDesc }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { api } from "../api";

const data = reactive({ org: [], cust: [] });
const loading = ref(false);
const err = ref("");
const syncMsg = ref("");

async function reload() {
  loading.value = true;
  err.value = "";
  syncMsg.value = "";
  try {
    const res = await api.get("/api/admin/metrics/list");
    data.org = res.org || [];
    data.cust = res.cust || [];
  } catch (e) {
    err.value = e?.message || String(e);
  } finally {
    loading.value = false;
  }
}

async function sync() {
  loading.value = true;
  err.value = "";
  syncMsg.value = "";
  try {
    const res = await api.post("/api/admin/metrics/sync", {});
    syncMsg.value = `已同步：${res.upsertCount} 条向量 ✅`;
    await reload();
  } catch (e) {
    err.value = e?.message || String(e);
  } finally {
    loading.value = false;
  }
}

onMounted(reload);
</script>

<style scoped>
.admin-wrap { padding: 14px; display: flex; flex-direction: column; gap: 14px; }
.panel { padding: 14px; }
.row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.actions { display: flex; gap: 10px; }
.title { font-weight: 800; }
.sub { margin-top: 4px; }
.section-title { padding: 12px 12px 6px; font-weight: 700; }
.grid { display: grid; grid-template-columns: 1fr; gap: 14px; }
.desc { max-width: 620px; white-space: pre-wrap; }
.err { margin-top: 10px; color: #ffb4b4; }
.ok { margin-top: 10px; color: #b7ffd9; }
</style>

