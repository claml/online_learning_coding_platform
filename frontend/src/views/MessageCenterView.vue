<template>
  <div class="message-center">
    <div class="header">
      <h2>消息中心</h2>
      <el-button type="primary" @click="refresh">刷新</el-button>
    </div>
    <el-card>
      <el-table :data="notifications" v-loading="loading" stripe>
        <el-table-column prop="type" label="类型" width="160">
          <template #default="scope">
            <el-tag :type="typeTag(scope.row.type)">{{ typeLabel(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="360" />
        <el-table-column prop="createdAt" label="时间" width="200">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="read" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.read ? 'info' : 'warning'">
              {{ scope.row.read ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button v-if="!scope.row.read" type="primary" link @click="markRead(scope.row.id)">标记已读</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { useNotificationStore } from '../stores/notifications'

const notificationStore = useNotificationStore()

const notifications = computed(() => notificationStore.items)
const loading = computed(() => notificationStore.loading)

const typeLabel = (type) => {
  switch (type) {
    case 'COURSE_REJECTED':
      return '课程被驳回'
    case 'BINDING_REJECTED':
      return '绑定被驳回'
    case 'POST_DELETED':
      return '帖子被删除'
    default:
      return type
  }
}

const typeTag = (type) => {
  switch (type) {
    case 'COURSE_REJECTED':
      return 'danger'
    case 'BINDING_REJECTED':
      return 'warning'
    case 'POST_DELETED':
      return 'info'
    default:
      return 'info'
  }
}

const formatTime = (time) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '')

const markRead = async (id) => {
  await notificationStore.markAsRead(id)
}

const refresh = async () => {
  await notificationStore.fetchNotifications()
}

onMounted(() => {
  refresh()
})
</script>

<style scoped>
.message-center {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
