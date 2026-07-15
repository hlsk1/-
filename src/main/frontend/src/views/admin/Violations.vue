<template>
  <div class="av-page">
    <div class="av-header">
      <h2>违章管理</h2>
    </div>

    <!-- Toast -->
    <div class="av-toast" v-if="toast.show" :class="'av-toast--' + toast.type">{{ toast.msg }}</div>

    <!-- Toolbar -->
    <div class="av-toolbar">
      <div class="av-toolbar__left">
        <button class="av-btn av-btn--primary" @click="openAddModal">+ 新增违章</button>
      </div>
      <div class="av-toolbar__right">
        <input v-model="searchKey" class="av-search" placeholder="搜索订单号/客户/车牌..." @input="onSearch" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="av-loading">加载中...</div>

    <!-- Table -->
    <table v-else class="av-table">
      <thead>
        <tr>
          <th>订单号</th>
          <th>客户</th>
          <th>车牌</th>
          <th>违章时间</th>
          <th>地点</th>
          <th>扣分</th>
          <th>罚款</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="v in filteredViolations" :key="v.id">
          <td>{{ v.orderNo || '-' }}</td>
          <td>{{ v.customerName || '-' }}</td>
          <td>{{ v.vehiclePlate || '-' }}</td>
          <td>{{ fmtTime(v.violationTime) }}</td>
          <td>{{ v.location || '-' }}</td>
          <td class="av-points">{{ v.points ?? 0 }}</td>
          <td>&yen;{{ v.fineAmount ?? 0 }}</td>
          <td>
            <span class="av-badge" :class="v.status === 'RESOLVED' ? 'av-badge--green' : 'av-badge--amber'">
              {{ v.status === 'RESOLVED' ? '已处理' : '待处理' }}
            </span>
          </td>
          <td>
            <div class="av-actions">
              <button
                v-if="v.status !== 'RESOLVED'"
                class="av-btn av-btn--sm av-btn--success"
                @click="handleResolve(v)"
              >处理完成</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- ===== Add Modal ===== -->
    <div v-if="modalVisible" class="av-modal-overlay" @click.self="closeModal">
      <div class="av-modal">
        <div class="av-modal__header">
          <h3>新增违章</h3>
          <button class="av-modal__close" @click="closeModal">&times;</button>
        </div>
        <div class="av-modal__body">
          <div class="av-field">
            <label>订单号 <span class="av-required">*</span></label>
            <input v-model="form.orderNo" class="av-input" placeholder="订单号" />
          </div>
          <div class="av-field">
            <label>客户姓名 <span class="av-required">*</span></label>
            <input v-model="form.customerName" class="av-input" placeholder="客户姓名" />
          </div>
          <div class="av-field">
            <label>车牌号 <span class="av-required">*</span></label>
            <input v-model="form.vehiclePlate" class="av-input" placeholder="车牌号" />
          </div>
          <div class="av-field">
            <label>违章时间 <span class="av-required">*</span></label>
            <input v-model="form.violationTime" type="datetime-local" class="av-input" />
          </div>
          <div class="av-field">
            <label>地点</label>
            <input v-model="form.location" class="av-input" placeholder="违章地点" />
          </div>
          <div class="av-row">
            <div class="av-field av-field--half">
              <label>扣分</label>
              <input v-model.number="form.points" type="number" class="av-input" placeholder="扣分" />
            </div>
            <div class="av-field av-field--half">
              <label>罚款金额</label>
              <input v-model.number="form.fineAmount" type="number" class="av-input" placeholder="罚款金额" />
            </div>
          </div>
          <div class="av-field">
            <label>备注</label>
            <textarea v-model="form.remark" class="av-input av-textarea" placeholder="备注信息"></textarea>
          </div>
        </div>
        <div class="av-modal__footer">
          <button class="av-btn av-btn--outline" @click="closeModal">取消</button>
          <button class="av-btn av-btn--primary" @click="handleSubmit" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { violationApi } from '@/api'

// ---------- Helpers ----------
function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

// ---------- Data ----------
const violations = ref([])
const searchKey = ref('')
const loading = ref(false)
const modalVisible = ref(false)
const submitting = ref(false)

// ---------- Toast ----------
const toast = reactive({ show: false, msg: '', type: 'success' })
let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  toast.msg = msg
  toast.type = type
  toast.show = true
  toastTimer = setTimeout(() => { toast.show = false }, 2500)
}

// ---------- Filtered ----------
const filteredViolations = computed(() => {
  if (!searchKey.value) return violations.value
  const kw = searchKey.value.toLowerCase()
  return violations.value.filter(v =>
    (v.orderNo || '').toLowerCase().includes(kw) ||
    (v.customerName || '').toLowerCase().includes(kw) ||
    (v.vehiclePlate || '').toLowerCase().includes(kw)
  )
})

function onSearch() { /* computed handles filtering */ }

// ---------- Form ----------
const form = reactive({
  orderNo: '',
  customerName: '',
  vehiclePlate: '',
  violationTime: '',
  location: '',
  points: null,
  fineAmount: null,
  remark: ''
})

function resetForm() {
  form.orderNo = ''
  form.customerName = ''
  form.vehiclePlate = ''
  form.violationTime = ''
  form.location = ''
  form.points = null
  form.fineAmount = null
  form.remark = ''
}

// ---------- Load ----------
async function loadViolations() {
  loading.value = true
  try {
    const res = await violationApi.list()
    violations.value = res.data || []
  } catch { showToast('加载违章列表失败', 'error') }
  finally { loading.value = false }
}

// ---------- Add Modal ----------
function openAddModal() { resetForm(); modalVisible.value = true }
function closeModal() { modalVisible.value = false; resetForm() }

async function handleSubmit() {
  if (!form.orderNo || !form.customerName || !form.vehiclePlate || !form.violationTime) {
    showToast('请填写必填字段（订单号、客户姓名、车牌号、违章时间）', 'error')
    return
  }
  submitting.value = true
  try {
    const res = await violationApi.add({ ...form })
    if (res.code === 200) { showToast('新增违章记录成功'); closeModal(); await loadViolations() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
  finally { submitting.value = false }
}

// ---------- Resolve ----------
async function handleResolve(v) {
  const resolver = prompt('请输入处理人姓名：')
  if (!resolver) return
  try {
    const res = await violationApi.resolve(v.id, resolver)
    if (res.code === 200) { showToast('违章已标记为已处理'); await loadViolations() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
}

// ---------- Mount ----------
onMounted(() => { loadViolations() })
</script>

<style scoped>
/* ===== Page Layout ===== */
.av-page { padding: 20px; }
.av-header { margin-bottom: 20px; }
.av-header h2 { font-size: 22px; font-weight: 700; color: #1a1a1a; margin: 0; }

/* ===== Toast ===== */
.av-toast { position: fixed; top: 20px; right: 20px; z-index: 9999; padding: 12px 24px; border-radius: 8px; color: #fff; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.av-toast--success { background: #52c41a; }
.av-toast--error { background: #ff4d4f; }

/* ===== Toolbar ===== */
.av-toolbar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; margin-bottom: 20px; }
.av-toolbar__left { display: flex; align-items: center; gap: 10px; }
.av-toolbar__right { display: flex; align-items: center; gap: 10px; }
.av-search { padding: 8px 14px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; width: 240px; outline: none; transition: border-color 0.2s; font-family: inherit; }
.av-search:focus { border-color: #4096ff; }

/* ===== Loading ===== */
.av-loading { text-align: center; padding: 60px 0; color: #999; font-size: 15px; }

/* ===== Table ===== */
.av-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.av-table th, .av-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.av-table th { background: #fafafa; font-weight: 600; color: #333; }
.av-table tbody tr:hover td { background: #fafafa; }
.av-points { color: #ff4d4f; font-weight: 700; }

/* ===== Badges ===== */
.av-badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: #fff; }
.av-badge--amber { background: #fa8c16; }
.av-badge--green { background: #52c41a; }

/* ===== Buttons ===== */
.av-btn { padding: 8px 18px; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; gap: 4px; font-family: inherit; }
.av-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.av-btn--primary { background: #4096ff; color: #fff; }
.av-btn--primary:hover:not(:disabled) { background: #1677ff; }
.av-btn--outline { background: #fff; color: #333; border: 1px solid #d9d9d9; }
.av-btn--outline:hover:not(:disabled) { border-color: #4096ff; color: #4096ff; }
.av-btn--success { background: #52c41a; color: #fff; }
.av-btn--success:hover:not(:disabled) { background: #49aa19; }
.av-btn--sm { padding: 5px 12px; font-size: 12px; }
.av-actions { display: flex; gap: 6px; flex-wrap: wrap; }

/* ===== Modal ===== */
.av-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.av-modal { background: #fff; border-radius: 12px; width: 540px; max-height: 85vh; overflow-y: auto; box-shadow: 0 8px 30px rgba(0,0,0,0.18); }
.av-modal__header { display: flex; justify-content: space-between; align-items: center; padding: 18px 24px; border-bottom: 1px solid #f0f0f0; }
.av-modal__header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a1a; }
.av-modal__close { background: none; border: none; font-size: 22px; cursor: pointer; color: #999; line-height: 1; padding: 0; }
.av-modal__close:hover { color: #333; }
.av-modal__body { padding: 20px 24px; }
.av-modal__footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px; border-top: 1px solid #f0f0f0; }

/* ===== Forms ===== */
.av-field { margin-bottom: 14px; }
.av-field label { display: block; margin-bottom: 4px; font-size: 13px; font-weight: 500; color: #333; }
.av-required { color: #ff4d4f; }
.av-row { display: flex; gap: 14px; }
.av-field--half { flex: 1; }
.av-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; outline: none; box-sizing: border-box; transition: border-color 0.2s; font-family: inherit; background: #fff; }
.av-input:focus { border-color: #4096ff; }
.av-textarea { min-height: 80px; resize: vertical; }
</style>
