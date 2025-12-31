<template>
  <div class="auth-wrapper">
    <el-card class="auth-card">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="registerForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="example@mail.com" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
          <el-button link @click="toLogin">已有账号？登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api/axios'

const router = useRouter()

const registerForm = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  email: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleRegister = () => {
  registerForm.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await api.post('/auth/register', form.value)
      ElMessage.success('注册成功，请登录')
      router.push({ name: 'login' })
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}

const toLogin = () => router.push({ name: 'login' })
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px);
}

.auth-card {
  width: 420px;
}
</style>
