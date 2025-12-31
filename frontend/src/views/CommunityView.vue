<template>
  <div class="community-page">
    <el-card class="composer-card" shadow="hover">
      <div class="composer-header">
        <div>
          <h2>社区发帖</h2>
          <p class="muted">支持 Markdown，发布你的学习笔记或提问</p>
        </div>
        <el-select v-model="sort" size="large" style="width: 200px" @change="fetchPosts">
          <el-option label="最新发布" value="LATEST" />
          <el-option label="最热（点赞）" value="HOTTEST" />
          <el-option label="最多评论" value="MOST_COMMENTED" />
        </el-select>
      </div>
      <el-form :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="正文 (Markdown)">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="使用 Markdown 书写内容"
          />
        </el-form-item>
        <div class="composer-actions">
          <el-button type="primary" :loading="publishing" @click="handlePublish">发布</el-button>
          <el-button @click="resetForm">清空</el-button>
        </div>
        <div class="preview" v-if="form.content">
          <p class="muted">实时预览</p>
          <div class="preview-box" v-html="renderMarkdown(form.content)"></div>
        </div>
      </el-form>
    </el-card>

    <el-card class="posts-card" shadow="never">
      <div class="posts-header">
        <h3>社区动态</h3>
        <el-button link type="primary" @click="fetchPosts" :loading="loading">刷新</el-button>
      </div>
      <el-empty v-if="!loading && posts.length === 0" description="暂无帖子" />
      <el-skeleton v-else-if="loading" animated :rows="4" />
      <div v-else class="posts-list">
        <el-card v-for="post in posts" :key="post.id" class="post-card" shadow="hover">
          <div class="post-header">
            <div>
              <div class="post-title">{{ post.title }}</div>
              <div class="post-meta">
                <span>作者：{{ post.authorUsername }}</span>
                <el-divider direction="vertical" />
                <span>{{ formatDate(post.createdAt) }}</span>
              </div>
            </div>
            <div class="post-actions">
              <el-button
                type="primary"
                plain
                size="small"
                @click="toggleLike(post)"
              >
                点赞 {{ post.likeCount }}
              </el-button>
              <el-button
                v-if="isAdmin"
                type="danger"
                plain
                size="small"
                @click="confirmDelete(post)"
              >
                删除
              </el-button>
            </div>
          </div>
          <div class="post-content" v-html="renderMarkdown(post.content)"></div>
          <el-alert
            v-if="post.deleted"
            type="warning"
            show-icon
            :closable="false"
            title="该帖子已被管理员删除"
            :description="post.deletedReason"
            class="delete-alert"
          />
          <div class="post-footer">
            <span>评论：{{ post.commentCount }}</span>
            <el-button link type="primary" size="small" @click="toggleComments(post.id)">
              {{ commentPanels[post.id]?.visible ? '收起评论' : '查看评论' }}
            </el-button>
          </div>
          <div v-if="commentPanels[post.id]?.visible" class="comments-section">
            <el-skeleton v-if="commentPanels[post.id]?.loading" animated :rows="2" />
            <div v-else>
              <div v-if="(comments[post.id] || []).length === 0" class="muted">暂无评论</div>
              <div v-else class="comment-list">
                <div v-for="comment in comments[post.id]" :key="comment.id" class="comment-item">
                  <div class="comment-meta">
                    <span class="comment-author">{{ comment.authorUsername }}</span>
                    <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                </div>
              </div>
              <div class="comment-form">
                <el-input
                  v-model="commentInputs[post.id]"
                  type="textarea"
                  :rows="2"
                  placeholder="发表你的看法"
                />
                <el-button type="primary" size="small" @click="submitComment(post.id)">评论</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import api from '../api/axios'
import { useUserStore } from '../stores/user'

const posts = ref([])
const loading = ref(false)
const publishing = ref(false)
const comments = reactive({})
const commentInputs = reactive({})
const commentPanels = reactive({})
const sort = ref('LATEST')

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const form = reactive({
  title: '',
  content: ''
})

const renderMarkdown = (markdownText) => {
  if (!markdownText) return ''
  const html = marked.parse(markdownText)
  return DOMPurify.sanitize(html)
}

const resetForm = () => {
  form.title = ''
  form.content = ''
}

const handlePublish = async () => {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  publishing.value = true
  try {
    const { data } = await api.post('/community/posts', form)
    posts.value = [data, ...posts.value]
    resetForm()
    ElMessage.success('发布成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发布失败')
  } finally {
    publishing.value = false
  }
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/community/posts', { params: { sort: sort.value } })
    posts.value = data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取帖子失败')
  } finally {
    loading.value = false
  }
}

const toggleLike = async (post) => {
  try {
    const { data } = await api.post(`/community/posts/${post.id}/like`)
    const idx = posts.value.findIndex((p) => p.id === post.id)
    if (idx !== -1) {
      posts.value[idx] = { ...posts.value[idx], ...data }
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '点赞失败')
  }
}

const toggleComments = async (postId) => {
  const panel = commentPanels[postId] || { visible: false, loading: false }
  panel.visible = !panel.visible
  commentPanels[postId] = panel
  if (panel.visible && !comments[postId]) {
    await loadComments(postId)
  }
}

const loadComments = async (postId) => {
  commentPanels[postId] = { ...(commentPanels[postId] || {}), loading: true, visible: true }
  try {
    const { data } = await api.get(`/community/posts/${postId}/comments`)
    comments[postId] = data
    commentInputs[postId] = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取评论失败')
  } finally {
    commentPanels[postId].loading = false
  }
}

const submitComment = async (postId) => {
  const content = commentInputs[postId]?.trim()
  if (!content) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    const { data } = await api.post(`/community/posts/${postId}/comments`, { content })
    comments[postId] = [...(comments[postId] || []), data]
    const idx = posts.value.findIndex((p) => p.id === postId)
    if (idx !== -1) {
      posts.value[idx] = { ...posts.value[idx], commentCount: posts.value[idx].commentCount + 1 }
    }
    commentInputs[postId] = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '评论失败')
  }
}

const confirmDelete = (post) => {
  ElMessageBox.prompt('请输入删除原因，该原因将展示给所有用户', '删除帖子', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    inputPlaceholder: '违规原因',
    inputValidator: (value) => !!value?.trim() || '请填写原因'
  })
    .then(async ({ value }) => {
      try {
        await api.delete(`/community/posts/${post.id}`, { data: { reason: value } })
        posts.value = posts.value.filter((p) => p.id !== post.id)
        ElMessage.success('删除成功')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '删除失败')
      }
    })
    .catch(() => {})
}

const formatDate = (value) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.community-page {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.composer-card {
  border-radius: 10px;
}

.composer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.muted {
  color: #909399;
  margin: 4px 0;
}

.composer-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.preview {
  margin-top: 12px;
}

.preview-box {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.posts-card {
  border-radius: 10px;
}

.posts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-card {
  border-radius: 8px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  margin-top: 4px;
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-content {
  margin: 12px 0;
  line-height: 1.6;
}

.post-content :deep(pre) {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
  overflow: auto;
}

.post-content :deep(code) {
  background: #f0f0f0;
  padding: 2px 4px;
  border-radius: 4px;
}

.post-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comments-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 8px;
}

.comment-item {
  padding: 8px 10px;
  background: #f8f9fb;
  border-radius: 6px;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  color: #909399;
  margin-bottom: 4px;
}

.comment-author {
  font-weight: 600;
}

.comment-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.delete-alert {
  margin: 8px 0;
}
</style>
