<template>
  <el-card class="page-card">
    <div class="header">
      <div>
        <h2>欢迎回来</h2>
        <p>这是一个受保护的页面，仅在登录后可访问。</p>
      </div>
      <el-tag type="success" v-if="userStore.isAuthenticated">已登录</el-tag>
    </div>
    <el-divider />
    <div class="content">
      <p>可以在这里放置课程列表、学习进度等内容。</p>
      <el-button type="primary" @click="refreshProfile">刷新用户信息</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import api from '../api/axios'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

const refreshProfile = async () => {
  try {
    // 示例：请求受保护的接口
    await api.get('/me')
    ElMessage.success('已尝试请求受保护接口（示例）。')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '请求失败')
  }
}
</script>

<style scoped>
.page-card {
  max-width: 960px;
  margin: 0 auto;
  padding: 12px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.content {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
