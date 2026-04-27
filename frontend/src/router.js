import { createRouter, createWebHistory } from "vue-router";
import ChatPage from "./pages/ChatPage.vue";
import AdminPage from "./pages/AdminPage.vue";

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: ChatPage },
    { path: "/admin", component: AdminPage }
  ]
});

