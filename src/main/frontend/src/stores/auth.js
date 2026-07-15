import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(sessionStorage.getItem('user') || 'null'))
  const token = ref(sessionStorage.getItem('token') || '')

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => {
    const role = user.value?.role
    return role === 'SUPER' || role === 'ADMIN'
  })
  const isSuper = computed(() => user.value?.role === 'SUPER')
  const userName = computed(() => user.value?.realName || user.value?.username || '')
  const userAvatar = computed(() => userName.value.charAt(0))
  const roleLabel = computed(() => {
    const map = { SUPER: '系统管理员', ADMIN: '管理员', NORMAL: '普通用户', CUSTOMER: '注册客户' }
    return map[user.value?.role] || user.value?.role || ''
  })

  async function login(username, password) {
    const res = await userApi.login({ username, password })
    if (res.code === 200 && res.data) {
      user.value = res.data
      token.value = res.data.username
      sessionStorage.setItem('user', JSON.stringify(res.data))
      sessionStorage.setItem('token', res.data.username)
      return { success: true, data: res.data }
    }
    return { success: false, msg: res.msg || '登录失败' }
  }

  function logout() {
    user.value = null
    token.value = ''
    sessionStorage.clear()
  }

  return { user, token, isLoggedIn, isAdmin, isSuper, userName, userAvatar, roleLabel, login, logout }
})
