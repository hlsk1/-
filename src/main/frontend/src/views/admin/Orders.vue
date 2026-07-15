<template>
  <div class="ao-page">
    <div class="ao-header">
      <h2>订单管理</h2>
    </div>

    <!-- Toast -->
    <div class="ao-toast" v-if="toast.show" :class="'ao-toast--' + toast.type">{{ toast.msg }}</div>

    <!-- Toolbar -->
    <div class="ao-toolbar">
      <div class="ao-toolbar__left">
        <button class="ao-btn ao-btn--primary" @click="openCreateModal">+ 创建订单</button>
      </div>
      <div class="ao-toolbar__right">
        <input v-model="searchKey" class="ao-search" placeholder="搜索订单号/客户/车辆..." @input="onSearch" />
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="ao-loading">加载中...</div>

    <!-- Table -->
    <table v-else class="ao-table">
      <thead>
        <tr>
          <th>订单号</th>
          <th>客户</th>
          <th>车辆</th>
          <th>取车时间</th>
          <th>还车时间</th>
          <th>金额</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in filteredOrders" :key="o.id">
          <td>{{ o.orderNo }}</td>
          <td>{{ o.customerName || '-' }}</td>
          <td>{{ o.vehiclePlate || '-' }}</td>
          <td>{{ fmtTime(o.pickupTime) }}</td>
          <td>{{ fmtTime(o.returnTime) }}</td>
          <td>&yen;{{ o.totalAmount ?? 0 }}</td>
          <td>
            <span class="ao-badge" :class="orderStatusBadgeClass(o.status)">{{ orderStatusLabel(o.status) }}</span>
          </td>
          <td>
            <div class="ao-actions">
              <button v-if="o.status === 'PENDING'" class="ao-btn ao-btn--sm ao-btn--primary" @click="handlePickup(o)">取车</button>
              <button v-if="o.status === 'PICKED_UP'" class="ao-btn ao-btn--sm ao-btn--success" @click="openReturnModal(o)">还车</button>
              <button v-if="o.status === 'PENDING'" class="ao-btn ao-btn--sm ao-btn--warning" @click="handleCancel(o)">取消</button>
              <button v-if="o.status === 'RETURNED'" class="ao-btn ao-btn--sm ao-btn--warning" @click="handleMarkAbnormal(o)">标记异常</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- ===== Create Order Modal ===== -->
    <div v-if="createModalVisible" class="ao-modal-overlay" @click.self="closeCreateModal">
      <div class="ao-modal">
        <div class="ao-modal__header">
          <h3>创建订单</h3>
          <button class="ao-modal__close" @click="closeCreateModal">&times;</button>
        </div>
        <div class="ao-modal__body">
          <div class="ao-field">
            <label>客户 <span class="ao-required">*</span></label>
            <select v-model="createForm.customerId" class="ao-input">
              <option value="">-- 请选择客户 --</option>
              <option v-for="c in customerList" :key="c.id" :value="c.id">{{ c.name }} ({{ c.phone }})</option>
            </select>
          </div>
          <div class="ao-field">
            <label>车辆 <span class="ao-required">*</span></label>
            <select v-model="createForm.vehicleId" class="ao-input">
              <option value="">-- 请选择车辆 --</option>
              <option v-for="v in availableVehicles" :key="v.id" :value="v.id">
                {{ v.brand }} {{ v.model }} ({{ v.plateNumber }}) - &yen;{{ v.dailyPrice }}/天
              </option>
            </select>
          </div>
          <div class="ao-field">
            <label>取车时间 <span class="ao-required">*</span></label>
            <input v-model="createForm.pickupTime" type="datetime-local" class="ao-input" />
          </div>
          <div class="ao-field">
            <label>还车时间 <span class="ao-required">*</span></label>
            <input v-model="createForm.returnTime" type="datetime-local" class="ao-input" />
          </div>
        </div>
        <div class="ao-modal__footer">
          <button class="ao-btn ao-btn--outline" @click="closeCreateModal">取消</button>
          <button class="ao-btn ao-btn--primary" @click="handleCreateOrder" :disabled="createSubmitting">
            {{ createSubmitting ? '提交中...' : '提交' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ===== Return Modal ===== -->
    <div v-if="returnModalVisible" class="ao-modal-overlay" @click.self="closeReturnModal">
      <div class="ao-modal">
        <div class="ao-modal__header">
          <h3>还车确认</h3>
          <button class="ao-modal__close" @click="closeReturnModal">&times;</button>
        </div>
        <div class="ao-modal__body">
          <div class="ao-field">
            <label>实际还车时间 <span class="ao-required">*</span></label>
            <input v-model="returnForm.actualReturnTime" type="datetime-local" class="ao-input" />
          </div>
          <div class="ao-field">
            <label>车况描述</label>
            <textarea v-model="returnForm.carCondition" class="ao-input ao-textarea" placeholder="请描述车辆状况..."></textarea>
          </div>
        </div>
        <div class="ao-modal__footer">
          <button class="ao-btn ao-btn--outline" @click="closeReturnModal">取消</button>
          <button class="ao-btn ao-btn--primary" @click="handleReturnOrder" :disabled="returnSubmitting">
            {{ returnSubmitting ? '提交中...' : '确认还车' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { orderApi, customerApi, vehicleApi } from '@/api'

// ---------- Status Helpers ----------
const ORDER_STATUS_MAP = {
  PENDING: '待取车',
  PICKED_UP: '已取车',
  RETURNED: '已还车',
  CANCELLED: '已取消',
  COMPLETED: '已完成'
}
function orderStatusLabel(s) { return ORDER_STATUS_MAP[s] || s }

function orderStatusBadgeClass(s) {
  if (s === 'PENDING') return 'ao-badge--amber'
  if (s === 'PICKED_UP') return 'ao-badge--blue'
  if (s === 'RETURNED' || s === 'COMPLETED') return 'ao-badge--green'
  if (s === 'CANCELLED') return 'ao-badge--red'
  return 'ao-badge--gray'
}

function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

// ---------- Data ----------
const orders = ref([])
const searchKey = ref('')
const loading = ref(false)
const customerList = ref([])
const availableVehicles = ref([])

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

// ---------- Filtered Orders ----------
const filteredOrders = computed(() => {
  if (!searchKey.value) return orders.value
  const kw = searchKey.value.toLowerCase()
  return orders.value.filter(o =>
    (o.orderNo || '').toLowerCase().includes(kw) ||
    (o.customerName || '').toLowerCase().includes(kw) ||
    (o.vehiclePlate || '').toLowerCase().includes(kw)
  )
})

function onSearch() { /* computed handles filtering */ }

// ---------- Load Data ----------
async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.list()
    orders.value = res.data || []
  } catch { showToast('加载订单列表失败', 'error') }
  finally { loading.value = false }
}

// ---------- Create Order ----------
const createModalVisible = ref(false)
const createSubmitting = ref(false)
const createForm = reactive({ customerId: '', vehicleId: '', pickupTime: '', returnTime: '' })

async function openCreateModal() {
  createForm.customerId = ''
  createForm.vehicleId = ''
  createForm.pickupTime = ''
  createForm.returnTime = ''
  createModalVisible.value = true
  try {
    const [custRes, vehRes] = await Promise.all([customerApi.list(), vehicleApi.list()])
    customerList.value = custRes.data || []
    availableVehicles.value = (vehRes.data || []).filter(v => v.status === 'AVAILABLE')
  } catch { showToast('加载客户/车辆数据失败', 'error') }
}

function closeCreateModal() { createModalVisible.value = false }

async function handleCreateOrder() {
  if (!createForm.customerId || !createForm.vehicleId || !createForm.pickupTime || !createForm.returnTime) {
    showToast('请填写所有必填字段', 'error')
    return
  }
  if (createForm.pickupTime >= createForm.returnTime) {
    showToast('还车时间必须晚于取车时间', 'error')
    return
  }
  createSubmitting.value = true
  try {
    const res = await orderApi.create({
      customerId: Number(createForm.customerId),
      vehicleId: Number(createForm.vehicleId),
      pickupTime: createForm.pickupTime,
      returnTime: createForm.returnTime
    })
    if (res.code === 200) { showToast('订单创建成功'); closeCreateModal(); await loadOrders() }
    else { showToast(res.msg || '创建失败', 'error') }
  } catch { showToast('创建失败', 'error') }
  finally { createSubmitting.value = false }
}

// ---------- Pickup ----------
async function handlePickup(o) {
  try {
    const res = await orderApi.pickup(o.id)
    if (res.code === 200) { showToast('取车确认成功'); await loadOrders() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
}

// ---------- Return ----------
const returnModalVisible = ref(false)
const returnSubmitting = ref(false)
const returningOrderId = ref(null)
const returnForm = reactive({ actualReturnTime: '', carCondition: '' })

function openReturnModal(o) {
  returningOrderId.value = o.id
  returnForm.actualReturnTime = ''
  returnForm.carCondition = ''
  returnModalVisible.value = true
}

function closeReturnModal() { returnModalVisible.value = false; returningOrderId.value = null }

async function handleReturnOrder() {
  if (!returnForm.actualReturnTime) { showToast('请填写实际还车时间', 'error'); return }
  returnSubmitting.value = true
  try {
    const res = await orderApi.return({
      orderId: returningOrderId.value,
      actualReturnTime: returnForm.actualReturnTime,
      carCondition: returnForm.carCondition || undefined
    })
    if (res.code === 200) { showToast('还车确认成功'); closeReturnModal(); await loadOrders() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
  finally { returnSubmitting.value = false }
}

// ---------- Cancel ----------
async function handleCancel(o) {
  const reason = prompt('请输入取消原因：')
  if (!reason) return
  try {
    const res = await orderApi.cancel(o.id, reason)
    if (res.code === 200) { showToast('订单已取消'); await loadOrders() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
}

// ---------- Mark Abnormal ----------
async function handleMarkAbnormal(o) {
  const type = prompt('请输入异常类型（如：车损/违章/其他）：')
  if (!type) return
  const remark = prompt('请输入异常备注：')
  try {
    const res = await orderApi.markAbnormal({ orderId: o.id, type, remark: remark || undefined })
    if (res.code === 200) { showToast('已标记为异常'); await loadOrders() }
    else { showToast(res.msg || '操作失败', 'error') }
  } catch { showToast('操作失败', 'error') }
}

// ---------- Mount ----------
onMounted(() => { loadOrders() })
</script>

<style scoped>
/* ===== Page Layout ===== */
.ao-page { padding: 20px; }
.ao-header { margin-bottom: 20px; }
.ao-header h2 { font-size: 22px; font-weight: 700; color: #1a1a1a; margin: 0; }

/* ===== Toast ===== */
.ao-toast { position: fixed; top: 20px; right: 20px; z-index: 9999; padding: 12px 24px; border-radius: 8px; color: #fff; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
.ao-toast--success { background: #52c41a; }
.ao-toast--error { background: #ff4d4f; }

/* ===== Toolbar ===== */
.ao-toolbar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; margin-bottom: 20px; }
.ao-toolbar__left { display: flex; align-items: center; gap: 10px; }
.ao-toolbar__right { display: flex; align-items: center; gap: 10px; }
.ao-search { padding: 8px 14px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; width: 240px; outline: none; transition: border-color 0.2s; font-family: inherit; }
.ao-search:focus { border-color: #4096ff; }

/* ===== Loading / Empty ===== */
.ao-loading { text-align: center; padding: 60px 0; color: #999; font-size: 15px; }

/* ===== Table ===== */
.ao-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.ao-table th, .ao-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.ao-table th { background: #fafafa; font-weight: 600; color: #333; }
.ao-table tbody tr:hover td { background: #fafafa; }

/* ===== Badges ===== */
.ao-badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: #fff; }
.ao-badge--amber { background: #fa8c16; }
.ao-badge--blue { background: #1677ff; }
.ao-badge--red { background: #ff4d4f; }
.ao-badge--green { background: #52c41a; }
.ao-badge--gray { background: #8c8c8c; }

/* ===== Buttons ===== */
.ao-btn { padding: 8px 18px; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; gap: 4px; font-family: inherit; }
.ao-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.ao-btn--primary { background: #4096ff; color: #fff; }
.ao-btn--primary:hover:not(:disabled) { background: #1677ff; }
.ao-btn--outline { background: #fff; color: #333; border: 1px solid #d9d9d9; }
.ao-btn--outline:hover:not(:disabled) { border-color: #4096ff; color: #4096ff; }
.ao-btn--success { background: #52c41a; color: #fff; }
.ao-btn--success:hover:not(:disabled) { background: #49aa19; }
.ao-btn--warning { background: #fa8c16; color: #fff; }
.ao-btn--warning:hover:not(:disabled) { background: #d87a0d; }
.ao-btn--sm { padding: 5px 12px; font-size: 12px; }
.ao-actions { display: flex; gap: 6px; flex-wrap: wrap; }

/* ===== Modal ===== */
.ao-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.ao-modal { background: #fff; border-radius: 12px; width: 540px; max-height: 85vh; overflow-y: auto; box-shadow: 0 8px 30px rgba(0,0,0,0.18); }
.ao-modal__header { display: flex; justify-content: space-between; align-items: center; padding: 18px 24px; border-bottom: 1px solid #f0f0f0; }
.ao-modal__header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a1a; }
.ao-modal__close { background: none; border: none; font-size: 22px; cursor: pointer; color: #999; line-height: 1; padding: 0; }
.ao-modal__close:hover { color: #333; }
.ao-modal__body { padding: 20px 24px; }
.ao-modal__footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px; border-top: 1px solid #f0f0f0; }

/* ===== Forms ===== */
.ao-field { margin-bottom: 14px; }
.ao-field label { display: block; margin-bottom: 4px; font-size: 13px; font-weight: 500; color: #333; }
.ao-required { color: #ff4d4f; }
.ao-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; outline: none; box-sizing: border-box; transition: border-color 0.2s; font-family: inherit; background: #fff; }
.ao-input:focus { border-color: #4096ff; }
.ao-textarea { min-height: 80px; resize: vertical; }
</style>
