import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'
import api from '../api/axios'

export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    items: [],
    unreadCount: 0,
    loading: false
  }),
  actions: {
    async fetchNotifications() {
      this.loading = true
      try {
        const { data } = await api.get('/messages')
        this.items = data || []
        this.unreadCount = this.items.filter((item) => !item.read).length
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '加载消息失败')
      } finally {
        this.loading = false
      }
    },
    async fetchUnreadCount() {
      try {
        const { data } = await api.get('/messages/unread-count')
        this.unreadCount = data?.count ?? 0
      } catch (error) {
        this.unreadCount = 0
      }
    },
    async markAsRead(id) {
      try {
        await api.put(`/messages/${id}/read`)
        this.items = this.items.map((item) => (item.id === id ? { ...item, read: true } : item))
        this.unreadCount = Math.max(0, this.unreadCount - 1)
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '标记已读失败')
      }
    },
    reset() {
      this.items = []
      this.unreadCount = 0
      this.loading = false
    }
  }
})
