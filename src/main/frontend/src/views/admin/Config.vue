<template>
  <div class="config-page">
    <div class="card">
      <div class="card-header">
        <h3 style="margin: 0; font-size: 1rem;">⚙️ 系统参数配置</h3>
      </div>
      <div class="card-body">
        <div class="config-form-grid">
          <div class="form-group">
            <label class="form-label">日租金上限</label>
            <input v-model.number="config.maxDailyPrice" type="number" class="form-input" min="0" />
          </div>
          <div class="form-group">
            <label class="form-label">押金比例</label>
            <input v-model.number="config.depositRatio" type="number" class="form-input" step="0.01" min="0" />
          </div>
          <div class="form-group">
            <label class="form-label">超时费率 元/时</label>
            <input v-model.number="config.overtimeRate" type="number" class="form-input" min="0" />
          </div>
          <div class="form-group">
            <label class="form-label">违章保证金</label>
            <input v-model.number="config.violationDeposit" type="number" class="form-input" min="0" />
          </div>
        </div>
        <div class="form-actions" style="border-top: none; padding-top: 0;">
          <button class="btn btn-ghost" @click="handleReset" :disabled="loading">
            <i class="fas fa-undo"></i> 恢复
          </button>
          <button class="btn btn-primary" @click="handleSave" :disabled="saving">
            <i v-if="saving" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-save"></i>
            {{ saving ? '保存中...' : '保存配置' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast-fade">
        <div v-if="toast.visible" :class="['toast', toast.type === 'success' ? 'toast--success' : 'toast--error']">
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { configApi } from '@/api'

const config = reactive({
  maxDailyPrice: null,
  depositRatio: null,
  overtimeRate: null,
  violationDeposit: null
})

const loading = ref(false)
const saving = ref(false)

const toast = reactive({
  visible: false,
  message: '',
  type: 'success'
})

let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.message = message
  toast.type = type
  toast.visible = true
  toastTimer = setTimeout(() => {
    toast.visible = false
  }, 2500)
}

async function loadConfig() {
  loading.value = true
  try {
    const res = await configApi.map()
    if (res && res.code === 200 && res.data) {
      const data = res.data
      config.maxDailyPrice = data.maxDailyPrice ?? null
      config.depositRatio = data.depositRatio ?? null
      config.overtimeRate = data.overtimeRate ?? null
      config.violationDeposit = data.violationDeposit ?? null
    }
  } catch {
    showToast('加载配置失败', 'error')
  } finally {
    loading.value = false
  }
}

async function handleReset() {
  await loadConfig()
  showToast('已恢复为服务器配置')
}

async function handleSave() {
  saving.value = true
  try {
    const payload = {
      maxDailyPrice: config.maxDailyPrice,
      depositRatio: config.depositRatio,
      overtimeRate: config.overtimeRate,
      violationDeposit: config.violationDeposit
    }
    const res = await configApi.saveBatch(payload)
    if (res && res.code === 200) {
      showToast('配置保存成功', 'success')
    } else {
      showToast((res && res.msg) || '保存失败', 'error')
    }
  } catch {
    showToast('网络请求失败', 'error')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.config-page {
  padding: 0;
}

.config-form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 1.5rem;
}

@media (max-width: 768px) {
  .config-form-grid {
    grid-template-columns: 1fr;
  }
}

/* Inline toast overrides for this page */
.toast--success {
  background-color: #10b981;
}

.toast--error {
  background-color: #ef4444;
}

.toast-fade-enter-active {
  animation: toast-in 0.25s ease;
}

.toast-fade-leave-active {
  animation: toast-out 0.2s ease;
}

@keyframes toast-in {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes toast-out {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-8px); }
}
</style>
