<template>
  <el-card class="page-card">
    <div class="header">
      <div>
        <h2>欢迎回来</h2>
        <p>这里可以查看当前可学习的课程。</p>
      </div>
      <el-tag type="success" v-if="userStore.isAuthenticated">已登录</el-tag>
    </div>
    <el-divider />
    <div class="course-header">
      <div class="title-group">
        <h3>可学习的课程</h3>
        <p class="sub">只有审核通过的课程会展示在这里</p>
      </div>
      <el-button type="primary" @click="fetchCourses" :loading="loading">刷新列表</el-button>
    </div>
    <el-empty v-if="!loading && courses.length === 0" description="暂无可学习课程" />
    <el-skeleton v-if="loading" animated :rows="4" />
    <el-space v-else wrap :size="16" class="course-list">
      <el-card v-for="course in courses" :key="course.id" class="course-card" shadow="hover">
        <div class="course-title">
          <h4>{{ course.title }}</h4>
          <el-tag type="success">可学习</el-tag>
        </div>
        <p class="desc">{{ course.description }}</p>
        <p class="teacher">教师：{{ course.teacherUsername }}</p>
        <el-divider />
        <div class="chapters">
          <p class="chapters-title">章节</p>
          <ol>
            <li v-for="(chapter, idx) in course.chapters" :key="idx">
              <div class="chapter-line">
                <span class="chapter-name">{{ chapter.title }}</span>
                <span class="chapter-video">视频：{{ chapter.videoUrl }}</span>
              </div>
              <div class="chapter-question">题目：{{ chapter.question }}</div>
            </li>
          </ol>
        </div>
      </el-card>
    </el-space>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/axios'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const courses = ref([])
const loading = ref(false)

const fetchCourses = async () => {
  loading.value = true
  try {
    const { data } = await api.get('/courses/available')
    courses.value = data
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取课程失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCourses()
})
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

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title-group h3 {
  margin: 0;
}

.title-group .sub {
  margin: 4px 0 0;
  color: #888;
}

.course-list {
  width: 100%;
}

.course-card {
  width: 100%;
}

.course-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.desc {
  color: #666;
  margin: 8px 0;
}

.teacher {
  color: #444;
  margin-bottom: 8px;
}

.chapters-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.chapters ol {
  padding-left: 20px;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chapter-line {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.chapter-name {
  font-weight: 600;
}

.chapter-video,
.chapter-question {
  color: #666;
}
</style>
