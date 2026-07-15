<template>
  <div class="auth-wrapper--centered">
    <div class="auth-card--forgot">
      <div class="card-header">
        <div class="lock-icon">
          <i class="fas fa-lock"></i>
        </div>
        <h2>忘记密码</h2>
        <p class="subtitle">请输入您的用户名，系统将生成新的临时密码</p>
      </div>

      <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="form-input"
            placeholder="请输入您的用户名"
            required
            autocomplete="username"
          />
        </div>

        <button type="submit" class="btn w-full btn-lg btn-primary" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-key"></i>
          {{ loading ? '处理中...' : '重置密码' }}
        </button>
      </form>

      <div class="auth-footer">
        <router-link to="/login">返回登录</router-link>
        &nbsp;·&nbsp;
        <router-link to="/" class="text-muted-link">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { authApi } from '@/api'

const username = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

async function handleSubmit() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!username.value.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }

  loading.value = true
  try {
    const res = await authApi.forgotPassword(username.value.trim())

    if (res.code === 200) {
      const tempPwd = res.data || res.msg || '新密码'
      successMsg.value = `密码重置成功！您的临时密码为：${tempPwd}，请尽快登录并修改密码。`
    } else {
      errorMsg.value = res.msg || '重置失败，请确认用户名是否正确'
    }
  } catch {
    errorMsg.value = '网络连接失败，请检查网络后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrapper--centered {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(135deg, var(--gray-100) 0%, #eef2ff 100%);
}

.auth-card--forgot {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 420px;
}

.card-header {
  text-align: center;
  margin-bottom: 1.5rem;
}

.lock-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--primary-light);
  color: var(--primary);
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.card-header h2 {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--dark);
  margin-bottom: 0.375rem;
}

.subtitle {
  color: var(--text-muted);
  font-size: 0.8125rem;
  line-height: 1.5;
}

/* Buttons */
.w-full { width: 100%; }

/* Footer */
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

.auth-footer a.text-muted-link {
  color: var(--gray-400);
}

.auth-footer a:hover {
  text-decoration: underline;
}

/* Responsive */
@media (max-width: 520px) {
  .auth-wrapper--centered {
    padding: 1rem;
    align-items: flex-start;
  }
  .auth-card--forgot {
    padding: 1.5rem 1.25rem;
  }
}
</style>
