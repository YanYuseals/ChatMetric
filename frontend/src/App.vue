<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">ChatMetric</div>
      <nav class="nav">
        <RouterLink class="nav-link" to="/">对话</RouterLink>
        <RouterLink class="nav-link" to="/admin">管理</RouterLink>
      </nav>
    </header>
    <main class="main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router";
</script>

