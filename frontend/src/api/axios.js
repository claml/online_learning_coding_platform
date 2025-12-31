import axios from 'axios'
import router from '../router'
import { useUserStore } from '../stores/user'

const api = axios.create({
  baseURL: 'http://localhost:8080'
})

api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      if (router.currentRoute.value.name !== 'login') {
        await router.push({ name: 'login' })
      }
    }
    return Promise.reject(error)
  }
)

export default api
