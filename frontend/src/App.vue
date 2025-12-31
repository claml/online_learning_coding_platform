<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-left">
        <div class="logo">Online Learning Platform</div>
        <div class="primary-nav" v-if="isAuthenticated">
          <el-button type="primary" link @click="toHome">课程列表</el-button>
          <el-button type="primary" link @click="toProblems">编程题目</el-button>
          <el-button type="primary" link @click="toCommunity">社区</el-button>
        </div>
      </div>
      <div class="nav-actions">
        <el-badge v-if="isAuthenticated" :value="unreadCount" :hidden="!unreadCount" class="message-entry">
          <el-button type="primary" link @click="toMessages">
            <el-icon><Bell /></el-icon>
            消息
          </el-button>
        </el-badge>
        <el-button v-if="!isAuthenticated" type="primary" link @click="toLogin">登录</el-button>
        <el-button v-if="!isAuthenticated" type="primary" link @click="toRegister">注册</el-button>
        <template v-else>
          <span class="welcome">欢迎，{{ userStore.user?.username || '用户' }}</span>
          <el-button type="primary" link @click="logout">退出</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed, watch, onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { useUserStore } from './stores/user'
import { useNotificationStore } from './stores/notifications'

const router = useRouter()
const userStore = useUserStore()
const notificationStore = useNotificationStore()

const isAuthenticated = computed(() => userStore.isAuthenticated)
const unreadCount = computed(() => notificationStore.unreadCount)

const toLogin = () => router.push({ name: 'login' })
const toRegister = () => router.push({ name: 'register' })
const toHome = () => router.push({ name: 'home' })
const toProblems = () => router.push({ name: 'problems' })
const toCommunity = () => router.push({ name: 'community' })
const toMessages = () => router.push({ name: 'messages' })
const logout = () => {
  userStore.logout()
  notificationStore.reset()
  router.push({ name: 'login' })
}

const loadUnread = () => {
  if (isAuthenticated.value) {
    notificationStore.fetchUnreadCount()
  }
}

watch(isAuthenticated, (authed) => {
  if (authed) {
    loadUnread()
  } else {
    notificationStore.reset()
  }
})

onMounted(() => {
  loadUnread()
})
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo {
  font-size: 18px;
  font-weight: 600;
}

.primary-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-entry :deep(.el-badge__content.is-fixed) {
  right: 2px;
}

.welcome {
  margin-right: 8px;
  color: #606266;
}

.app-main {
  padding: 32px;
}
</style>
