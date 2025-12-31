<template>
  <div class="profile-page">
    <el-row :gutter="16">
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="section-card">
          <div class="card-header">
            <div>
              <h3>账号信息</h3>
              <p class="muted">更新你的昵称和头像，查看绑定状态</p>
            </div>
            <el-tag v-if="profile" type="info">{{ roleLabel }}</el-tag>
          </div>
          <el-form :model="profileForm" label-width="100px" class="info-form">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="头像">
              <div class="avatar-field">
                <el-input v-model="profileForm.avatarUrl" placeholder="粘贴图片 URL" />
                <img v-if="profileForm.avatarUrl" :src="profileForm.avatarUrl" alt="avatar" class="avatar-preview" />
              </div>
            </el-form-item>
            <el-form-item label="身份">
              <el-tag type="success">{{ roleLabel }}</el-tag>
            </el-form-item>
            <el-form-item :label="bindingLabel">
              <div class="binding-block">
                <div class="binding-status">
                  <span class="muted">当前状态：</span>
                  <el-tag :type="bindingTagType">{{ bindingStatusText }}</el-tag>
                </div>
                <template v-if="!profile?.boundStatus">
                  <el-input
                    v-model="bindingInput"
                    :placeholder="`请输入${bindingLabel}`"
                    :disabled="bindingLoading || isPending"
                    class="binding-input"
                  />
                  <el-button
                    type="primary"
                    :loading="bindingLoading"
                    :disabled="!bindingInput || isPending"
                    @click="submitBinding"
                  >
                    提交绑定
                  </el-button>
                  <p class="muted" v-if="isPending">已提交申请，审核中</p>
                </template>
                <template v-else>
                  <p class="muted">已绑定：{{ boundIdentifier }}</p>
                </template>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存</el-button>
              <el-button @click="resetProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="section-card">
          <div class="card-header">
            <div>
              <h3>修改密码</h3>
              <p class="muted">更新登录密码</p>
            </div>
          </div>
          <el-form :model="passwordForm" label-width="100px" class="password-form">
            <el-form-item label="旧密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="updatePassword">保存修改</el-button>
              <el-button @click="resetPassword">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="section-card posts-card">
          <div class="card-header">
            <div>
              <h3>我发布的帖子</h3>
              <p class="muted">查看你发布的社区动态</p>
            </div>
            <el-button link type="primary" :loading="postsLoading" @click="fetchData">刷新</el-button>
          </div>
          <el-skeleton v-if="loading" :rows="3" animated />
          <el-empty v-else-if="posts.length === 0" description="你还没有发布帖子" />
          <div v-else class="posts-list">
            <el-card v-for="post in posts" :key="post.id" shadow="hover" class="post-item">
              <div class="post-title">{{ post.title }}</div>
              <div class="post-excerpt">{{ renderExcerpt(post.content) }}</div>
              <div class="post-meta">
                <span>{{ formatDate(post.createdAt) }}</span>
                <el-divider direction="vertical" />
                <span>点赞 {{ post.likeCount }}</span>
                <el-divider direction="vertical" />
                <span>评论 {{ post.commentCount }}</span>
              </div>
              <div class="post-actions">
                <el-button type="primary" link @click="viewPost(post.id)">查看</el-button>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  bindIdentifier,
  getCurrentUser,
  updateCurrentUser,
  updatePassword as updatePasswordApi,
  type UserProfile
} from '../api/user'
import { getMyPosts, type PostSummary } from '../api/community'
import { useUserStore } from '../stores/user'

interface ProfileForm {
  username: string
  avatarUrl?: string
}

interface PasswordForm {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

const router = useRouter()
const userStore = useUserStore()

const profile = ref<UserProfile | null>(null)
const posts = ref<PostSummary[]>([])
const loading = ref(true)
const postsLoading = ref(false)
const savingProfile = ref(false)
const passwordLoading = ref(false)
const bindingLoading = ref(false)

const profileForm = reactive<ProfileForm>({
  username: '',
  avatarUrl: ''
})

const passwordForm = reactive<PasswordForm>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const bindingInput = ref<string>('')

const roleLabel = computed(() => {
  switch (profile.value?.role) {
    case 'ADMIN':
      return '管理员'
    case 'TEACHER':
      return '老师'
    default:
      return '学生'
  }
})

const bindingLabel = computed(() => {
  if (profile.value?.role === 'TEACHER') return '工号'
  return '学号'
})

const boundIdentifier = computed(() => {
  if (!profile.value) return ''
  return profile.value.teacherId || profile.value.studentId || profile.value.identifier || ''
})

const bindingStatusText = computed(() => {
  if (profile.value?.boundStatus) return '已绑定'
  return profile.value?.bindingStatus || '未绑定'
})

const bindingTagType = computed(() => {
  if (profile.value?.boundStatus) return 'success'
  if (profile.value?.bindingStatus === 'PENDING') return 'warning'
  return 'info'
})

const isPending = computed(() => profile.value?.bindingStatus === 'PENDING')

const renderExcerpt = (content?: string) => {
  if (!content) return ''
  const plain = content.replace(/[#>*_`\-]/g, '').replace(/\n+/g, ' ')
  return plain.length > 80 ? `${plain.slice(0, 80)}...` : plain
}

const formatDate = (value?: string | number | Date) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

const resetProfile = () => {
  if (!profile.value) return
  profileForm.username = profile.value.username || ''
  profileForm.avatarUrl = profile.value.avatarUrl || ''
}

const resetPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const fetchData = async () => {
  loading.value = true
  postsLoading.value = true
  try {
    const [profileRes, postsRes] = await Promise.all([getCurrentUser(), getMyPosts()])
    profile.value = profileRes.data
    userStore.setUser(profileRes.data)
    resetProfile()
    posts.value = postsRes.data || []
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取数据失败')
  } finally {
    loading.value = false
    postsLoading.value = false
  }
}

const saveProfile = async () => {
  if (!profileForm.username?.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  savingProfile.value = true
  try {
    const { data } = await updateCurrentUser({
      username: profileForm.username,
      avatarUrl: profileForm.avatarUrl
    })
    profile.value = data
    userStore.setUser(data)
    ElMessage.success('保存成功')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    savingProfile.value = false
  }
}

const submitBinding = async () => {
  if (!bindingInput.value) {
    ElMessage.warning(`请输入${bindingLabel.value}`)
    return
  }
  bindingLoading.value = true
  try {
    const payload: { identifier: string; studentId?: string; teacherId?: string } = {
      identifier: bindingInput.value
    }
    if (profile.value?.role === 'STUDENT') {
      payload.studentId = bindingInput.value
    }
    if (profile.value?.role === 'TEACHER') {
      payload.teacherId = bindingInput.value
    }
    const { data } = await bindIdentifier(payload)
    if (profile.value) {
      profile.value.bindingStatus = data.status
      profile.value.boundStatus = data.status === 'APPROVED'
      profile.value.identifier = data.identifier ?? profile.value.identifier
      profile.value.studentId = data.studentId ?? profile.value.studentId
      profile.value.teacherId = data.teacherId ?? profile.value.teacherId
    }
    if (data.identifier) {
      bindingInput.value = ''
    }
    ElMessage.success('绑定申请已提交')
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    bindingLoading.value = false
  }
}

const updatePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  passwordLoading.value = true
  try {
    await updatePasswordApi({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    resetPassword()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '修改失败')
  } finally {
    passwordLoading.value = false
  }
}

const viewPost = (id: number | string) => {
  router.push({ name: 'postDetail', params: { id } })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.section-card {
  margin-bottom: 16px;
  border-radius: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.muted {
  color: #909399;
  margin: 4px 0 0;
  font-size: 13px;
}

.info-form,
.password-form {
  margin-top: 8px;
}

.avatar-field {
  display: flex;
  gap: 12px;
  align-items: center;
}

.avatar-preview {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #ebeef5;
}

.binding-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.binding-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.binding-input {
  max-width: 260px;
}

.posts-card .post-item {
  margin-bottom: 12px;
  border-radius: 8px;
}

.post-title {
  font-weight: 600;
  margin-bottom: 6px;
}

.post-excerpt {
  color: #606266;
  line-height: 1.5;
  margin-bottom: 6px;
}

.post-meta {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-actions {
  margin-top: 6px;
  text-align: right;
}
</style>
