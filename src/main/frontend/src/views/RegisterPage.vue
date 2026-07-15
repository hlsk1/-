<template>
  <div class="auth-wrapper--centered">
    <div class="auth-card--register">
      <div class="brand-header">
        <h1>爱<span class="text-accent">租</span>车</h1>
        <p>创建您的租车账户</p>
      </div>

      <div v-if="errorMsg" class="alert alert-error">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>

      <form @submit.prevent="handleRegister">
        <!-- Section 1: Login Account -->
        <fieldset class="form-section">
          <legend><span class="section-icon">&#128274;</span> 登录账号</legend>
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="至少3个字符"
              required
              minlength="3"
              autocomplete="username"
            />
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="至少6个字符"
              required
              minlength="6"
              autocomplete="new-password"
            />
          </div>
          <div class="form-group">
            <label class="form-label">确认密码</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="form-input"
              placeholder="请再次输入密码"
              required
              autocomplete="new-password"
            />
          </div>
        </fieldset>

        <!-- Section 2: Identity Info -->
        <fieldset class="form-section">
          <legend><span class="section-icon">&#128203;</span> 身份信息</legend>
          <div class="form-group">
            <label class="form-label">真实姓名 <span class="required">*</span></label>
            <input
              v-model="form.name"
              type="text"
              class="form-input"
              placeholder="请输入真实姓名"
              required
            />
          </div>
          <div class="form-group">
            <label class="form-label">手机号 <span class="required">*</span></label>
            <input
              v-model="form.phone"
              type="tel"
              class="form-input"
              placeholder="请输入11位手机号"
              required
              @input="onPhoneInput"
              maxlength="11"
            />
          </div>
          <div class="form-group">
            <label class="form-label">身份证号</label>
            <input
              v-model="form.idCard"
              type="text"
              class="form-input"
              placeholder="请输入身份证号（最多18位）"
              maxlength="18"
              @input="onIdCardInput"
            />
          </div>
          <div class="form-group">
            <label class="form-label">驾驶证号</label>
            <input
              v-model="form.license"
              type="text"
              class="form-input"
              placeholder="请输入驾驶证号（选填）"
            />
          </div>
          <div class="form-group">
            <label class="form-label">备注</label>
            <input
              v-model="form.remark"
              type="text"
              class="form-input"
              placeholder="其他备注信息（选填）"
            />
          </div>
        </fieldset>

        <button type="submit" class="btn w-full btn-lg btn-primary" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-user-plus"></i>
          {{ loading ? '注册中...' : '立即注册' }}
        </button>
      </form>

      <div class="auth-footer">
        <router-link to="/login">已有账号？立即登录</router-link>
        &nbsp;·&nbsp;
        <router-link to="/" class="text-muted-link">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'

const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  idCard: '',
  license: '',
  remark: ''
})

const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

function onPhoneInput() {
  form.phone = form.phone.replace(/\D/g, '')
}

function onIdCardInput() {
  form.idCard = form.idCard.toUpperCase()
}

function validate() {
  errorMsg.value = ''

  if (form.username.trim().length < 3) {
    errorMsg.value = '用户名至少需要3个字符'
    return false
  }
  if (form.password.length < 6) {
    errorMsg.value = '密码至少需要6个字符'
    return false
  }
  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return false
  }
  if (!form.name.trim()) {
    errorMsg.value = '请输入真实姓名'
    return false
  }
  if (!form.phone.trim()) {
    errorMsg.value = '请输入手机号'
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    errorMsg.value = '请输入正确的11位手机号'
    return false
  }

  return true
}

async function handleRegister() {
  successMsg.value = ''
  if (!validate()) return

  loading.value = true
  try {
    const res = await authApi.register({
      username: form.username.trim(),
      password: form.password,
      name: form.name.trim(),
      phone: form.phone.trim(),
      idCard: form.idCard.trim(),
      license: form.license.trim(),
      remark: form.remark.trim()
    })

    if (res.code === 200) {
      successMsg.value = '注册成功！即将跳转到登录页面...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMsg.value = res.msg || '注册失败，请稍后重试'
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

.auth-card--register {
  background: var(--card-bg);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 480px;
}

.brand-header {
  text-align: center;
  margin-bottom: 1.75rem;
}

.brand-header h1 {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--dark);
  letter-spacing: 0.06em;
}

.text-accent {
  background: linear-gradient(135deg, var(--accent-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-header p {
  color: var(--text-muted);
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

/* Form sections */
.form-section {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 1.125rem 1.25rem;
  margin-bottom: 1.25rem;
}

.form-section legend {
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--text);
  padding: 0 0.375rem;
}

.section-icon {
  margin-right: 0.25rem;
}

.required {
  color: var(--danger);
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
  .auth-card--register {
    padding: 1.5rem 1.25rem;
  }
}
</style>
