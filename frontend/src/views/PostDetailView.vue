<template>
  <div class="post-detail">
    <el-page-header @back="goBack" content="帖子详情" />
    <el-card v-if="loading" shadow="never">
      <el-skeleton :rows="5" animated />
    </el-card>
    <el-card v-else-if="post" shadow="never" class="post-card">
      <div class="post-header">
        <h2 class="title">{{ post.title }}</h2>
        <div class="meta">
          <span>作者：{{ post.authorUsername }}</span>
          <el-divider direction="vertical" />
          <span>{{ formatDate(post.createdAt) }}</span>
          <el-divider direction="vertical" />
          <span>点赞 {{ post.likeCount }}</span>
          <el-divider direction="vertical" />
          <span>评论 {{ post.commentCount }}</span>
        </div>
      </div>
      <div class="content" v-html="renderMarkdown(post.content)"></div>
      <el-divider />
      <div class="comments">
        <h4>评论</h4>
        <div v-if="post.comments?.length === 0" class="muted">暂无评论</div>
        <div v-else class="comment-list">
          <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
            <div class="comment-meta">
              <span class="author">{{ comment.authorUsername }}</span>
              <span class="time">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </el-card>
    <el-empty v-else description="未找到帖子" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import api from '../api/axios'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const loading = ref(true)

const goBack = () => {
  router.back()
}

const renderMarkdown = (markdownText) => {
  if (!markdownText) return ''
  const html = marked.parse(markdownText)
  return DOMPurify.sanitize(html)
}

const formatDate = (value) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

const fetchPost = async () => {
  loading.value = true
  try {
    const { data } = await api.get(`/community/posts/${route.params.id}`)
    post.value = data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取帖子失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPost()
})
</script>

<style scoped>
.post-detail {
  max-width: 900px;
  margin: 0 auto;
}

.post-card {
  margin-top: 12px;
  border-radius: 10px;
}

.post-header {
  margin-bottom: 12px;
}

.title {
  margin: 0 0 8px;
}

.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.content {
  line-height: 1.6;
}

.content :deep(pre) {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
  overflow: auto;
}

.content :deep(code) {
  background: #f0f0f0;
  padding: 2px 4px;
  border-radius: 4px;
}

.comments {
  margin-top: 12px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 8px;
}

.comment-item {
  padding: 10px;
  background: #f8f9fb;
  border-radius: 8px;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  color: #606266;
}

.comment-content {
  line-height: 1.5;
}

.muted {
  color: #909399;
}
</style>
