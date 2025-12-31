<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo">Online Learning Platform</div>
      <div class="nav-actions">
        <el-button v-if="isAuthenticated" type="primary" link @click="toHome">课程列表</el-button>
        <el-button v-if="isAuthenticated" type="primary" link @click="toProblems">编程题目</el-button>
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
import { computed } from 'vue'
import { useUserStore } from './stores/user'

const router = useRouter()
const userStore = useUserStore()

const isAuthenticated = computed(() => userStore.isAuthenticated)

const toLogin = () => router.push({ name: 'login' })
const toRegister = () => router.push({ name: 'register' })
const toHome = () => router.push({ name: 'home' })
const toProblems = () => router.push({ name: 'problems' })
const logout = () => {
  userStore.logout()
  router.push({ name: 'login' })
}
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

.logo {
  font-size: 18px;
  font-weight: 600;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  margin-right: 8px;
  color: #606266;
}

.app-main {
  padding: 32px;
}
</style>
