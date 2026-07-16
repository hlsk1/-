<template>
  <div class="auth-wrapper">
    <div class="auth-left">
      <div class="auth-left-content">
        <div class="logo-icon"><i class="fas fa-car"></i></div>
        <h1>爱<span class="text-accent">租</span>车</h1>
        <p>专业汽车租赁管理平台</p>
        <div class="features">
          <div class="feat"><i class="fas fa-check-circle"></i> 50+精选车型</div>
          <div class="feat"><i class="fas fa-check-circle"></i> 7×24小时服务</div>
          <div class="feat"><i class="fas fa-check-circle"></i> 全险安全保障</div>
          <div class="feat"><i class="fas fa-check-circle"></i> 极速取车还车</div>
        </div>
      </div>
    </div>
    <div class="auth-right">
      <div class="auth-card">
        <div class="tabs">
          <button class="tab" :class="{ active: tab === 'user' }" @click="switchTab('user')">
            <i class="fas fa-user"></i> 用户登录
          </button>
          <button class="tab admin-tab" :class="{ active: tab === 'admin' }" @click="switchTab('admin')">
            <i class="fas fa-shield-haltered"></i> 管理员
          </button>
        </div>
        <h2>{{ tab === 'user' ? '欢迎回来' : '管理员登录' }}</h2>
        <p class="subtitle">{{ tab === 'user' ? '请使用您的账号登录' : '请使用管理员账号登录' }}</p>
        <span class="role-tag" :class="tab">{{ tab === 'user' ? '👤 用户登录' : '🛡️ 管理员' }}</span>

        <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>

        <form @submit.prevent="handleLogin">
          <div class="form-group input-icon">
            <i :class="tab === 'user' ? 'fas fa-user' : 'fas fa-shield-haltered'"></i>
            <input v-model="username" :placeholder="tab === 'user' ? '用户名 / 手机号' : '管理员账号'" required autocomplete="username" />
          </div>
          <div class="form-group input-icon">
            <i class="fas fa-lock"></i>
            <input v-model="password" type="password" placeholder="输入密码" required autocomplete="current-password" />
          </div>
          <button type="submit" class="btn w-full btn-lg" :class="tab === 'admin' ? 'btn-primary' : 'btn-primary'" :disabled="loading">
            <i v-if="loading" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-arrow-right-to-bracket"></i>
            {{ tab === 'user' ? '用户登录' : '管理员登录' }}
          </button>
        </form>

        <div class="auth-footer">
          <router-link to="/register">注册新账号</router-link> &nbsp;·&nbsp;
          <router-link to="/forgot-password" class="warn">忘记密码</router-link> &nbsp;·&nbsp;
          <router-link to="/" class="text-muted-link">返回首页</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const tab = ref('user')
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)

const presets = {
  user: { username: '', password: '' },
  admin: { username: 'admin', password: '123456' }
}

function switchTab(t) {
  tab.value = t
  username.value = presets[t].username
  password.value = presets[t].password
  errorMsg.value = ''
}

async function handleLogin() {
  errorMsg.value = ''
  if (!username.value.trim() || !password.value) {
    errorMsg.value = '请输入账号和密码'
    return
  }
  loading.value = true
  try {
    const result = await authStore.login(username.value.trim(), password.value)
    if (result.success) {
      const role = result.data.role
      if (role === 'SUPER' || role === 'ADMIN') {
        router.push('/admin')
      } else {
        router.push('/user')
      }
    } else {
      errorMsg.value = result.msg
    }
  } catch {
    errorMsg.value = '网络连接失败'
  } finally {
    loading.value = false
  }
}

switchTab('user')
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  width: 100%;
  min-height: 100vh;
}
.auth-left {
  flex: 1;
  background: linear-gradient(135deg, var(--dark) 0%, #1e3a5f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  position: relative;
  overflow: hidden;
}
.auth-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(ellipse at 30% 50%, rgba(245,158,11,0.08) 0%, transparent 50%),
              radial-gradient(ellipse at 70% 30%, rgba(37,99,235,0.06) 0%, transparent 50%);
}
.auth-left-content {
  position: relative;
  z-index: 1;
  text-align: center;
}
.logo-icon {
  font-size: 3.5rem;
  color: var(--accent);
  margin-bottom: 1rem;
}
.auth-left-content h1 {
  font-size: 2rem;
  color: #fff;
  font-weight: 800;
  letter-spacing: 0.08em;
}
.text-accent {
  background: linear-gradient(135deg, var(--accent-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.auth-left-content > p {
  color: rgba(255,255,255,0.55);
  margin-top: 0.5rem;
  font-size: 0.9375rem;
}
.features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-top: 2.5rem;
  text-align: left;
}
.feat {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255,255,255,0.65);
  font-size: 0.875rem;
}
.feat i { color: var(--success); }

.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background: #f3f4f6;
}
.auth-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08), 0 1px 4px rgba(0,0,0,0.04);
  padding: 2.5rem 2rem;
}
.tabs {
  display: flex;
  background: var(--gray-100);
  border-radius: var(--radius);
  padding: 4px;
  margin-bottom: 2rem;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 0.625rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--gray-500);
  border: none;
  background: transparent;
  font-family: inherit;
}
.tab.active {
  background: #fff;
  color: var(--primary);
  box-shadow: var(--shadow-sm);
}
.tab.admin-tab.active { color: #7c3aed; }
.auth-card h2 {
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
  color: var(--dark);
}
.subtitle {
  font-size: 0.875rem;
  color: var(--gray-400);
  margin-bottom: 1.5rem;
}
.role-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-bottom: 1rem;
}
.role-tag.user { background: var(--primary-light); color: var(--primary); }
.role-tag.admin { background: #f5f3ff; color: #7c3aed; }
.auth-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.875rem;
  color: var(--gray-400);
}
.auth-footer a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 600;
}
.auth-footer a.warn { color: var(--warning); }
.auth-footer a.text-muted-link { color: var(--gray-400); }
.auth-footer a:hover { text-decoration: underline; }
.w-full { width: 100%; }
.input-icon { position: relative; }
.input-icon i {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  color: var(--gray-400);
  z-index: 1;
}
.input-icon input {
  width: 100%;
  padding: 0.625rem 0.875rem 0.625rem 2.5rem;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
  outline: none;
  font-family: inherit;
}
.input-icon input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(37,99,235,.08);
}
@media (max-width: 768px) {
  .auth-left { display: none; }
  .auth-right { padding: 2rem; }
}
</style>
