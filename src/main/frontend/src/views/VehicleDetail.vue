<template>
  <div class="vd-page">
    <!-- ==================== Navbar ==================== -->
    <nav class="vd-navbar" :class="{ 'vd-navbar--scrolled': scrolled }">
      <div class="vd-navbar__inner">
        <router-link to="/" class="vd-navbar__brand">
          <i class="fa-solid fa-car vd-navbar__logo-icon"></i>
          <span class="vd-navbar__brand-text">爱租车</span>
        </router-link>

        <ul class="vd-navbar__links">
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/">车型</router-link></li>
          <li><router-link to="/reviews">用户评价</router-link></li>
          <li><router-link to="/about">关于</router-link></li>
          <li><router-link to="/contact">联系</router-link></li>
        </ul>

        <div class="vd-navbar__actions">
          <router-link to="/login" class="vd-navbar__btn vd-navbar__btn--login">登录</router-link>
          <router-link to="/register" class="vd-navbar__btn vd-navbar__btn--register">免费注册</router-link>
        </div>

        <button class="vd-navbar__hamburger" aria-label="Toggle menu">
          <span></span><span></span><span></span>
        </button>
      </div>
    </nav>

    <!-- ==================== Main Content ==================== -->
    <main class="vd-main">
      <div class="vd-container">
        <!-- Loading State -->
        <div v-if="loading" class="vd-loading">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <p>加载中...</p>
        </div>

        <!-- Error / Not Found State -->
        <div v-else-if="error" class="vd-error">
          <div class="vd-error__card">
            <i class="fa-solid fa-circle-exclamation vd-error__icon"></i>
            <h2>车辆不存在</h2>
            <p>未找到该车辆信息，可能已被下架或删除</p>
            <router-link to="/" class="btn btn-primary">返回首页</router-link>
          </div>
        </div>

        <!-- Vehicle Detail -->
        <template v-else-if="vehicle">
          <!-- Breadcrumb -->
          <div class="vd-breadcrumb">
            <router-link to="/">首页</router-link>
            <i class="fa-solid fa-chevron-right"></i>
            <router-link to="/">车型</router-link>
            <i class="fa-solid fa-chevron-right"></i>
            <span>{{ vehicle.brand }} {{ vehicle.model }}</span>
          </div>

          <!-- Two-column layout -->
          <div class="vd-detail-layout">
            <!-- Left: Image (larger) -->
            <div class="vd-image-section">
              <img
                v-if="vehicleImage"
                :src="vehicleImage"
                :alt="vehicle.model || '车辆图片'"
                class="vd-image"
              />
              <div v-else class="vd-image-fallback">
                <span class="vd-fallback-emoji">🚗</span>
              </div>
            </div>

            <!-- Right: Info card -->
            <div class="vd-info-section">
              <div class="vd-info-card">
                <!-- Status badge with colored dot -->
                <div class="vd-status-row">
                  <span class="vd-status-dot" :class="'vd-status--' + vehicle.status"></span>
                  <span class="badge" :class="statusBadgeClass">{{ statusLabel }}</span>
                </div>

                <!-- Vehicle name -->
                <h1 class="vd-vehicle-name">{{ vehicle.brand }} {{ vehicle.model }}</h1>

                <!-- Price -->
                <div class="vd-price">
                  <span class="vd-price-symbol">¥</span>
                  <span class="vd-price-value">{{ vehicle.dailyPrice }}</span>
                  <span class="vd-price-unit">/日</span>
                </div>

                <!-- Specs grid (2x3) -->
                <div class="vd-specs-grid">
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">品牌</span>
                    <span class="vd-spec-value">{{ vehicle.brand }}</span>
                  </div>
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">型号</span>
                    <span class="vd-spec-value">{{ vehicle.model }}</span>
                  </div>
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">车牌号</span>
                    <span class="vd-spec-value">{{ vehicle.plateNumber }}</span>
                  </div>
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">颜色</span>
                    <span class="vd-spec-value">{{ vehicle.color }}</span>
                  </div>
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">座位数</span>
                    <span class="vd-spec-value">{{ vehicle.seatCount }} 座</span>
                  </div>
                  <div class="vd-spec-item">
                    <span class="vd-spec-label">日租金</span>
                    <span class="vd-spec-value vd-spec-price">¥{{ vehicle.dailyPrice }}</span>
                  </div>
                </div>

                <!-- Parking spot (if available) -->
                <div v-if="vehicle.parkingSpot" class="vd-parking">
                  <i class="fa-solid fa-location-dot"></i>
                  <span>车位: {{ vehicle.parkingSpot }}</span>
                </div>

                <!-- Description (if available) -->
                <p v-if="vehicle.description" class="vd-description">{{ vehicle.description }}</p>

                <!-- Action buttons -->
                <div class="vd-actions">
                  <template v-if="vehicle.status === 'AVAILABLE'">
                    <router-link to="/register" class="btn btn-accent btn-lg vd-btn-rent">
                      <i class="fa-solid fa-car"></i> 立即注册租车
                    </router-link>
                    <router-link to="/login" class="btn btn-outline btn-lg vd-btn-login">
                      已有账号？登录
                    </router-link>
                  </template>
                  <button v-else class="btn btn-lg vd-btn-disabled" disabled>
                    <i class="fa-solid fa-ban"></i> 暂不可租
                  </button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </main>

    <!-- ==================== Footer ==================== -->
    <footer class="vd-footer">
      <div class="vd-footer__inner">
        <div class="vd-footer__grid">
          <!-- Col 1: Brand -->
          <div class="vd-footer__col">
            <div class="vd-footer__brand">
              <i class="fa-solid fa-car vd-footer__brand-icon"></i>
              <span class="vd-footer__brand-text">爱租车</span>
            </div>
            <p class="vd-footer__desc">专业汽车租赁服务平台</p>
            <div class="vd-footer__contact">
              <div class="vd-footer__contact-item">
                <i class="fa-solid fa-phone vd-footer__contact-icon"></i>
                <span>400-888-6688</span>
              </div>
              <div class="vd-footer__contact-item">
                <i class="fa-solid fa-envelope vd-footer__contact-icon"></i>
                <span>service@aizuche.com</span>
              </div>
            </div>
          </div>

          <!-- Col 2: 快速链接 -->
          <div class="vd-footer__col">
            <h4 class="vd-footer__heading">快速链接</h4>
            <ul class="vd-footer__links">
              <li><router-link to="/" class="vd-footer__link">首页</router-link></li>
              <li><router-link to="/" class="vd-footer__link">服务特色</router-link></li>
              <li><router-link to="/" class="vd-footer__link">车型展示</router-link></li>
              <li><a href="#" class="vd-footer__link">租车流程</a></li>
            </ul>
          </div>

          <!-- Col 3: 服务支持 -->
          <div class="vd-footer__col">
            <h4 class="vd-footer__heading">服务支持</h4>
            <ul class="vd-footer__links">
              <li><a href="#" class="vd-footer__link">常见问题</a></li>
              <li><a href="#" class="vd-footer__link">租车须知</a></li>
              <li><a href="#" class="vd-footer__link">保险说明</a></li>
              <li><a href="#" class="vd-footer__link">违章处理</a></li>
            </ul>
          </div>

          <!-- Col 4: 关于我们 -->
          <div class="vd-footer__col">
            <h4 class="vd-footer__heading">关于我们</h4>
            <ul class="vd-footer__links">
              <li><router-link to="/about" class="vd-footer__link">公司简介</router-link></li>
              <li><router-link to="/contact" class="vd-footer__link">联系方式</router-link></li>
              <li><a href="#" class="vd-footer__link">加入我们</a></li>
              <li><a href="#" class="vd-footer__link">用户协议</a></li>
            </ul>
          </div>
        </div>

        <div class="vd-footer__bottom">
          <p class="vd-footer__copyright">&copy; 2026 爱租车汽车租赁服务平台 &middot; 粤ICP备XXXXXXXX号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { vehicleApi } from '@/api'

const route = useRoute()
const id = route.params.id

/* -----------------------------------------------
   State
----------------------------------------------- */
const vehicle = ref(null)
const loading = ref(true)
const error = ref(false)
const scrolled = ref(false)

/* -----------------------------------------------
   Status mapping
----------------------------------------------- */
const statusMap = {
  AVAILABLE: { label: '可租', cls: 'badge-green' },
  RENTED: { label: '已租', cls: 'badge-red' },
  REPAIR: { label: '维修中', cls: 'badge-amber' },
  OFFLINE: { label: '已下架', cls: 'badge-gray' }
}

const statusLabel = computed(() => {
  if (!vehicle.value) return ''
  return statusMap[vehicle.value.status]?.label || vehicle.value.status
})

const statusBadgeClass = computed(() => {
  if (!vehicle.value) return ''
  return statusMap[vehicle.value.status]?.cls || 'badge-gray'
})

function getFirstImageUrl(imageUrls) {
  if (!imageUrls) return ''
  if (Array.isArray(imageUrls)) return imageUrls[0] || ''
  return imageUrls.split(',')[0]?.trim() || ''
}

const vehicleImage = computed(() => {
  if (!vehicle.value) return ''
  return getFirstImageUrl(vehicle.value.imageUrls) || getFirstImageUrl(vehicle.value.imageUrl)
})

/* -----------------------------------------------
   Scroll handler
----------------------------------------------- */
function handleScroll() {
  scrolled.value = window.scrollY > 10
}

/* -----------------------------------------------
   Fetch vehicle
----------------------------------------------- */
async function fetchVehicle() {
  loading.value = true
  error.value = false
  try {
    const res = await vehicleApi.detail(id)
    if (res.code === 200 && res.data) {
      vehicle.value = res.data
    } else {
      error.value = true
    }
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

/* -----------------------------------------------
   Lifecycle
----------------------------------------------- */
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()
  fetchVehicle()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ================================================
   NAVBAR
================================================ */
.vd-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  background: rgba(18, 18, 18, 0.65);
  backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  transition: background 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
}

.vd-navbar--scrolled {
  background: rgba(18, 18, 18, 0.88);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.35);
}

.vd-navbar__inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.vd-navbar__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #f1f5f9;
  flex-shrink: 0;
}

.vd-navbar__logo-icon {
  font-size: 24px;
  color: var(--accent, #f59e0b);
}

.vd-navbar__brand-text {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.vd-navbar__links {
  display: flex;
  list-style: none;
  gap: 32px;
  margin: 0;
  padding: 0;
  margin-left: 48px;
}

.vd-navbar__links a {
  text-decoration: none;
  color: #f1f5f9;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s ease;
  position: relative;
}

.vd-navbar__links a::after {
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

.vd-navbar__links a:hover,
.vd-navbar__links a.router-link-active {
  color: #ffffff;
}

.vd-navbar__links a:hover::after,
.vd-navbar__links a.router-link-active::after {
  width: 100%;
}

.vd-navbar__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.vd-navbar__btn {
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.vd-navbar__btn--login {
  color: #f1f5f9;
  background: transparent;
}

.vd-navbar__btn--login:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.vd-navbar__btn--register {
  color: #ffffff;
  background: var(--accent, #f59e0b);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.35);
}

.vd-navbar__btn--register:hover {
  background: var(--accent-dark, #d97706);
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.5);
}

.vd-navbar__hamburger {
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

.vd-navbar__hamburger span {
  display: block;
  width: 24px;
  height: 2px;
  background: #f1f5f9;
  border-radius: 2px;
  transition: transform 0.25s ease, opacity 0.25s ease;
}

/* ================================================
   PAGE LAYOUT
================================================ */
.vd-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg, #f1f5f9);
}

.vd-main {
  flex: 1;
  padding-top: 88px;
  padding-bottom: 48px;
}

.vd-container {
  max-width: 1140px;
  margin: 0 auto;
  padding: 0 24px;
}

/* ================================================
   BREADCRUMB
================================================ */
.vd-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  color: var(--text-muted, #94a3b8);
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.vd-breadcrumb a {
  color: var(--text-muted, #94a3b8);
  text-decoration: none;
  transition: color 0.2s;
}

.vd-breadcrumb a:hover {
  color: var(--primary, #2563eb);
}

.vd-breadcrumb i {
  font-size: 0.625rem;
  color: var(--gray-300, #cbd5e1);
}

.vd-breadcrumb span {
  color: var(--text, #1e293b);
  font-weight: 600;
}

/* ================================================
   LOADING
================================================ */
.vd-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: var(--text-muted, #94a3b8);
}

.vd-loading i {
  font-size: 2.5rem;
  margin-bottom: 16px;
  color: var(--primary, #2563eb);
}

.vd-loading p {
  font-size: 1rem;
}

/* ================================================
   ERROR / NOT FOUND
================================================ */
.vd-error {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.vd-error__card {
  text-align: center;
  background: var(--card-bg, #fff);
  border-radius: var(--radius-lg, 14px);
  box-shadow: var(--shadow-md, 0 4px 12px rgba(0, 0, 0, 0.08));
  padding: 48px 40px;
  max-width: 440px;
  width: 100%;
}

.vd-error__icon {
  font-size: 3.5rem;
  color: var(--danger, #dc2626);
  margin-bottom: 16px;
}

.vd-error__card h2 {
  font-size: 1.5rem;
  color: var(--dark, #0f172a);
  margin-bottom: 8px;
}

.vd-error__card p {
  color: var(--text-muted, #94a3b8);
  margin-bottom: 24px;
  font-size: 0.9375rem;
}

/* ================================================
   DETAIL LAYOUT (Two-column)
================================================ */
.vd-detail-layout {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 32px;
  align-items: start;
}

/* ================================================
   IMAGE SECTION (Left - larger)
================================================ */
.vd-image-section {
  background: var(--gray-200, #e2e8f0);
  border-radius: var(--radius-lg, 14px);
  overflow: hidden;
  min-height: 350px;
}

.vd-image {
  width: 100%;
  height: 100%;
  min-height: 350px;
  object-fit: cover;
  display: block;
}

.vd-image-fallback {
  width: 100%;
  min-height: 350px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gray-200, #e2e8f0);
}

.vd-fallback-emoji {
  font-size: 96px;
  line-height: 1;
}

/* ================================================
   INFO CARD (Right)
================================================ */
.vd-info-card {
  background: var(--card-bg, #fff);
  border-radius: var(--radius-lg, 14px);
  box-shadow: var(--shadow-md, 0 4px 12px rgba(0, 0, 0, 0.08));
  padding: 32px;
}

/* Status badge */
.vd-status-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.vd-status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.vd-status--AVAILABLE {
  background: var(--success, #16a34a);
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.2);
}

.vd-status--RENTED {
  background: var(--danger, #dc2626);
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.2);
}

.vd-status--REPAIR {
  background: var(--warning, #d97706);
  box-shadow: 0 0 0 3px rgba(217, 119, 6, 0.2);
}

.vd-status--OFFLINE {
  background: var(--gray-400, #94a3b8);
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.2);
}

/* Vehicle name */
.vd-vehicle-name {
  font-size: 1.625rem;
  font-weight: 800;
  color: var(--dark, #0f172a);
  margin: 0 0 12px;
}

/* Price */
.vd-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-bottom: 24px;
}

.vd-price-symbol {
  font-size: 1rem;
  font-weight: 700;
  color: var(--danger, #dc2626);
}

.vd-price-value {
  font-size: 2rem;
  font-weight: 800;
  color: var(--danger, #dc2626);
  line-height: 1;
}

.vd-price-unit {
  font-size: 0.875rem;
  color: var(--text-muted, #94a3b8);
  margin-left: 2px;
}

/* ================================================
   SPECS GRID (2x3)
================================================ */
.vd-specs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 20px 0;
  border-top: 1px solid var(--border, #e2e8f0);
  border-bottom: 1px solid var(--border, #e2e8f0);
  margin-bottom: 16px;
}

.vd-spec-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.vd-spec-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-muted, #94a3b8);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.vd-spec-value {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--text, #1e293b);
}

.vd-spec-price {
  color: var(--danger, #dc2626);
}

/* ================================================
   PARKING SPOT
================================================ */
.vd-parking {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: var(--gray-50, #f8fafc);
  border-radius: var(--radius-sm, 6px);
  font-size: 0.875rem;
  color: var(--text-secondary, #64748b);
  margin-bottom: 16px;
}

.vd-parking i {
  color: var(--accent, #f59e0b);
}

/* ================================================
   DESCRIPTION
================================================ */
.vd-description {
  font-size: 0.9375rem;
  color: var(--text-secondary, #64748b);
  line-height: 1.7;
  margin: 0 0 24px;
}

/* ================================================
   ACTION BUTTONS
================================================ */
.vd-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.vd-btn-rent {
  width: 100%;
}

.vd-btn-login {
  width: 100%;
}

.vd-btn-disabled {
  width: 100%;
  background: var(--gray-200, #e2e8f0);
  color: var(--gray-400, #94a3b8);
  cursor: not-allowed;
  opacity: 1 !important;
}

/* ================================================
   FOOTER
================================================ */
.vd-footer {
  background: var(--dark, #0f172a);
  color: var(--gray-400, #94a3b8);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
  margin-top: auto;
}

.vd-footer__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 24px 0;
}

.vd-footer__grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr 1fr;
  gap: 40px;
  padding-bottom: 40px;
}

.vd-footer__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.vd-footer__brand-icon {
  font-size: 28px;
  color: var(--accent, #f59e0b);
}

.vd-footer__brand-text {
  font-size: 22px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 0.5px;
}

.vd-footer__desc {
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px;
}

.vd-footer__contact {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.vd-footer__contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.vd-footer__contact-icon {
  font-size: 14px;
  color: var(--accent, #f59e0b);
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.vd-footer__heading {
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 16px;
  position: relative;
  padding-bottom: 10px;
}

.vd-footer__heading::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 28px;
  height: 2px;
  background: var(--accent, #f59e0b);
  border-radius: 1px;
}

.vd-footer__links {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.vd-footer__link {
  text-decoration: none;
  color: var(--gray-400, #94a3b8);
  font-size: 14px;
  transition: color 0.2s ease, padding-left 0.2s ease;
  display: inline-block;
}

.vd-footer__link:hover {
  color: #ffffff;
  padding-left: 6px;
}

.vd-footer__bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px 0;
  text-align: center;
}

.vd-footer__copyright {
  margin: 0;
  font-size: 13px;
}

/* ================================================
   RESPONSIVE
================================================ */
@media (max-width: 768px) {
  .vd-navbar__links,
  .vd-navbar__actions {
    display: none;
  }

  .vd-navbar__hamburger {
    display: flex;
  }

  .vd-detail-layout {
    grid-template-columns: 1fr;
  }

  .vd-image-section {
    min-height: 240px;
  }

  .vd-image {
    min-height: 240px;
  }

  .vd-image-fallback {
    min-height: 240px;
  }

  .vd-footer__grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 540px) {
  .vd-main {
    padding-top: 76px;
  }

  .vd-container {
    padding: 0 16px;
  }

  .vd-info-card {
    padding: 20px;
  }

  .vd-specs-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .vd-footer__inner {
    padding-top: 40px;
  }

  .vd-footer__grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .vd-footer__brand {
    justify-content: center;
  }

  .vd-footer__desc {
    text-align: center;
  }

  .vd-footer__contact {
    align-items: center;
  }

  .vd-footer__col {
    text-align: center;
  }

  .vd-footer__heading::after {
    left: 50%;
    transform: translateX(-50%);
  }

  .vd-footer__links {
    align-items: center;
  }
}
</style>
