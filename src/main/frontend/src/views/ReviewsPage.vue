<template>
  <div class="reviews-page">
    <!-- ==================== Navbar ==================== -->
    <nav class="navbar" :class="{ 'navbar--scrolled': scrolled }">
      <div class="navbar__inner">
        <router-link to="/" class="navbar__brand">
          <i class="fa-solid fa-car navbar__logo-icon"></i>
          <span class="navbar__brand-text">爱租车</span>
        </router-link>

        <ul class="navbar__links">
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/">车型</router-link></li>
          <li><router-link to="/reviews">用户评价</router-link></li>
          <li><router-link to="/about">关于</router-link></li>
          <li><router-link to="/contact">联系</router-link></li>
        </ul>

        <div class="navbar__actions">
          <router-link to="/login" class="navbar__btn navbar__btn--login">登录</router-link>
          <router-link to="/register" class="navbar__btn navbar__btn--register">免费注册</router-link>
        </div>

        <button class="navbar__hamburger" aria-label="Toggle menu">
          <span></span><span></span><span></span>
        </button>
      </div>
    </nav>

    <!-- ==================== Hero ==================== -->
    <section class="hero">
      <div class="hero__inner">
        <h1 class="hero__title">用户评价</h1>
        <p class="hero__subtitle">来自真实用户的反馈</p>
      </div>
    </section>

    <!-- ==================== Reviews Grid ==================== -->
    <section class="reviews-section">
      <div v-if="loading" class="reviews-loading">
        <i class="fa-solid fa-spinner fa-spin"></i>
        <span>加载评价中...</span>
      </div>

      <div v-else-if="loadError" class="reviews-error">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <span>{{ loadError }}</span>
        <button class="btn btn-primary" @click="fetchReviews">重试</button>
      </div>

      <div v-else-if="reviews.length === 0" class="reviews-empty">
        <i class="fa-solid fa-comment-slash"></i>
        <span>暂无用户评价</span>
      </div>

      <div v-else class="reviews__grid">
        <div
          v-for="review in reviews"
          :key="review.id"
          class="review-card"
        >
          <!-- Stars: ★ filled gold, ☆ remaining -->
          <div class="review-card__stars">
            <template v-for="n in 5" :key="n">
              <span v-if="n <= review.rating" class="review-card__star review-card__star--filled">★</span>
              <span v-else class="review-card__star review-card__star--empty">☆</span>
            </template>
          </div>

          <!-- Content (italic) -->
          <p class="review-card__content">{{ review.content }}</p>

          <!-- Author row -->
          <div class="review-card__author">
            <div class="review-card__avatar" :style="{ background: avatarColor(review.customerName) }">
              {{ review.customerName ? review.customerName.charAt(0) : '?' }}
            </div>
            <div class="review-card__author-info">
              <span class="review-card__name">{{ review.customerName }}</span>
              <span
                v-if="review.vehicleBrand || review.vehicleModel"
                class="badge badge-blue review-card__vehicle-tag"
              >
                {{ [review.vehicleBrand, review.vehicleModel].filter(Boolean).join(' ') }}
              </span>
            </div>
          </div>

          <!-- Meta -->
          <div class="review-card__meta">
            <span class="review-card__date">
              <i class="fa-regular fa-calendar"></i>
              {{ formatDate(review.createTime) }}
            </span>
            <span class="review-card__likes" @click.stop="handleLike(review)">
              <span class="review-card__heart" :class="{ 'review-card__heart--liked': likedSet.has(review.id) }">❤️</span>
              {{ review.likes || 0 }} 赞
            </span>
            <span class="review-card__views">
              👁 {{ review.views || 0 }} 浏览
            </span>
          </div>
        </div>
      </div>
    </section>

    <!-- ==================== Write Review Modal ==================== -->
    <div v-if="showModal" class="modal-mask" @click.self="closeModal">
      <div class="modal-box review-modal">
        <h3>撰写评价</h3>

        <!-- Vehicle info (readonly) -->
        <div class="review-modal__info">
          <div class="review-modal__info-item">
            <span class="review-modal__info-label">车辆信息</span>
            <span class="review-modal__info-value">{{ modalBrand }} {{ modalModel }}</span>
          </div>
          <div class="review-modal__info-item">
            <span class="review-modal__info-label">订单编号</span>
            <span class="review-modal__info-value">{{ modalOrderNo }}</span>
          </div>
        </div>

        <!-- Star rating: 5 interactive stars -->
        <div class="review-modal__rating">
          <span class="review-modal__rating-label">评分</span>
          <div class="review-modal__stars">
            <i
              v-for="n in 5"
              :key="n"
              class="fa-solid"
              :class="n <= (hoverStar || rating) ? 'fa-star review-modal__star--active' : 'fa-star review-modal__star--inactive'"
              @click="rating = n"
              @mouseenter="hoverStar = n"
              @mouseleave="hoverStar = 0"
            ></i>
          </div>
          <span v-if="rating > 0" class="review-modal__rating-text">{{ rating }} 星</span>
        </div>

        <!-- Content textarea (required) -->
        <div class="review-modal__content">
          <label class="review-modal__content-label">评价内容 <span class="review-modal__required">*</span></label>
          <textarea
            v-model="reviewContent"
            class="form-textarea review-modal__textarea"
            placeholder="请分享您的用车体验..."
            maxlength="500"
          ></textarea>
          <span class="review-modal__char-count">{{ reviewContent.length }}/500</span>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button class="btn btn-ghost" @click="closeModal">取消</button>
          <button
            class="btn btn-accent"
            :disabled="submitting || !rating || !reviewContent.trim()"
            @click="submitReview"
          >
            <i v-if="submitting" class="fa-solid fa-spinner fa-spin"></i>
            {{ submitting ? '提交中...' : '提交评价' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ==================== Footer ==================== -->
    <footer class="footer">
      <div class="footer__inner">
        <div class="footer__grid">
          <!-- Col 1: Brand -->
          <div class="footer__col">
            <div class="footer__brand">
              <i class="fa-solid fa-car footer__brand-icon"></i>
              <span class="footer__brand-text">爱租车</span>
            </div>
            <p class="footer__desc">专业汽车租赁服务平台</p>
            <div class="footer__contact">
              <div class="footer__contact-item">
                <i class="fa-solid fa-phone footer__contact-icon"></i>
                <span>400-888-6688</span>
              </div>
              <div class="footer__contact-item">
                <i class="fa-solid fa-envelope footer__contact-icon"></i>
                <span>service@aizuche.com</span>
              </div>
            </div>
          </div>

          <!-- Col 2: 快速链接 -->
          <div class="footer__col">
            <h4 class="footer__heading">快速链接</h4>
            <ul class="footer__links">
              <li><router-link to="/" class="footer__link">首页</router-link></li>
              <li><router-link to="/" class="footer__link">服务特色</router-link></li>
              <li><router-link to="/" class="footer__link">车型展示</router-link></li>
              <li><a href="#" class="footer__link">租车流程</a></li>
            </ul>
          </div>

          <!-- Col 3: 服务支持 -->
          <div class="footer__col">
            <h4 class="footer__heading">服务支持</h4>
            <ul class="footer__links">
              <li><a href="#" class="footer__link">常见问题</a></li>
              <li><a href="#" class="footer__link">租车须知</a></li>
              <li><a href="#" class="footer__link">保险说明</a></li>
              <li><a href="#" class="footer__link">违章处理</a></li>
            </ul>
          </div>

          <!-- Col 4: 关于我们 -->
          <div class="footer__col">
            <h4 class="footer__heading">关于我们</h4>
            <ul class="footer__links">
              <li><router-link to="/about" class="footer__link">公司简介</router-link></li>
              <li><router-link to="/contact" class="footer__link">联系方式</router-link></li>
              <li><a href="#" class="footer__link">加入我们</a></li>
              <li><a href="#" class="footer__link">用户协议</a></li>
            </ul>
          </div>
        </div>

        <div class="footer__bottom">
          <p class="footer__copyright">&copy; 2026 爱租车汽车租赁服务平台 &middot; 粤ICP备XXXXXXXX号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { reviewApi } from '@/api'

const route = useRoute()
const router = useRouter()

/* -----------------------------------------------
   Navbar scroll
----------------------------------------------- */
const scrolled = ref(false)

function handleScroll() {
  scrolled.value = window.scrollY > 10
}

/* -----------------------------------------------
   Reviews data
----------------------------------------------- */
const reviews = ref([])
const loading = ref(false)
const loadError = ref('')
const renderedCardIds = ref(new Set())

const AVATAR_COLORS = [
  '#2563eb', '#7c3aed', '#db2777', '#16a34a',
  '#d97706', '#0ea5e9', '#dc2626', '#059669'
]

function avatarColor(name) {
  if (!name) return AVATAR_COLORS[0]
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

async function fetchReviews() {
  loading.value = true
  loadError.value = ''
  try {
    const res = await reviewApi.popular()
    if (res.code === 200 && res.data) {
      reviews.value = res.data
      nextTick(() => autoViewAll(res.data))
    } else {
      loadError.value = res.msg || '加载评价失败'
    }
  } catch {
    loadError.value = '网络请求失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

/* -----------------------------------------------
   Like: localStorage `liked_${id}` to prevent double-like
----------------------------------------------- */
const likedSet = computed(() => {
  const s = new Set()
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i)
    if (key && key.startsWith('liked_') && localStorage.getItem(key) === 'true') {
      s.add(key.replace('liked_', ''))
    }
  }
  return s
})

async function handleLike(review) {
  if (likedSet.value.has(String(review.id))) return
  try {
    const res = await reviewApi.like(review.id)
    if (res.code === 200) {
      localStorage.setItem(`liked_${review.id}`, 'true')
      review.likes = (review.likes || 0) + 1
    }
  } catch {
    // Fire and forget - silently fail
  }
}

/* -----------------------------------------------
   Auto view (fire and forget when card renders)
----------------------------------------------- */
function autoViewAll(reviewList) {
  reviewList.forEach((review) => {
    if (!renderedCardIds.value.has(review.id)) {
      renderedCardIds.value.add(review.id)
      reviewApi.view(review.id).catch(() => {})
    }
  })
}

/* -----------------------------------------------
   Write Review Modal
----------------------------------------------- */
const showModal = ref(false)
const rating = ref(0)
const hoverStar = ref(0)
const reviewContent = ref('')
const submitting = ref(false)

const modalOrderId = ref('')
const modalOrderNo = ref('')
const modalVehicleId = ref('')
const modalBrand = ref('')
const modalModel = ref('')

function openModalFromQuery() {
  const q = route.query
  if (q.action === 'write') {
    modalOrderId.value = q.orderId || ''
    modalOrderNo.value = q.orderNo || ''
    modalVehicleId.value = q.vehicleId || ''
    modalBrand.value = q.brand || ''
    modalModel.value = q.model || ''
    showModal.value = true
  }
}

function closeModal() {
  showModal.value = false
  rating.value = 0
  hoverStar.value = 0
  reviewContent.value = ''
  submitting.value = false

  // Clear query params
  if (route.query.action) {
    router.replace({ query: {} })
  }
}

async function submitReview() {
  if (!rating.value || !reviewContent.value.trim()) return

  let customerName = '匿名用户'
  try {
    const stored = sessionStorage.getItem('user')
    if (stored) {
      const user = JSON.parse(stored)
      if (user) {
        customerName = user.customerName || user.username || user.realName || user.name || '匿名用户'
      }
    }
  } catch {
    // Use default
  }

  submitting.value = true
  try {
    const res = await reviewApi.add({
      orderId: modalOrderId.value,
      orderNo: modalOrderNo.value,
      vehicleId: modalVehicleId.value,
      vehicleBrand: modalBrand.value,
      vehicleModel: modalModel.value,
      customerName,
      rating: rating.value,
      content: reviewContent.value.trim()
    })

    if (res.code === 200) {
      closeModal()
      await fetchReviews()
    } else {
      alert(res.msg || '提交失败，请稍后重试')
    }
  } catch {
    alert('提交失败，网络异常')
  } finally {
    submitting.value = false
  }
}

/* -----------------------------------------------
   Watch route query changes (for modal reopening)
----------------------------------------------- */
watch(() => route.query, (newQuery) => {
  if (newQuery.action === 'write') {
    openModalFromQuery()
  } else if (!newQuery.action) {
    showModal.value = false
  }
})

/* -----------------------------------------------
   Lifecycle
----------------------------------------------- */
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()
  fetchReviews()
  openModalFromQuery()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ================================================
   PAGE WRAPPER
================================================ */
.reviews-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg, #f1f5f9);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
}

/* ================================================
   NAVBAR (white glass-morphism)
================================================ */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px) saturate(160%);
  -webkit-backdrop-filter: blur(16px) saturate(160%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: background 0.3s ease, box-shadow 0.3s ease;
}

.navbar--scrolled {
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
}

.navbar__inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.navbar__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: var(--dark, #0f172a);
  flex-shrink: 0;
}

.navbar__logo-icon {
  font-size: 24px;
  color: var(--accent, #f59e0b);
}

.navbar__brand-text {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.navbar__links {
  display: flex;
  list-style: none;
  gap: 32px;
  margin: 0;
  padding: 0;
  margin-left: 48px;
}

.navbar__links a {
  text-decoration: none;
  color: var(--slate, #334155);
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s ease;
  position: relative;
}

.navbar__links a::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent, #f59e0b);
  border-radius: 1px;
  transition: width 0.25s ease;
}

.navbar__links a:hover {
  color: var(--dark, #0f172a);
}

.navbar__links a:hover::after,
.navbar__links a.router-link-active::after {
  width: 100%;
}

.navbar__links a.router-link-active {
  color: var(--accent, #f59e0b);
}

.navbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.navbar__btn {
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.navbar__btn--login {
  color: var(--slate, #334155);
  background: transparent;
}

.navbar__btn--login:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--dark, #0f172a);
}

.navbar__btn--register {
  color: #ffffff;
  background: var(--accent, #f59e0b);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.35);
}

.navbar__btn--register:hover {
  background: var(--accent-dark, #d97706);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.5);
}

.navbar__hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  margin-left: auto;
}

.navbar__hamburger span {
  display: block;
  width: 24px;
  height: 2px;
  background: var(--slate, #334155);
  border-radius: 2px;
  transition: transform 0.25s ease, opacity 0.25s ease;
}

/* ================================================
   HERO (gray bg)
================================================ */
.hero {
  margin-top: 64px;
  background: var(--gray-200, #e2e8f0);
  padding: 60px 24px;
  text-align: center;
}

.hero__inner {
  max-width: 1200px;
  margin: 0 auto;
}

.hero__title {
  font-size: 2.25rem;
  font-weight: 800;
  color: var(--dark, #0f172a);
  margin: 0 0 8px;
  letter-spacing: -0.01em;
}

.hero__subtitle {
  font-size: 1.0625rem;
  color: var(--text-secondary, #64748b);
  margin: 0;
}

/* ================================================
   REVIEWS SECTION
================================================ */
.reviews-section {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 48px 24px 64px;
}

.reviews__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.reviews-loading,
.reviews-error,
.reviews-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 80px 20px;
  color: var(--text-muted, #94a3b8);
  font-size: 1rem;
}

.reviews-loading i {
  font-size: 2rem;
  color: var(--primary, #2563eb);
}

.reviews-error i {
  font-size: 2.5rem;
  color: var(--warning, #d97706);
}

.reviews-error span {
  margin-bottom: 8px;
}

.reviews-empty i {
  font-size: 2.5rem;
}

/* ================================================
   REVIEW CARD
================================================ */
.review-card {
  background: var(--card-bg, #fff);
  border-radius: var(--radius-lg, 14px);
  padding: 28px;
  border: 1px solid var(--border, #e2e8f0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg, 0 10px 30px rgba(0, 0, 0, 0.12));
}

/* Stars: ★ filled gold, ☆ remaining gray */
.review-card__stars {
  display: flex;
  gap: 2px;
}

.review-card__star {
  font-size: 1.125rem;
  line-height: 1;
}

.review-card__star--filled {
  color: #f59e0b;
}

.review-card__star--empty {
  color: var(--gray-300, #cbd5e1);
}

/* Content (italic) */
.review-card__content {
  font-size: 0.9375rem;
  line-height: 1.7;
  color: var(--text, #1e293b);
  margin: 0;
  font-style: italic;
  flex: 1;
}

/* Author */
.review-card__author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.review-card__avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  flex-shrink: 0;
}

.review-card__author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.review-card__name {
  font-weight: 700;
  color: var(--dark, #0f172a);
  font-size: 0.9375rem;
}

.review-card__vehicle-tag {
  align-self: flex-start;
}

/* Meta: date, ❤️ likes, 👁 views */
.review-card__meta {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--border, #e2e8f0);
  font-size: 0.8125rem;
  color: var(--text-muted, #94a3b8);
  flex-wrap: wrap;
}

.review-card__date i {
  margin-right: 4px;
}

.review-card__likes {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  user-select: none;
}

.review-card__heart {
  transition: transform 0.15s ease;
  display: inline-block;
}

.review-card__heart--liked {
  animation: heartPop 0.3s ease;
}

.review-card__likes:hover .review-card__heart {
  transform: scale(1.2);
}

@keyframes heartPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.35); }
  100% { transform: scale(1); }
}

/* ================================================
   WRITE REVIEW MODAL
================================================ */
.review-modal {
  max-width: 520px;
}

.review-modal h3 {
  margin-bottom: 20px;
}

.review-modal__info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px 16px;
  background: var(--gray-50, #f8fafc);
  border-radius: var(--radius-sm, 6px);
  margin-bottom: 20px;
}

.review-modal__info-item {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
}

.review-modal__info-label {
  color: var(--text-muted, #94a3b8);
  font-weight: 500;
}

.review-modal__info-value {
  color: var(--text, #1e293b);
  font-weight: 600;
}

/* Star rating */
.review-modal__rating {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.review-modal__rating-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--text, #1e293b);
}

.review-modal__stars {
  display: flex;
  gap: 6px;
}

.review-modal__stars i {
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.1s ease, transform 0.15s ease;
}

.review-modal__stars i:hover {
  transform: scale(1.15);
}

.review-modal__star--active {
  color: #f59e0b;
}

.review-modal__star--inactive {
  color: var(--gray-300, #cbd5e1);
}

.review-modal__rating-text {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--accent, #f59e0b);
}

/* Content textarea */
.review-modal__content {
  margin-bottom: 4px;
}

.review-modal__content-label {
  display: block;
  font-size: 0.8125rem;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--text, #1e293b);
}

.review-modal__required {
  color: var(--danger, #dc2626);
}

.review-modal__textarea {
  min-height: 120px;
}

.review-modal__char-count {
  display: block;
  text-align: right;
  font-size: 0.75rem;
  color: var(--text-muted, #94a3b8);
  margin-top: 4px;
}

/* ================================================
   FOOTER (dark bg, 4-column grid, copyright)
================================================ */
.footer {
  background: var(--dark, #0f172a);
  color: var(--gray-400, #94a3b8);
  margin-top: auto;
}

.footer__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 24px 0;
}

.footer__grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1fr;
  gap: 40px;
  padding-bottom: 40px;
}

.footer__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.footer__brand-icon {
  font-size: 28px;
  color: var(--accent, #f59e0b);
}

.footer__brand-text {
  font-size: 22px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 0.5px;
}

.footer__desc {
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px;
}

.footer__contact {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer__contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.footer__contact-icon {
  font-size: 14px;
  color: var(--accent, #f59e0b);
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.footer__heading {
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 16px;
  position: relative;
  padding-bottom: 10px;
}

.footer__heading::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 28px;
  height: 2px;
  background: var(--accent, #f59e0b);
  border-radius: 1px;
}

.footer__links {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer__link {
  text-decoration: none;
  color: var(--gray-400, #94a3b8);
  font-size: 14px;
  transition: color 0.2s ease, padding-left 0.2s ease;
  display: inline-block;
}

.footer__link:hover {
  color: #ffffff;
  padding-left: 6px;
}

.footer__bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px 0;
  text-align: center;
}

.footer__copyright {
  margin: 0;
  font-size: 13px;
}

/* ================================================
   RESPONSIVE
================================================ */
@media (max-width: 1024px) {
  .reviews__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer__grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 768px) {
  .navbar__links,
  .navbar__actions {
    display: none;
  }

  .navbar__hamburger {
    display: flex;
  }

  .reviews__grid {
    grid-template-columns: 1fr;
    max-width: 460px;
    margin: 0 auto;
  }

  .hero {
    padding: 48px 20px;
  }

  .hero__title {
    font-size: 1.75rem;
  }

  .reviews-section {
    padding: 32px 16px 48px;
  }

  .review-modal {
    width: 95%;
    padding: 20px;
  }
}

@media (max-width: 540px) {
  .footer__grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .footer__brand {
    justify-content: center;
  }

  .footer__desc {
    text-align: center;
  }

  .footer__contact {
    align-items: center;
  }

  .footer__col {
    text-align: center;
  }

  .footer__heading::after {
    left: 50%;
    transform: translateX(-50%);
  }

  .footer__links {
    align-items: center;
  }
}
</style>
