<template>
  <div class="uh-page" v-if="user">
    <!-- Toast -->
    <div class="uh-toast" v-if="toast.show" :class="'uh-toast--' + toast.type">{{ toast.msg }}</div>

    <!-- Top Nav Bar -->
    <header class="uh-header">
      <div class="uh-header__inner">
        <span class="uh-header__brand"><i class="fa-solid fa-car"></i> 爱租车</span>
        <nav class="uh-header__tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            :class="['uh-header__tab', { 'uh-header__tab--active': activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >{{ tab.label }}</button>
        </nav>
        <div class="uh-header__right">
          <span class="uh-header__username">{{ user.realName || user.username }}</span>
          <button class="uh-header__logout" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="uh-main">
      <div class="uh-container">

        <!-- ========== 首页 ========== -->
        <section v-if="activeTab === 'home'" class="uh-section">
          <div class="uh-hero">
            <div class="uh-hero__text">
              <h1>欢迎回来，{{ user.realName || user.username }}</h1>
            </div>
            <div class="uh-hero__points">
              <span class="uh-hero__points-label">可用积分</span>
              <span class="uh-hero__points-value">{{ customerPoints }}</span>
            </div>
          </div>

          <div class="uh-stat-grid">
            <div class="uh-stat-card">
              <div class="uh-stat-card__icon uh-stat-card__icon--blue"><i class="fa-solid fa-receipt"></i></div>
              <div class="uh-stat-card__info">
                <span class="uh-stat-card__value">{{ orders.length }}</span>
                <span class="uh-stat-card__label">我的订单数</span>
              </div>
            </div>
            <div class="uh-stat-card">
              <div class="uh-stat-card__icon uh-stat-card__icon--red"><i class="fa-solid fa-triangle-exclamation"></i></div>
              <div class="uh-stat-card__info">
                <span class="uh-stat-card__value">{{ pendingViolations }}</span>
                <span class="uh-stat-card__label">待处理违章</span>
              </div>
            </div>
            <div class="uh-stat-card">
              <div class="uh-stat-card__icon uh-stat-card__icon--amber"><i class="fa-solid fa-star"></i></div>
              <div class="uh-stat-card__info">
                <span class="uh-stat-card__value">{{ customerPoints }}</span>
                <span class="uh-stat-card__label">可用积分</span>
              </div>
            </div>
          </div>

          <div class="uh-quick-actions">
            <button class="uh-btn uh-btn--primary" @click="activeTab = 'vehicles'">浏览车型</button>
            <button class="uh-btn uh-btn--outline" @click="activeTab = 'orders'">查看订单</button>
          </div>
        </section>

        <!-- ========== 我的订单 ========== -->
        <section v-if="activeTab === 'orders'" class="uh-section">
          <div class="uh-section-header">
            <h2>我的订单</h2>
            <span>共 {{ orders.length }} 条</span>
          </div>

          <div v-if="orderLoading" class="uh-loading">加载中...</div>

          <table v-else-if="orders.length > 0" class="uh-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>车辆</th>
                <th>取车时间</th>
                <th>还车时间</th>
                <th>天数</th>
                <th>押金</th>
                <th>总金额</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="(o, idx) in orders" :key="o.id">
                <tr
                  :style="{ background: idx % 2 === 0 ? '#ffffff' : '#f5f5f5' }"
                  @click="showOrderDetail(o)"
                >
                  <td><strong>{{ o.orderNo }}</strong></td>
                  <td>{{ o.vehicleBrand }} {{ o.vehicleModel }}<br><span style="font-size:11px;color:#999;">{{ o.vehiclePlate }}</span></td>
                  <td>{{ fmtTime(o.pickupTime) }}</td>
                  <td>{{ fmtTime(o.returnTime) }}</td>
                  <td>{{ o.rentDays ?? '-' }}天</td>
                  <td>&yen;{{ o.depositAmount ?? 0 }}</td>
                  <td style="font-weight:600;">&yen;{{ o.finalAmount || o.totalAmount || 0 }}</td>
                  <td><span class="uh-badge" :class="orderStatusBadgeCls(o.status)">{{ orderStatusLabel(o.status) }}</span></td>
                  <td @click.stop>
                    <div class="uh-actions">
                      <button v-if="o.status === 'PENDING'" class="uh-btn uh-btn--sm uh-btn--warning" @click="cancelOrder(o)">取消</button>
                      <button v-if="o.status === 'COMPLETED' && !reviewedOrderIds.has(o.id)" class="uh-btn uh-btn--sm uh-btn--accent" style="background:#d97706;" @click="goReview(o)">去评价</button>
                      <button v-if="o.status === 'COMPLETED' && reviewedOrderIds.has(o.id)" class="uh-btn uh-btn--sm review-toggle-btn" @click="toggleReviewDetail(o.id)">评价详情</button>
                    </div>
                  </td>
                </tr>
                <tr
                  v-if="o.status === 'COMPLETED' && reviewedOrderIds.has(o.id) && expandedReviews.has(o.id)"
                  :style="{ background: idx % 2 === 0 ? '#ffffff' : '#f5f5f5' }"
                >
                  <td :colspan="9" style="padding:0;border-top:none;">
                    <div class="review-detail-cell">
                      <div class="review-detail-cell__top">
                        <span class="review-detail-cell__stars">{{ starsHtml(reviewMap[o.id]?.rating) }}</span>
                        <span class="review-detail-cell__time">🕐 {{ fmtTime(reviewMap[o.id]?.createTime) }}</span>
                      </div>
                      <p class="review-detail-cell__content">{{ reviewMap[o.id]?.content || '（无内容）' }}</p>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
          <div v-else class="uh-empty">暂无订单记录</div>

          <!-- Cancel Dialog -->
          <div v-if="cancelDialog.show" class="uh-modal-overlay" @click.self="cancelDialog.show = false">
            <div class="uh-modal">
              <div class="uh-modal__header">
                <h3>取消订单</h3>
                <button class="uh-modal__close" @click="cancelDialog.show = false">&times;</button>
              </div>
              <div class="uh-modal__body">
                <div class="uh-field">
                  <label>取消原因</label>
                  <input v-model="cancelDialog.reason" class="uh-input" placeholder="请输入取消原因" />
                </div>
              </div>
              <div class="uh-modal__footer">
                <button class="uh-btn uh-btn--outline" @click="cancelDialog.show = false">返回</button>
                <button class="uh-btn uh-btn--danger" @click="confirmCancel" :disabled="cancelDialog.loading">
                  {{ cancelDialog.loading ? '提交中...' : '确认取消' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Order Detail Dialog -->
          <div v-if="detailDialog.show" class="uh-modal-overlay" @click.self="detailDialog.show = false">
            <div class="uh-modal">
              <div class="uh-modal__header">
                <h3>订单详情</h3>
                <button class="uh-modal__close" @click="detailDialog.show = false">&times;</button>
              </div>
              <div class="uh-modal__body">
                <div v-if="detailDialog.order" class="uh-detail-grid">
                  <div class="uh-detail-item"><span class="uh-detail-label">订单号</span><span>{{ detailDialog.order.orderNo }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">车辆</span><span>{{ detailDialog.order.vehicleBrand }} {{ detailDialog.order.vehicleModel }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">取车时间</span><span>{{ fmtTime(detailDialog.order.pickupTime) }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">还车时间</span><span>{{ fmtTime(detailDialog.order.returnTime) }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">天数</span><span>{{ detailDialog.order.rentDays ?? '-' }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">押金</span><span>&yen;{{ detailDialog.order.deposit ?? 0 }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">总金额</span><span>&yen;{{ detailDialog.order.totalAmount ?? 0 }}</span></div>
                  <div class="uh-detail-item"><span class="uh-detail-label">状态</span><span class="uh-badge" :class="orderStatusBadgeCls(detailDialog.order.status)">{{ orderStatusLabel(detailDialog.order.status) }}</span></div>
                </div>
              </div>
              <div class="uh-modal__footer">
                <button class="uh-btn uh-btn--outline" @click="detailDialog.show = false">关闭</button>
              </div>
            </div>
          </div>
        </section>

        <!-- ========== 浏览车型 ========== -->
        <section v-if="activeTab === 'vehicles'" class="uh-section">
          <div class="uh-section-header">
            <h2>浏览车型</h2>
            <span>共 {{ availableVehicles.length }} 辆可租</span>
          </div>

          <div v-if="vehicleLoading" class="uh-loading">加载中...</div>

          <div v-else-if="availableVehicles.length > 0" class="uh-vehicle-grid">
            <div v-for="v in availableVehicles" :key="v.id" class="uh-vehicle-card">
              <div class="uh-vehicle-card__img">
                <img v-if="v.imageUrl" :src="v.imageUrl" :alt="v.brand + ' ' + v.model" />
                <span v-else>🚗</span>
              </div>
              <div class="uh-vehicle-card__body">
                <h3>{{ v.brand }} {{ v.model }}</h3>
                <div class="uh-vehicle-card__meta">
                  <span>{{ v.plateNumber }}</span>
                  <span>{{ v.color }}</span>
                  <span>{{ v.seats }}座</span>
                  <span>{{ v.transmission }}</span>
                </div>
                <div class="uh-vehicle-card__footer">
                  <span class="uh-vehicle-card__price">&yen;{{ v.dailyPrice }}<small>/天</small></span>
                  <button class="uh-btn uh-btn--sm uh-btn--accent" @click="openRentModal(v)">立即租车</button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="uh-empty">暂无可租车辆</div>

          <!-- Rent Modal -->
          <div v-if="rentDialog.show" class="uh-modal-overlay" @click.self="rentDialog.show = false">
            <div class="uh-modal">
              <div class="uh-modal__header">
                <h3>创建订单</h3>
                <button class="uh-modal__close" @click="rentDialog.show = false">&times;</button>
              </div>
              <div class="uh-modal__body">
                <div v-if="rentDialog.vehicle" class="uh-rent-info">
                  <strong>{{ rentDialog.vehicle.brand }} {{ rentDialog.vehicle.model }}</strong>
                  <span>{{ rentDialog.vehicle.plateNumber }} | &yen;{{ rentDialog.vehicle.dailyPrice }}/天</span>
                </div>
                <div class="uh-field">
                  <label>取车时间 <span class="uh-required">*</span></label>
                  <input v-model="rentDialog.pickupTime" type="datetime-local" class="uh-input" />
                </div>
                <div class="uh-field">
                  <label>还车时间 <span class="uh-required">*</span></label>
                  <input v-model="rentDialog.returnTime" type="datetime-local" class="uh-input" />
                </div>
                <div v-if="rentCost.days > 0" class="uh-cost-summary">
                  <div class="uh-cost-row"><span>租车天数</span><span>{{ rentCost.days }} 天</span></div>
                  <div class="uh-cost-row"><span>日租金</span><span>&yen;{{ rentDialog.vehicle.dailyPrice }}</span></div>
                  <div class="uh-cost-row uh-cost-row--total"><span>总金额</span><span>&yen;{{ rentCost.totalAmount }}</span></div>
                  <div class="uh-cost-row"><span>押金 (30%)</span><span>&yen;{{ rentCost.deposit }}</span></div>
                </div>
                <div v-else class="uh-cost-hint">请选择取车和还车时间以计算费用</div>
              </div>
              <div class="uh-modal__footer">
                <button class="uh-btn uh-btn--outline" @click="rentDialog.show = false">取消</button>
                <button class="uh-btn uh-btn--primary" @click="confirmRent" :disabled="rentDialog.loading || rentCost.days <= 0">
                  {{ rentDialog.loading ? '提交中...' : '确认下单' }}
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- ========== 违章记录 ========== -->
        <section v-if="activeTab === 'violations'" class="uh-section">
          <div class="uh-section-header">
            <h2>违章记录</h2>
            <span>共 {{ violations.length }} 条</span>
          </div>

          <div v-if="violationLoading" class="uh-loading">加载中...</div>

          <table v-else-if="violations.length > 0" class="uh-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>车牌</th>
                <th>违章时间</th>
                <th>地点</th>
                <th>扣分</th>
                <th>罚款</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="v in violations" :key="v.id">
                <td>{{ v.orderNo || '-' }}</td>
                <td>{{ v.vehiclePlate || '-' }}</td>
                <td>{{ fmtTime(v.violationTime) }}</td>
                <td>{{ v.location || '-' }}</td>
                <td class="uh-points">{{ v.points ?? 0 }}</td>
                <td>&yen;{{ v.fineAmount ?? 0 }}</td>
                <td>
                  <span class="uh-badge" :class="v.status === 'RESOLVED' ? 'uh-badge--green' : 'uh-badge--amber'">
                    {{ v.status === 'RESOLVED' ? '已处理' : '待处理' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else class="uh-empty">暂无违章记录</div>
        </section>

        <!-- ========== 个人资料 ========== -->
        <section v-if="activeTab === 'profile'" class="uh-section">
          <div class="uh-section-header"><h2>个人资料</h2></div>

          <div class="uh-profile-layout">
            <div class="uh-profile-card">
              <div class="uh-profile-card__header">
                <span class="uh-profile-name">{{ user.username }}</span>
                <span class="uh-badge uh-badge--blue">{{ user.role }}</span>
              </div>
              <div class="uh-profile-card__body">
                <div class="uh-profile-row"><span>最后登录时间</span><span>{{ fmtTime(user.lastLoginTime) }}</span></div>
                <div class="uh-profile-row"><span>最后登录IP</span><span>{{ user.lastLoginIp || '-' }}</span></div>
              </div>
              <div class="uh-profile-card__footer">
                <router-link to="/forgot-password" class="uh-link">修改密码</router-link>
              </div>
            </div>

            <div class="uh-profile-form" v-if="customer">
              <h4>编辑资料</h4>
              <div class="uh-field">
                <label>姓名</label>
                <input v-model="profileForm.name" class="uh-input" placeholder="姓名" />
              </div>
              <div class="uh-field">
                <label>手机号</label>
                <input :value="customer.phone || user.phone" class="uh-input" readonly />
              </div>
              <div class="uh-field">
                <label>驾驶证号</label>
                <input v-model="profileForm.driverLicenseNumber" class="uh-input" placeholder="驾驶证号" />
              </div>
              <div class="uh-field">
                <label>备注</label>
                <textarea v-model="profileForm.remark" class="uh-input uh-textarea" placeholder="备注"></textarea>
              </div>
              <button class="uh-btn uh-btn--primary" @click="saveProfile" :disabled="profileSaving">
                {{ profileSaving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </section>

      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi, orderApi, reviewApi, vehicleApi, violationApi } from '@/api'

const router = useRouter()
const user = JSON.parse(sessionStorage.getItem('user') || 'null')

if (!user) {
  router.push('/login')
  throw new Error('Not authenticated')
}

// ---------- helpers ----------
function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

function starsHtml(rating) {
  let s = ''
  for (let i = 1; i <= 5; i++) s += i <= (rating || 0) ? '⭐' : '☆'
  return s
}

function toggleReviewDetail(orderId) {
  const next = new Set(expandedReviews.value)
  if (next.has(orderId)) next.delete(orderId)
  else next.add(orderId)
  expandedReviews.value = next
}

const ORDER_STATUS = {
  PENDING: '待取车',
  PICKED_UP: '已取车',
  RETURNED: '已还车',
  CANCELLED: '已取消',
  COMPLETED: '已完成'
}
function orderStatusLabel(s) { return ORDER_STATUS[s] || s }
function orderStatusBadgeCls(s) {
  if (s === 'PENDING') return 'uh-badge--amber'
  if (s === 'PICKED_UP') return 'uh-badge--blue'
  if (s === 'RETURNED' || s === 'COMPLETED') return 'uh-badge--green'
  if (s === 'CANCELLED') return 'uh-badge--red'
  return 'uh-badge--gray'
}

// ---------- tabs ----------
const tabs = [
  { key: 'home', label: '首页' },
  { key: 'orders', label: '我的订单' },
  { key: 'vehicles', label: '浏览车型' },
  { key: 'violations', label: '违章记录' },
  { key: 'profile', label: '个人资料' }
]
const activeTab = ref('home')

// ---------- toast ----------
const toast = reactive({ show: false, msg: '', type: 'success' })
let toastTimer = null
function showToast(msg, type = 'success') {
  clearTimeout(toastTimer)
  toast.msg = msg
  toast.type = type
  toast.show = true
  toastTimer = setTimeout(() => { toast.show = false }, 2500)
}

// ---------- data ----------
const customer = ref(null)
const orders = ref([])
const violations = ref([])
const availableVehicles = ref([])
const orderLoading = ref(false)
const vehicleLoading = ref(false)
const violationLoading = ref(false)

// 评价相关状态
const reviewedOrderIds = ref(new Set())
const reviewMap = reactive({})
const expandedReviews = ref(new Set())

const customerPoints = computed(() => customer.value?.points ?? 0)
const pendingViolations = computed(() => violations.value.filter(v => v.status !== 'RESOLVED').length)

// ---------- load data ----------
async function loadCustomer() {
  try {
    const res = await customerApi.list({ phone: user.phone })
    if (res.code === 200 && res.data?.length > 0) { customer.value = res.data[0] }
  } catch { /* silently fail */ }
}

async function loadOrders() {
  orderLoading.value = true
  try {
    const res = await orderApi.list({ customerName: user.realName })
    orders.value = res.data || []
  } catch { showToast('加载订单失败', 'error') }
  finally { orderLoading.value = false }
}

async function loadReviews() {
  // 清除旧数据
  reviewedOrderIds.value = new Set()
  Object.keys(reviewMap).forEach(k => delete reviewMap[k])
  // 第1步：按客户ID查评价
  if (customer.value?.id) {
    try {
      const res = await reviewApi.byCustomer(customer.value.id)
      if (res.code === 200 && res.data) {
        res.data.forEach(rv => {
          if (rv.orderId) {
            const s = new Set(reviewedOrderIds.value); s.add(rv.orderId); reviewedOrderIds.value = s
            reviewMap[rv.orderId] = rv
          }
        })
      }
    } catch { /* ignore */ }
  }
  // 第2步：兜底——对未命中且已完成的订单，并行查评价详情
  const unchecked = orders.value.filter(o => o.status === 'COMPLETED' && !reviewedOrderIds.value.has(o.id))
  if (unchecked.length > 0) {
    const results = await Promise.all(unchecked.map(o =>
      reviewApi.byOrder(o.id).then(d => {
        if (d.code === 200 && d.data) return d.data
        return null
      }).catch(() => null)
    ))
    results.forEach(rv => {
      if (rv && rv.orderId) {
        const s = new Set(reviewedOrderIds.value); s.add(rv.orderId); reviewedOrderIds.value = s
        reviewMap[rv.orderId] = rv
      }
    })
  }
}

async function loadViolations() {
  violationLoading.value = true
  try {
    const res = await violationApi.list({ customerName: user.realName })
    violations.value = res.data || []
  } catch { showToast('加载违章记录失败', 'error') }
  finally { violationLoading.value = false }
}

const UPLOAD_FILES = [
  '20260714_0ea2c5b3.jpg','20260714_292125e4.jpg','20260714_3c12e78e.jpg',
  '20260714_4f342939.jpg','20260714_58b3de9c.jpg','20260714_5a18ba75.jpg',
  '20260714_6ba438a6.jpg','20260714_826103fa.jpg','20260714_8f852483.jpg',
  '20260714_94293e3d.jpg','20260714_9bfc047b.jpg','20260714_b8a0dfb1.jpg',
  '20260714_c2b7012e.jpg','20260714_d00dd179.jpg','20260714_ec01e985.jpg',
  '20260714_fb841242.jpg'
]

async function loadVehicles() {
  vehicleLoading.value = true
  try {
    const res = await vehicleApi.list()
    availableVehicles.value = (res.data || [])
      .filter(v => v.status === 'AVAILABLE')
      .map(v => {
        const idx = (v.id || 1) % UPLOAD_FILES.length
        return { ...v, imageUrl: `/uploads/vehicles/${UPLOAD_FILES[idx]}` }
      })
  } catch { showToast('加载车辆数据失败', 'error') }
  finally { vehicleLoading.value = false }
}

async function fetchAll() {
  await Promise.all([loadCustomer(), loadOrders(), loadViolations()])
  await loadReviews()
}

// ---------- logout ----------
function handleLogout() {
  sessionStorage.removeItem('user')
  router.push('/login')
}

// ---------- 我的订单 ----------
const cancelDialog = reactive({ show: false, order: null, reason: '', loading: false })

function cancelOrder(o) {
  cancelDialog.order = o
  cancelDialog.reason = ''
  cancelDialog.loading = false
  cancelDialog.show = true
}

async function confirmCancel() {
  if (!cancelDialog.order) return
  cancelDialog.loading = true
  try {
    const res = await orderApi.cancel(cancelDialog.order.id, cancelDialog.reason)
    if (res.code === 200) { showToast('订单已取消'); cancelDialog.show = false; await loadOrders() }
    else { showToast(res.msg || '取消失败', 'error') }
  } catch { showToast('取消失败', 'error') }
  finally { cancelDialog.loading = false }
}

function goReview(o) {
  router.push(`/reviews?action=write&orderId=${o.id}&vehicleId=${o.vehicleId}&customerId=${customer.value?.id || ''}`)
}

// ---------- 订单详情 ----------
const detailDialog = reactive({ show: false, order: null })
function showOrderDetail(o) { detailDialog.order = o; detailDialog.show = true }

// ---------- 浏览车型 / 租车 ----------
const rentDialog = reactive({ show: false, vehicle: null, pickupTime: '', returnTime: '', loading: false })

const rentCost = computed(() => {
  const v = rentDialog.vehicle
  if (!v || !rentDialog.pickupTime || !rentDialog.returnTime) return { days: 0, totalAmount: 0, deposit: 0 }
  const start = new Date(rentDialog.pickupTime)
  const end = new Date(rentDialog.returnTime)
  if (end <= start) return { days: 0, totalAmount: 0, deposit: 0 }
  const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
  const totalAmount = days * v.dailyPrice
  const deposit = Math.round(totalAmount * 0.3)
  return { days, totalAmount, deposit }
})

function openRentModal(vehicle) {
  rentDialog.vehicle = vehicle
  rentDialog.pickupTime = ''
  rentDialog.returnTime = ''
  rentDialog.loading = false
  rentDialog.show = true
}

async function confirmRent() {
  if (!customer.value?.id) { showToast('无法获取客户信息', 'error'); return }
  if (!rentDialog.pickupTime || !rentDialog.returnTime) { showToast('请填写取车和还车时间', 'error'); return }
  if (rentDialog.pickupTime >= rentDialog.returnTime) { showToast('还车时间必须晚于取车时间', 'error'); return }
  rentDialog.loading = true
  try {
    const res = await orderApi.create({
      customerId: customer.value.id,
      vehicleId: rentDialog.vehicle.id,
      pickupTime: rentDialog.pickupTime,
      returnTime: rentDialog.returnTime
    })
    if (res.code === 200) { showToast('下单成功'); rentDialog.show = false; await loadOrders() }
    else { showToast(res.msg || '下单失败', 'error') }
  } catch { showToast('下单失败', 'error') }
  finally { rentDialog.loading = false }
}

// ---------- 个人资料 ----------
const profileSaving = ref(false)
const profileForm = reactive({ name: '', driverLicenseNumber: '', remark: '' })

function initProfileForm() {
  if (customer.value) {
    profileForm.name = customer.value.name || ''
    profileForm.driverLicenseNumber = customer.value.driverLicenseNumber || ''
    profileForm.remark = customer.value.remark || ''
  }
}

async function saveProfile() {
  if (!customer.value?.id) { showToast('无法获取客户信息', 'error'); return }
  profileSaving.value = true
  try {
    const res = await customerApi.edit(customer.value.id, {
      name: profileForm.name,
      driverLicenseNumber: profileForm.driverLicenseNumber,
      remark: profileForm.remark
    })
    if (res.code === 200) {
      showToast('保存成功')
      await loadCustomer()
      initProfileForm()
    } else { showToast(res.msg || '保存失败', 'error') }
  } catch { showToast('保存失败', 'error') }
  finally { profileSaving.value = false }
}

// ---------- mount ----------
onMounted(() => {
  fetchAll()
})
</script>

<style scoped>
/* ===== Toast ===== */
.uh-toast {
  position: fixed; top: 24px; right: 24px; z-index: 9999;
  padding: 12px 24px; border-radius: 8px; color: #fff; font-size: 14px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  animation: uh-toast-in 0.35s ease-out;
}
@keyframes uh-toast-in { from { opacity: 0; transform: translateX(100%); } to { opacity: 1; transform: translateX(0); } }
.uh-toast--success { background: #52c41a; }
.uh-toast--error { background: #ff4d4f; }

/* ===== Header ===== */
.uh-header { position: sticky; top: 0; z-index: 900; background: #fff; border-bottom: 1px solid #f0f0f0; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.uh-header__inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; height: 56px; gap: 16px; }
.uh-header__brand { font-size: 20px; font-weight: 800; color: #1a1a1a; white-space: nowrap; }
.uh-header__tabs { display: flex; gap: 4px; }
.uh-header__tab { padding: 6px 14px; border-radius: 6px; border: none; background: transparent; color: #666; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s; white-space: nowrap; font-family: inherit; }
.uh-header__tab:hover { background: #f0f5ff; color: #4096ff; }
.uh-header__tab--active { background: #e6f0ff; color: #1677ff; }
.uh-header__right { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.uh-header__username { font-size: 14px; font-weight: 600; color: #333; }
.uh-header__logout { padding: 6px 14px; border-radius: 6px; border: 1px solid #d9d9d9; background: #fff; color: #666; font-size: 13px; cursor: pointer; transition: all 0.2s; font-family: inherit; white-space: nowrap; }
.uh-header__logout:hover { border-color: #ff4d4f; color: #ff4d4f; }

/* ===== Main ===== */
.uh-main { flex: 1; padding: 24px; min-height: calc(100vh - 56px); background: #f5f7fa; }
.uh-container { max-width: 1140px; margin: 0 auto; }
.uh-section { animation: uh-fade 0.25s ease; }
@keyframes uh-fade { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
.uh-section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.uh-section-header h2 { font-size: 20px; font-weight: 700; color: #1a1a1a; margin: 0; }
.uh-section-header span { font-size: 13px; color: #999; }
.uh-loading { text-align: center; padding: 60px 0; color: #999; font-size: 15px; }
.uh-empty { text-align: center; padding: 60px 0; color: #999; font-size: 14px; }

/* ===== Hero ===== */
.uh-hero { display: flex; align-items: center; justify-content: space-between; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; padding: 32px 36px; margin-bottom: 24px; }
.uh-hero__text h1 { font-size: 22px; color: #fff; margin: 0; font-weight: 600; }
.uh-hero__points { text-align: center; background: rgba(255,255,255,0.15); border-radius: 10px; padding: 14px 24px; }
.uh-hero__points-label { display: block; font-size: 12px; color: rgba(255,255,255,0.6); margin-bottom: 4px; }
.uh-hero__points-value { font-size: 32px; font-weight: 800; color: #ffd666; line-height: 1; }

/* ===== Stat Cards ===== */
.uh-stat-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.uh-stat-card { background: #fff; border-radius: 10px; padding: 20px 24px; display: flex; align-items: center; gap: 16px; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.uh-stat-card__icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.uh-stat-card__icon--blue { background: #e6f0ff; color: #4096ff; }
.uh-stat-card__icon--red { background: #fff1f0; color: #ff4d4f; }
.uh-stat-card__icon--amber { background: #fff7e6; color: #fa8c16; }
.uh-stat-card__info { display: flex; flex-direction: column; }
.uh-stat-card__value { font-size: 26px; font-weight: 800; color: #1a1a1a; line-height: 1.2; }
.uh-stat-card__label { font-size: 13px; color: #888; }

/* ===== Quick Actions ===== */
.uh-quick-actions { display: flex; gap: 12px; }

/* ===== Table ===== */
.uh-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.uh-table th, .uh-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.uh-table th { background: #fafafa; font-weight: 600; color: #333; }
.uh-table tbody tr { cursor: pointer; }
.uh-table tbody tr:hover td { background: #fafafa; }
.uh-points { color: #ff4d4f; font-weight: 700; }

/* ===== Badges ===== */
.uh-badge { display: inline-block; padding: 3px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: #fff; }
.uh-badge--amber { background: #fa8c16; }
.uh-badge--blue { background: #1677ff; }
.uh-badge--red { background: #ff4d4f; }
.uh-badge--green { background: #52c41a; }
.uh-badge--gray { background: #8c8c8c; }

/* ===== Buttons ===== */
.uh-btn { padding: 8px 18px; border: none; border-radius: 6px; font-size: 14px; cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; gap: 4px; font-family: inherit; }
.uh-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.uh-btn--primary { background: #4096ff; color: #fff; }
.uh-btn--primary:hover:not(:disabled) { background: #1677ff; }
.uh-btn--outline { background: #fff; color: #333; border: 1px solid #d9d9d9; }
.uh-btn--outline:hover:not(:disabled) { border-color: #4096ff; color: #4096ff; }
.uh-btn--danger { background: #ff4d4f; color: #fff; }
.uh-btn--danger:hover:not(:disabled) { background: #e04345; }
.uh-btn--warning { background: #fa8c16; color: #fff; }
.uh-btn--warning:hover:not(:disabled) { background: #d87a0d; }
.uh-btn--accent { background: #ff6b35; color: #fff; }
.uh-btn--accent:hover:not(:disabled) { background: #e55a2b; }
.uh-btn--sm { padding: 5px 12px; font-size: 12px; }
.uh-actions { display: flex; gap: 6px; flex-wrap: wrap; }

/* ===== Vehicle Grid ===== */
.uh-vehicle-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; }
.uh-vehicle-card { background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,0.06); transition: box-shadow 0.2s; }
.uh-vehicle-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.1); }
.uh-vehicle-card__img { width: 100%; height: 180px; overflow: hidden; background: #f5f5f5; display: flex; align-items: center; justify-content: center; font-size: 52px; }
.uh-vehicle-card__img img { width: 100%; height: 100%; object-fit: cover; }
.uh-vehicle-card__body { padding: 16px; }
.uh-vehicle-card__body h3 { margin: 0 0 6px; font-size: 16px; font-weight: 600; color: #1a1a1a; }
.uh-vehicle-card__meta { display: flex; flex-wrap: wrap; gap: 6px; font-size: 12px; color: #888; margin-bottom: 12px; }
.uh-vehicle-card__meta span { background: #f5f5f5; padding: 2px 8px; border-radius: 4px; }
.uh-vehicle-card__footer { display: flex; align-items: center; justify-content: space-between; }
.uh-vehicle-card__price { font-size: 20px; font-weight: 700; color: #ff4d4f; }
.uh-vehicle-card__price small { font-size: 12px; font-weight: 400; color: #999; }

/* ===== Modal ===== */
.uh-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.uh-modal { background: #fff; border-radius: 12px; width: 520px; max-height: 85vh; overflow-y: auto; box-shadow: 0 8px 30px rgba(0,0,0,0.18); }
.uh-modal__header { display: flex; justify-content: space-between; align-items: center; padding: 18px 24px; border-bottom: 1px solid #f0f0f0; }
.uh-modal__header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a1a; }
.uh-modal__close { background: none; border: none; font-size: 22px; cursor: pointer; color: #999; line-height: 1; padding: 0; }
.uh-modal__close:hover { color: #333; }
.uh-modal__body { padding: 20px 24px; }
.uh-modal__footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px; border-top: 1px solid #f0f0f0; }

/* ===== Form Fields ===== */
.uh-field { margin-bottom: 14px; }
.uh-field label { display: block; margin-bottom: 4px; font-size: 13px; font-weight: 500; color: #333; }
.uh-required { color: #ff4d4f; }
.uh-input { width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px; font-size: 14px; outline: none; box-sizing: border-box; transition: border-color 0.2s; font-family: inherit; }
.uh-input:focus { border-color: #4096ff; }
.uh-input[readonly] { background: #f5f5f5; color: #888; cursor: not-allowed; }
.uh-textarea { min-height: 80px; resize: vertical; }

/* ===== Rent Info & Cost ===== */
.uh-rent-info { background: #f9fafb; padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; display: flex; flex-direction: column; gap: 4px; }
.uh-cost-summary { background: #f9fafb; padding: 14px 16px; border-radius: 8px; margin-top: 8px; }
.uh-cost-row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 14px; color: #555; }
.uh-cost-row--total { border-top: 1px solid #e8e8e8; margin-top: 6px; padding-top: 10px; font-weight: 700; font-size: 16px; color: #1a1a1a; }
.uh-cost-hint { padding: 10px 14px; background: #f9fafb; border-radius: 8px; margin-top: 8px; font-size: 13px; color: #999; }

/* ===== Detail Grid ===== */
.uh-detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.uh-detail-item { display: flex; flex-direction: column; gap: 2px; font-size: 14px; }
.uh-detail-label { font-size: 12px; color: #888; }

/* ===== Profile ===== */
.uh-profile-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.uh-profile-card { background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.uh-profile-card__header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid #f0f0f0; }
.uh-profile-name { font-size: 18px; font-weight: 600; color: #1a1a1a; }
.uh-profile-card__body { padding: 16px 24px; }
.uh-profile-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; }
.uh-profile-row span:first-child { color: #888; }
.uh-profile-row span:last-child { color: #333; }
.uh-profile-card__footer { padding: 12px 24px; border-top: 1px solid #f0f0f0; }
.uh-link { color: #4096ff; text-decoration: none; font-size: 14px; }
.uh-link:hover { color: #1677ff; }
.uh-profile-form { background: #fff; border-radius: 10px; padding: 24px; box-shadow: 0 1px 6px rgba(0,0,0,0.06); }
.uh-profile-form h4 { margin: 0 0 16px; font-size: 16px; font-weight: 600; color: #1a1a1a; }

/* ===== Review Detail Row ===== */
.review-toggle-btn {
  background: #fff !important;
  color: #16a34a !important;
  border: 1px solid #16a34a !important;
}
.review-toggle-btn:hover { background: #f0fdf4 !important; }
.review-detail-cell {
  background: #f0fdf4;
  border-left: 3px solid #16a34a;
  margin: 0;
  padding: 12px 16px;
  border-radius: 0 6px 6px 0;
}
.review-detail-cell__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.review-detail-cell__stars { font-size: 1rem; }
.review-detail-cell__time { font-size: 12px; color: #666; }
.review-detail-cell__content { margin: 0; line-height: 1.6; color: #333; }

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .uh-stat-grid { grid-template-columns: 1fr; }
  .uh-profile-layout { grid-template-columns: 1fr; }
  .uh-detail-grid { grid-template-columns: 1fr; }
  .uh-hero { flex-direction: column; gap: 16px; text-align: center; }
  .uh-header__tabs { display: none; }
  .uh-header__inner { padding: 0 12px; }
  .uh-main { padding: 16px; }
}
</style>
