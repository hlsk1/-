<template>
  <div class="home-page">

    <!-- ==================== 1. NavBar ==================== -->
    <nav class="navbar" :class="{ 'navbar--scrolled': isScrolled }">
      <div class="navbar__inner">
        <a href="#" class="navbar__brand" @click.prevent="scrollToTop">
          <i class="fa-solid fa-car navbar__logo-icon"></i>
          <span class="navbar__brand-text">爱租车</span>
        </a>

        <ul class="navbar__links">
          <li><a href="#hero" @click.prevent="scrollTo('hero')">首页</a></li>
          <li><a href="#services" @click.prevent="scrollTo('services')">服务</a></li>
          <li><a href="#fleet" @click.prevent="scrollTo('fleet')">车型</a></li>
          <li><a href="#process" @click.prevent="scrollTo('process')">关于</a></li>
          <li><a href="#footer" @click.prevent="scrollTo('footer')">联系</a></li>
        </ul>

        <div class="navbar__actions">
          <router-link to="/login" class="navbar__btn navbar__btn--login">登录</router-link>
          <router-link to="/register" class="navbar__btn navbar__btn--register">免费注册</router-link>
        </div>
      </div>
    </nav>

    <!-- ==================== 2. Hero Section ==================== -->
    <section id="hero" class="hero">
      <div class="hero__bg">
        <div
          v-for="(img, i) in CAR_IMAGES"
          :key="i"
          class="hero__bg-img"
          :class="{ 'hero__bg-img--active': i === currentImageIndex }"
          :style="{ backgroundImage: `url('/CarImages/${img}')` }"
        ></div>
      </div>
      <div class="hero__overlay"></div>

      <div class="hero__content">
        <div class="hero__badge">🚗 专业汽车租赁 · 值得信赖</div>
        <h1 class="hero__title">
          <span class="hero__title-gradient">爱租车</span>
        </h1>
        <p class="hero__subtitle">品质座驾 · 灵活租期 · 无忧出行</p>

        <div class="hero__cta">
          <router-link to="/register" class="btn btn--accent btn--lg">立即注册租车</router-link>
          <a href="#fleet" class="btn btn--outline btn--lg" @click.prevent="scrollTo('fleet')">浏览车型</a>
        </div>

        <div class="hero__stats">
          <div class="hero__stat">
            <span class="hero__stat-num">50+</span>
            <span class="hero__stat-label">精选车型</span>
          </div>
          <div class="hero__stat">
            <span class="hero__stat-num">5,000+</span>
            <span class="hero__stat-label">服务客户</span>
          </div>
          <div class="hero__stat">
            <span class="hero__stat-num">98%</span>
            <span class="hero__stat-label">客户满意度</span>
          </div>
          <div class="hero__stat">
            <span class="hero__stat-num">7×24</span>
            <span class="hero__stat-label">全天候服务</span>
          </div>
        </div>
      </div>

      <div class="hero__dots">
        <button
          v-for="(img, i) in CAR_IMAGES"
          :key="i"
          class="hero__dot"
          :class="{ 'hero__dot--active': i === currentImageIndex }"
          @click="goToSlide(i)"
        ></button>
      </div>
    </section>

    <!-- ==================== 3. Services Section ==================== -->
    <section id="services" class="services section">
      <div class="section__header">
        <h2 class="section__title">为什么选择我们</h2>
        <p class="section__desc">爱租车致力于为您提供最优质的出行体验</p>
      </div>

      <div class="services__grid">
        <div class="service-card">
          <div class="service-card__icon service-card__icon--blue">
            <i class="fa-solid fa-car"></i>
          </div>
          <h3 class="service-card__title">车型丰富</h3>
          <p class="service-card__desc">经济型到豪华型全覆盖，满足不同出行需求</p>
        </div>

        <div class="service-card">
          <div class="service-card__icon service-card__icon--amber">
            <i class="fa-solid fa-bolt"></i>
          </div>
          <h3 class="service-card__title">极速取还</h3>
          <p class="service-card__desc">线上预约，最快15分钟提车，快捷方便</p>
        </div>

        <div class="service-card">
          <div class="service-card__icon service-card__icon--green">
            <i class="fa-solid fa-shield-haltered"></i>
          </div>
          <h3 class="service-card__title">安全保障</h3>
          <p class="service-card__desc">全险覆盖，24h道路救援，出行无忧</p>
        </div>

        <div class="service-card">
          <div class="service-card__icon service-card__icon--purple">
            <i class="fa-solid fa-tag"></i>
          </div>
          <h3 class="service-card__title">价格透明</h3>
          <p class="service-card__desc">无隐藏费用，灵活定价，明码实价</p>
        </div>
      </div>
    </section>

    <!-- ==================== 4. Fleet Section ==================== -->
    <section id="fleet" class="fleet section">
      <div class="section__header">
        <h2 class="section__title">热门车型推荐</h2>
        <p class="section__desc">精选优质车型，总有一款适合您</p>
      </div>

      <div class="fleet__grid">
        <div v-if="loadingVehicles && displayVehicles.length === 0" class="fleet__loading">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <span>加载车型中...</span>
        </div>

        <div
          v-for="vehicle in displayVehicles"
          :key="vehicle.id"
          class="vehicle-card"
        >
          <div class="vehicle-card__img-wrap">
            <span v-if="vehicle.image" class="vehicle-card__img" :style="{ backgroundImage: `url('${vehicle.image}')` }"></span>
            <span v-else class="vehicle-card__placeholder">🚗</span>
          </div>
          <div class="vehicle-card__body">
            <h3 class="vehicle-card__brand">{{ vehicle.brand }}</h3>
            <p class="vehicle-card__model">{{ vehicle.model }}</p>
            <div class="vehicle-card__meta">
              <span v-if="vehicle.color"><i class="fa-solid fa-palette"></i> {{ vehicle.color }}</span>
              <span v-if="vehicle.seatCount"><i class="fa-solid fa-user"></i> {{ vehicle.seatCount }}座</span>
              <span v-if="vehicle.plate"><i class="fa-solid fa-id-card"></i> {{ vehicle.plate }}</span>
            </div>
            <div class="vehicle-card__price">
              <span class="vehicle-card__price-num">&yen;{{ vehicle.pricePerDay }}</span>
              <span class="vehicle-card__price-unit">/天</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="fleetError && displayVehicles.length === 0" class="fleet__error">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <span>{{ fleetError }}</span>
      </div>
    </section>

    <!-- ==================== 5. Process Section ==================== -->
    <section id="process" class="process section">
      <div class="section__header">
        <h2 class="section__title">租车只需4步</h2>
        <p class="section__desc">简单快捷，轻松开启您的旅程</p>
      </div>

      <div class="process__steps">
        <div class="process-step">
          <div class="process-step__num">01</div>
          <div class="process-step__icon">
            <i class="fa-solid fa-user-plus"></i>
          </div>
          <h3 class="process-step__title">在线注册</h3>
          <p class="process-step__desc">填写基本信息，<br/>一分钟完成注册</p>
        </div>

        <div class="process-step">
          <div class="process-step__num">02</div>
          <div class="process-step__icon">
            <i class="fa-solid fa-car-side"></i>
          </div>
          <h3 class="process-step__title">选择车型</h3>
          <p class="process-step__desc">浏览丰富车型库，<br/>选择心仪座驾</p>
        </div>

        <div class="process-step">
          <div class="process-step__num">03</div>
          <div class="process-step__icon">
            <i class="fa-solid fa-key"></i>
          </div>
          <h3 class="process-step__title">到店取车</h3>
          <p class="process-step__desc">凭订单到门店取车，<br/>即刻出发上路</p>
        </div>

        <div class="process-step">
          <div class="process-step__num">04</div>
          <div class="process-step__icon">
            <i class="fa-solid fa-rotate-left"></i>
          </div>
          <h3 class="process-step__title">便捷还车</h3>
          <p class="process-step__desc">租期结束归还车辆，<br/>轻松完成行程</p>
        </div>
      </div>
    </section>

    <!-- ==================== 6. Testimonials Section ==================== -->
    <section id="testimonials" class="testimonials section">
      <div class="section__header">
        <h2 class="section__title">用户好评</h2>
        <p class="section__desc">听听我们的客户怎么说</p>
      </div>

      <div class="testimonials__grid">
        <div v-for="t in testimonials" :key="t.name" class="testimonial-card">
          <div class="testimonial-card__stars">
            <i v-for="n in 5" :key="n" class="fa-solid fa-star"></i>
          </div>
          <p class="testimonial-card__text">"{{ t.text }}"</p>
          <div class="testimonial-card__author">
            <div class="testimonial-card__avatar">{{ t.name.charAt(0) }}</div>
            <div>
              <div class="testimonial-card__name">{{ t.name }}</div>
              <div class="testimonial-card__role">{{ t.role }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ==================== 7. CTA Section ==================== -->
    <section class="cta section">
      <div class="cta__inner">
        <h2 class="cta__title">准备好开启您的旅程了吗？</h2>
        <p class="cta__desc">注册即享新用户专属优惠，立即体验便捷租车服务</p>
        <router-link to="/register" class="btn btn--accent btn--lg cta__btn">立即免费注册</router-link>
      </div>
    </section>

    <!-- ==================== 8. Footer ==================== -->
    <footer id="footer" class="footer">
      <div class="footer__main">
        <div class="footer__col footer__col--brand">
          <div class="footer__brand">
            <i class="fa-solid fa-car"></i>
            <span>爱租车</span>
          </div>
          <p class="footer__brand-desc">专业汽车租赁服务平台，提供便捷、安全、优质的租车体验。</p>
          <div class="footer__contact-item">
            <i class="fa-solid fa-phone"></i>
            <span>400-888-9999</span>
          </div>
          <div class="footer__contact-item">
            <i class="fa-solid fa-envelope"></i>
            <span>service@aizuche.com</span>
          </div>
          <div class="footer__contact-item">
            <i class="fa-solid fa-location-dot"></i>
            <span>全国各地门店，覆盖主要城市</span>
          </div>
        </div>

        <div class="footer__col">
          <h4 class="footer__col-title">快速链接</h4>
          <ul class="footer__links">
            <li><a href="#hero" @click.prevent="scrollTo('hero')">首页</a></li>
            <li><a href="#services" @click.prevent="scrollTo('services')">服务项目</a></li>
            <li><a href="#fleet" @click.prevent="scrollTo('fleet')">车型展示</a></li>
            <li><a href="#process" @click.prevent="scrollTo('process')">租车流程</a></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4 class="footer__col-title">客户支持</h4>
          <ul class="footer__links">
            <li><router-link to="/register">注册账号</router-link></li>
            <li><router-link to="/login">用户登录</router-link></li>
            <li><a href="#">常见问题</a></li>
            <li><a href="#">租车须知</a></li>
          </ul>
        </div>

        <div class="footer__col">
          <h4 class="footer__col-title">关于我们</h4>
          <ul class="footer__links">
            <li><a href="#">公司简介</a></li>
            <li><a href="#">联系我们</a></li>
            <li><a href="#">服务条款</a></li>
            <li><a href="#">隐私政策</a></li>
          </ul>
        </div>
      </div>

      <div class="footer__bottom">
        <span>&copy; 2026 爱租车汽车租赁服务平台 &mdash; 版权所有</span>
      </div>
    </footer>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { vehicleApi } from '@/api'

/* -----------------------------------------------
   Car images for hero slider
----------------------------------------------- */
const CAR_IMAGES = [
  '上汽通用五菱 星光730新能源_15.jpg',
  '保时捷 保时捷911_8.jpg',
  '方程豹 钛7_13.jpg',
  '比亚迪 大唐_14.jpg',
  '特斯拉中国 Model Y_12.jpg',
  '长城汽车 坦克300_11.jpg',
  '零跑汽车 零跑A10_10.jpg',
  '广汽丰田 凯美瑞_4.jpg',
  '吉利汽车 星愿_2.jpg',
  '广汽本田 奥德赛_16.jpg',
  '奔驰(进口) 奔驰S级_6.jpg',
  '广汽丰田 赛那SIENNA_7.jpg'
]

/* -----------------------------------------------
   Navbar scroll state
----------------------------------------------- */
const isScrolled = ref(false)

function handleScroll() {
  isScrolled.value = window.scrollY > 20
}

/* -----------------------------------------------
   Smooth scroll helper
----------------------------------------------- */
function scrollTo(id) {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

/* -----------------------------------------------
   Hero image slider
----------------------------------------------- */
const currentImageIndex = ref(0)
let sliderTimer = null

function nextSlide() {
  currentImageIndex.value = (currentImageIndex.value + 1) % CAR_IMAGES.length
}

function goToSlide(index) {
  currentImageIndex.value = index
  resetSliderTimer()
}

function startSlider() {
  stopSlider()
  sliderTimer = setInterval(nextSlide, 4000)
}

function stopSlider() {
  if (sliderTimer) {
    clearInterval(sliderTimer)
    sliderTimer = null
  }
}

function resetSliderTimer() {
  stopSlider()
  startSlider()
}

/* -----------------------------------------------
   Fleet / vehicles
----------------------------------------------- */
const displayVehicles = ref([])
const loadingVehicles = ref(false)
const fleetError = ref('')

const DEFAULT_VEHICLES = [
  { id: 'd1', brand: '丰田', model: '凯美瑞', color: '珍珠白', seatCount: 5, plate: '粤B·88888', pricePerDay: 388, image: '' },
  { id: 'd2', brand: '本田', model: '雅阁', color: '星空蓝', seatCount: 5, plate: '粤A·66666', pricePerDay: 358, image: '' },
  { id: 'd3', brand: '宝马', model: '3系', color: '矿石白', seatCount: 5, plate: '沪A·77777', pricePerDay: 598, image: '' },
  { id: 'd4', brand: '奔驰', model: 'C级', color: '曜石黑', seatCount: 5, plate: '京A·99999', pricePerDay: 628, image: '' },
  { id: 'd5', brand: '别克', model: 'GL8', color: '琥珀金', seatCount: 7, plate: '粤C·55555', pricePerDay: 488, image: '' },
  { id: 'd6', brand: '特斯拉', model: 'Model 3', color: '深海蓝', seatCount: 5, plate: '沪B·33333', pricePerDay: 528, image: '' }
]

function getFirstImageUrl(imageUrls) {
  if (!imageUrls) return ''
  if (Array.isArray(imageUrls)) return imageUrls[0] || ''
  return imageUrls.split(',')[0]?.trim() || ''
}

function resolveVehicleImage(vehicle) {
  const firstUrl = getFirstImageUrl(vehicle.imageUrls)
  if (firstUrl) return firstUrl
  if (vehicle.imageUrl) return vehicle.imageUrl
  // Try to find a match in CAR_IMAGES
  for (const img of CAR_IMAGES) {
    if (vehicle.brand && img.includes(vehicle.brand)) return `/CarImages/${img}`
    if (vehicle.model && img.includes(vehicle.model)) return `/CarImages/${img}`
  }
  return ''
}

async function fetchVehicles() {
  loadingVehicles.value = true
  fleetError.value = ''
  try {
    const res = await vehicleApi.list()
    if (res.code === 200 && res.data && res.data.length > 0) {
      const available = res.data.filter(v => v.status === 'AVAILABLE')
      const top6 = available.slice(0, 6)
      displayVehicles.value = top6.map(v => ({
        id: v.id || v.vehicleId,
        brand: v.brand || '未知品牌',
        model: v.model || '',
        color: v.color || '',
        seatCount: v.seatCount || v.seats || 5,
        plate: v.plate || v.licensePlate || '',
        pricePerDay: v.pricePerDay ?? v.dailyRate ?? v.price ?? 299,
        image: resolveVehicleImage(v)
      }))
    } else {
      displayVehicles.value = DEFAULT_VEHICLES
    }
  } catch (e) {
    console.error('Failed to fetch vehicles, using defaults:', e)
    displayVehicles.value = DEFAULT_VEHICLES
  } finally {
    loadingVehicles.value = false
  }
}

/* -----------------------------------------------
   Testimonials
----------------------------------------------- */
const testimonials = [
  {
    name: '张先生',
    role: '商务出差用户',
    text: '出差经常租车，爱租车的服务非常专业，车型选择多，取还车都很快捷方便，价格也很合理，强烈推荐！'
  },
  {
    name: '李女士',
    role: '家庭出游用户',
    text: '带家人出游租了一辆GL8，车况很好，干净整洁，客服服务态度也很棒，以后还会继续使用爱租车。'
  },
  {
    name: '王先生',
    role: '自驾游爱好者',
    text: '租了一辆坦克300去川西自驾，车辆性能出色，租车流程也很顺畅，24小时道路救援让人特别安心。'
  }
]

/* -----------------------------------------------
   Lifecycle
----------------------------------------------- */
onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()
  startSlider()
  fetchVehicles()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  stopSlider()
})
</script>

<style scoped>
/* ================================================
   GENERAL / SECTION LAYOUT
================================================ */
.home-page {
  min-height: 100vh;
  background: var(--bg, #f1f5f9);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
}

.section {
  padding: 100px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.section__header {
  text-align: center;
  margin-bottom: 56px;
}

.section__title {
  font-size: 2.25rem;
  font-weight: 800;
  color: var(--dark, #0f172a);
  margin: 0 0 12px;
  letter-spacing: -0.02em;
}

.section__desc {
  font-size: 1.0625rem;
  color: var(--text-muted, #94a3b8);
  margin: 0;
}

/* ================================================
   BUTTONS
================================================ */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-decoration: none;
  font-weight: 600;
  font-family: inherit;
  border: none;
  border-radius: var(--radius, 10px);
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
}

.btn--lg {
  padding: 14px 36px;
  font-size: 1.0625rem;
  border-radius: var(--radius-lg, 14px);
}

.btn--accent {
  background: var(--accent, #f59e0b);
  color: #fff;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.35);
}

.btn--accent:hover {
  background: var(--accent-dark, #d97706);
  box-shadow: 0 6px 24px rgba(245, 158, 11, 0.5);
  transform: translateY(-1px);
}

.btn--outline {
  background: transparent;
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.6);
}

.btn--outline:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: #fff;
  transform: translateY(-1px);
}

/* ================================================
   1. NAVBAR
================================================ */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 68px;
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(16px) saturate(160%);
  -webkit-backdrop-filter: blur(16px) saturate(160%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  transition: background 0.3s ease, box-shadow 0.3s ease;
}

.navbar--scrolled {
  background: rgba(15, 23, 42, 0.98);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.4);
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
  color: #f1f5f9;
  flex-shrink: 0;
}

.navbar__logo-icon {
  font-size: 26px;
  color: var(--accent, #f59e0b);
}

.navbar__brand-text {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.navbar__links {
  display: flex;
  list-style: none;
  gap: 36px;
  margin: 0 0 0 52px;
  padding: 0;
}

.navbar__links a {
  text-decoration: none;
  color: #e2e8f0;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s ease;
  position: relative;
}

.navbar__links a::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--accent, #f59e0b);
  border-radius: 1px;
  transition: width 0.25s ease;
}

.navbar__links a:hover,
.navbar__links a.router-link-active {
  color: #fff;
}

.navbar__links a:hover::after,
.navbar__links a.router-link-active::after {
  width: 100%;
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
  padding: 9px 22px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.navbar__btn--login {
  color: #e2e8f0;
  background: transparent;
}

.navbar__btn--login:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.navbar__btn--register {
  color: #fff;
  background: var(--accent, #f59e0b);
  box-shadow: 0 2px 10px rgba(245, 158, 11, 0.35);
}

.navbar__btn--register:hover {
  background: var(--accent-dark, #d97706);
  box-shadow: 0 4px 18px rgba(245, 158, 11, 0.5);
}

/* ================================================
   2. HERO SECTION
================================================ */
.hero {
  position: relative;
  height: 100vh;
  min-height: 700px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero__bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero__bg-img {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transition: opacity 1s ease-in-out;
}

.hero__bg-img--active {
  opacity: 1;
}

.hero__overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    180deg,
    rgba(15, 23, 42, 0.75) 0%,
    rgba(15, 23, 42, 0.55) 40%,
    rgba(15, 23, 42, 0.65) 100%
  );
}

.hero__content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 780px;
  padding: 0 24px;
}

.hero__badge {
  display: inline-block;
  padding: 8px 20px;
  border-radius: 100px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: #e2e8f0;
  font-size: 0.9375rem;
  font-weight: 500;
  margin-bottom: 28px;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.hero__title {
  font-size: 4.5rem;
  font-weight: 900;
  margin: 0 0 16px;
  letter-spacing: 0.06em;
  line-height: 1.15;
}

.hero__title-gradient {
  background: linear-gradient(135deg, #f59e0b 0%, #fbbf24 40%, #eab308 60%, #f59e0b 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero__subtitle {
  font-size: 1.375rem;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 40px;
  font-weight: 400;
  letter-spacing: 0.04em;
}

.hero__cta {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 60px;
}

.hero__stats {
  display: flex;
  justify-content: center;
  gap: 56px;
  flex-wrap: wrap;
}

.hero__stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.hero__stat-num {
  font-size: 1.75rem;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.01em;
}

.hero__stat-label {
  font-size: 0.8125rem;
  color: rgba(255, 255, 255, 0.55);
  font-weight: 500;
  letter-spacing: 0.04em;
}

.hero__dots {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
  display: flex;
  gap: 10px;
}

.hero__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.5);
  background: transparent;
  cursor: pointer;
  padding: 0;
  transition: all 0.3s ease;
}

.hero__dot--active {
  background: var(--accent, #f59e0b);
  border-color: var(--accent, #f59e0b);
  transform: scale(1.3);
}

/* ================================================
   3. SERVICES SECTION
================================================ */
.services {
  background: var(--card-bg, #fff);
  max-width: 100%;
  padding-left: 0;
  padding-right: 0;
}

.services .section__header {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 24px;
}

.services__grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.service-card {
  background: var(--gray-50, #f8fafc);
  border-radius: var(--radius-lg, 14px);
  padding: 40px 28px;
  text-align: center;
  border: 1px solid var(--border, #e2e8f0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.service-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg, 0 10px 30px rgba(0, 0, 0, 0.12));
}

.service-card__icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius, 10px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 1.5rem;
}

.service-card__icon--blue {
  background: var(--primary-light, #eff6ff);
  color: var(--primary, #2563eb);
}

.service-card__icon--amber {
  background: #fffbeb;
  color: #d97706;
}

.service-card__icon--green {
  background: #f0fdf4;
  color: #16a34a;
}

.service-card__icon--purple {
  background: #f5f3ff;
  color: #7c3aed;
}

.service-card__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--dark, #0f172a);
  margin: 0 0 10px;
}

.service-card__desc {
  font-size: 0.9375rem;
  color: var(--text-secondary, #64748b);
  margin: 0;
  line-height: 1.6;
}

/* ================================================
   4. FLEET SECTION
================================================ */
.fleet {
  background: var(--bg, #f1f5f9);
}

.fleet__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.fleet__loading {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 80px 0;
  color: var(--text-muted, #94a3b8);
  font-size: 1.0625rem;
}

.fleet__loading i {
  font-size: 1.5rem;
  color: var(--primary, #2563eb);
}

.fleet__error {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 60px 0;
  color: var(--text-muted, #94a3b8);
}

.fleet__error i {
  font-size: 2.5rem;
  color: var(--warning, #d97706);
}

.vehicle-card {
  background: var(--card-bg, #fff);
  border-radius: var(--radius-lg, 14px);
  overflow: hidden;
  border: 1px solid var(--border, #e2e8f0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
}

.vehicle-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-xl, 0 20px 50px rgba(0, 0, 0, 0.16));
}

.vehicle-card__img-wrap {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: var(--gray-100, #f1f5f9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.vehicle-card__img {
  display: block;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.4s ease;
}

.vehicle-card:hover .vehicle-card__img {
  transform: scale(1.06);
}

.vehicle-card__placeholder {
  font-size: 4rem;
  opacity: 0.5;
}

.vehicle-card__body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.vehicle-card__brand {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--dark, #0f172a);
  margin: 0 0 2px;
}

.vehicle-card__model {
  font-size: 0.875rem;
  color: var(--text-secondary, #64748b);
  margin: 0 0 12px;
}

.vehicle-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 0.8125rem;
  color: var(--gray-400, #94a3b8);
  margin-bottom: 14px;
}

.vehicle-card__meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.vehicle-card__meta i {
  font-size: 0.75rem;
}

.vehicle-card__price {
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid var(--border, #e2e8f0);
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.vehicle-card__price-num {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--accent, #f59e0b);
}

.vehicle-card__price-unit {
  font-size: 0.8125rem;
  color: var(--text-muted, #94a3b8);
}

/* ================================================
   5. PROCESS SECTION
================================================ */
.process {
  background: var(--card-bg, #fff);
}

.process__steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  position: relative;
}

.process__steps::before {
  content: '';
  position: absolute;
  top: 48px;
  left: calc(12.5% + 40px);
  right: calc(12.5% + 40px);
  height: 2px;
  background: var(--border, #e2e8f0);
  z-index: 0;
}

.process-step {
  text-align: center;
  position: relative;
  z-index: 1;
}

.process-step__num {
  font-size: 0.75rem;
  font-weight: 800;
  color: var(--primary, #2563eb);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 12px;
}

.process-step__icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--card-bg, #fff);
  border: 2px solid var(--border, #e2e8f0);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 1.75rem;
  color: var(--primary, #2563eb);
  transition: all 0.3s ease;
}

.process-step:hover .process-step__icon {
  border-color: var(--primary, #2563eb);
  background: var(--primary-light, #eff6ff);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.15);
  transform: translateY(-4px);
}

.process-step__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--dark, #0f172a);
  margin: 0 0 8px;
}

.process-step__desc {
  font-size: 0.9375rem;
  color: var(--text-secondary, #64748b);
  margin: 0;
  line-height: 1.6;
}

/* ================================================
   6. TESTIMONIALS SECTION
================================================ */
.testimonials {
  background: var(--bg, #f1f5f9);
}

.testimonials__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.testimonial-card {
  background: var(--card-bg, #fff);
  border-radius: var(--radius-lg, 14px);
  padding: 32px;
  border: 1px solid var(--border, #e2e8f0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.testimonial-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg, 0 10px 30px rgba(0, 0, 0, 0.12));
}

.testimonial-card__stars {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  color: var(--accent, #f59e0b);
  font-size: 1rem;
}

.testimonial-card__text {
  font-size: 0.9375rem;
  line-height: 1.7;
  color: var(--text, #1e293b);
  margin: 0 0 20px;
  font-style: italic;
}

.testimonial-card__author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.testimonial-card__avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary, #2563eb), var(--accent, #f59e0b));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.0625rem;
  flex-shrink: 0;
}

.testimonial-card__name {
  font-weight: 700;
  color: var(--dark, #0f172a);
  font-size: 0.9375rem;
}

.testimonial-card__role {
  font-size: 0.8125rem;
  color: var(--text-muted, #94a3b8);
}

/* ================================================
   7. CTA SECTION
================================================ */
.cta {
  background: linear-gradient(135deg, var(--dark, #0f172a) 0%, #1e3a5f 100%);
  max-width: 100%;
  padding: 100px 24px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.cta::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(ellipse at 40% 50%, rgba(245, 158, 11, 0.08) 0%, transparent 50%),
              radial-gradient(ellipse at 60% 50%, rgba(37, 99, 235, 0.06) 0%, transparent 50%);
}

.cta__inner {
  position: relative;
  z-index: 1;
  max-width: 600px;
  margin: 0 auto;
}

.cta__title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #fff;
  margin: 0 0 16px;
  letter-spacing: -0.01em;
}

.cta__desc {
  font-size: 1.125rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 36px;
}

.cta__btn {
  display: inline-flex;
  text-decoration: none;
  font-size: 1.125rem;
  font-weight: 700;
  padding: 16px 44px;
  border-radius: var(--radius-lg, 14px);
  background: var(--accent, #f59e0b);
  color: #fff;
  box-shadow: 0 6px 24px rgba(245, 158, 11, 0.4);
  transition: all 0.25s ease;
}

.cta__btn:hover {
  background: var(--accent-dark, #d97706);
  box-shadow: 0 8px 32px rgba(245, 158, 11, 0.55);
  transform: translateY(-2px);
}

/* ================================================
   8. FOOTER
================================================ */
.footer {
  background: var(--dark, #0f172a);
  color: rgba(255, 255, 255, 0.55);
}

.footer__main {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
  padding: 72px 24px 48px;
}

.footer__brand {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  font-size: 1.5rem;
  font-weight: 800;
  color: #fff;
}

.footer__brand i {
  color: var(--accent, #f59e0b);
  font-size: 1.75rem;
}

.footer__brand-desc {
  font-size: 0.9375rem;
  line-height: 1.7;
  margin: 0 0 20px;
}

.footer__contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.875rem;
  margin-bottom: 8px;
}

.footer__contact-item i {
  width: 18px;
  text-align: center;
  color: var(--accent, #f59e0b);
  font-size: 0.8125rem;
}

.footer__col-title {
  font-size: 1rem;
  font-weight: 700;
  color: #fff;
  margin: 0 0 18px;
}

.footer__links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer__links li {
  margin-bottom: 10px;
}

.footer__links a {
  color: rgba(255, 255, 255, 0.55);
  text-decoration: none;
  font-size: 0.9375rem;
  transition: color 0.2s ease;
}

.footer__links a:hover {
  color: var(--accent, #f59e0b);
}

.footer__bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 22px 24px;
  text-align: center;
  font-size: 0.875rem;
  max-width: 1200px;
  margin: 0 auto;
}

/* ================================================
   RESPONSIVE
================================================ */
@media (max-width: 1024px) {
  .services__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .fleet__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .process__steps {
    grid-template-columns: repeat(2, 1fr);
  }

  .process__steps::before {
    display: none;
  }

  .testimonials__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .testimonials__grid .testimonial-card:last-child {
    grid-column: 1 / -1;
    max-width: 400px;
    justify-self: center;
  }

  .footer__main {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .navbar__links {
    display: none;
  }

  .navbar__actions .navbar__btn--login {
    display: none;
  }

  .section {
    padding: 72px 20px;
  }

  .section__title {
    font-size: 1.75rem;
  }

  .hero {
    min-height: 600px;
  }

  .hero__title {
    font-size: 2.75rem;
  }

  .hero__subtitle {
    font-size: 1.0625rem;
  }

  .hero__stats {
    gap: 28px;
  }

  .hero__stat-num {
    font-size: 1.375rem;
  }

  .fleet__grid {
    grid-template-columns: 1fr;
    max-width: 420px;
    margin: 0 auto;
  }

  .process__steps {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .testimonials__grid {
    grid-template-columns: 1fr;
    max-width: 420px;
    margin: 0 auto;
  }

  .testimonials__grid .testimonial-card:last-child {
    grid-column: 1;
    max-width: none;
  }

  .cta__title {
    font-size: 1.75rem;
  }

  .footer__main {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}

@media (max-width: 480px) {
  .services__grid {
    grid-template-columns: 1fr;
  }

  .hero__cta {
    flex-direction: column;
    align-items: center;
  }

  .hero__title {
    font-size: 2.25rem;
  }
}
</style>
