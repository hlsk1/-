<template>
  <div class="about-page">
    <!-- ===== Navbar (inline, fixed top, white bg with glass-morphism) ===== -->
    <nav class="navbar" :class="{ 'navbar--scrolled': isScrolled }">
      <div class="navbar__inner">
        <router-link to="/" class="navbar__brand">
          <i class="fa-solid fa-car navbar__logo-icon"></i>
          <span class="navbar__brand-text">爱租车</span>
        </router-link>

        <ul class="navbar__links">
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/service">服务</router-link></li>
          <li><router-link to="/cars">车型</router-link></li>
          <li><router-link to="/about">关于</router-link></li>
          <li><router-link to="/contact">联系</router-link></li>
        </ul>

        <div class="navbar__actions">
          <router-link to="/login" class="navbar__btn navbar__btn--login">登录</router-link>
          <router-link to="/register" class="navbar__btn navbar__btn--register">免费注册</router-link>
        </div>

        <button class="navbar__hamburger" aria-label="Toggle menu">
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </nav>

    <!-- ===== Page Hero ===== -->
    <section class="hero">
      <div class="hero__inner">
        <span class="hero__tag">🚗 我们的故事</span>
        <h1 class="hero__title">关于爱租车</h1>
        <p class="hero__subtitle">专业汽车租赁服务商</p>
      </div>
    </section>

    <!-- ===== Company Story ===== -->
    <section class="story">
      <div class="story__inner">
        <h2 class="story__heading">一路走来，初心不改</h2>
        <p class="story__text">
          爱租车成立于2020年，是一家专注于汽车租赁服务的现代化企业。公司秉承"安全、便捷、诚信"的服务理念，
          致力于为广大用户提供高品质的汽车租赁体验。经过多年的发展，我们已经建立起覆盖多城市的服务网络，
          拥有50余款精选车型，累计服务超过5,000位客户。无论是商务出行、家庭旅游还是日常通勤，
          爱租车都能为您提供最合适的用车方案。
        </p>

        <div class="story__stats">
          <div class="stat-card">
            <div class="stat-card__value">2020年</div>
            <div class="stat-card__label">成立</div>
          </div>
          <div class="stat-card">
            <div class="stat-card__value">50+</div>
            <div class="stat-card__label">精选车型</div>
          </div>
          <div class="stat-card">
            <div class="stat-card__value">5,000+</div>
            <div class="stat-card__label">服务客户</div>
          </div>
          <div class="stat-card">
            <div class="stat-card__value">98%</div>
            <div class="stat-card__label">满意度</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Core Values ===== -->
    <section class="values">
      <div class="values__inner">
        <h2 class="values__heading">核心价值观</h2>

        <div class="values__grid">
          <div class="value-card">
            <div class="value-card__icon value-card__icon--green">
              <i class="fa-solid fa-shield-haltered"></i>
            </div>
            <h3 class="value-card__title">安全第一</h3>
            <p class="value-card__desc">
              每一辆车上线前都经过严格的安全检测，确保车辆状况良好。我们定期对车辆进行保养维护，
              配备齐全的安全设备和保险，让您出行无忧。
            </p>
          </div>

          <div class="value-card">
            <div class="value-card__icon value-card__icon--amber">
              <i class="fa-solid fa-bolt"></i>
            </div>
            <h3 class="value-card__title">高效便捷</h3>
            <p class="value-card__desc">
              线上预约、快速审核、极速取车，我们通过智能化平台简化租车流程。
              支持多种取还车方式，让您随时随地享受便捷的用车服务。
            </p>
          </div>

          <div class="value-card">
            <div class="value-card__icon value-card__icon--blue">
              <i class="fa-solid fa-heart"></i>
            </div>
            <h3 class="value-card__title">透明诚信</h3>
            <p class="value-card__desc">
              所有费用明码标价，无隐藏收费项目。租车合同条款清晰透明，
              押金退还流程规范，以诚信赢得每一位客户的信赖。
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Team ===== -->
    <section class="team">
      <div class="team__inner">
        <h2 class="team__heading">核心团队</h2>

        <div class="team__grid">
          <div class="team-card">
            <div class="team-card__avatar" style="background: linear-gradient(135deg, #3b82f6, #1d4ed8);">
              <span>张</span>
            </div>
            <h3 class="team-card__name">张伟</h3>
            <p class="team-card__title">创始人 &amp; CEO</p>
            <p class="team-card__desc">
              拥有15年汽车行业经验，曾任知名汽车集团高管。2020年创立爱租车，
              致力于打造中国领先的汽车租赁服务平台。
            </p>
          </div>

          <div class="team-card">
            <div class="team-card__avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
              <span>李</span>
            </div>
            <h3 class="team-card__name">李娜</h3>
            <p class="team-card__title">运营总监</p>
            <p class="team-card__desc">
              负责公司日常运营和客户服务工作，拥有丰富的客户管理经验。
              以客户满意为核心，持续优化服务流程和体验。
            </p>
          </div>

          <div class="team-card">
            <div class="team-card__avatar" style="background: linear-gradient(135deg, #10b981, #059669);">
              <span>王</span>
            </div>
            <h3 class="team-card__name">王磊</h3>
            <p class="team-card__title">技术总监</p>
            <p class="team-card__desc">
              带领技术团队打造智能化平台，曾任一线互联网公司技术负责人。
              推动技术创新，让租车服务更智能、更高效。
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== Footer (inline) ===== -->
    <footer class="footer">
      <div class="footer__inner">
        <div class="footer__grid">
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

          <div class="footer__col">
            <h4 class="footer__heading">快速链接</h4>
            <ul class="footer__links">
              <li><router-link to="/" class="footer__link">首页</router-link></li>
              <li><router-link to="/service" class="footer__link">服务特色</router-link></li>
              <li><router-link to="/cars" class="footer__link">车型展示</router-link></li>
              <li><router-link to="/about" class="footer__link">租车流程</router-link></li>
            </ul>
          </div>

          <div class="footer__col">
            <h4 class="footer__heading">服务支持</h4>
            <ul class="footer__links">
              <li><router-link to="/contact" class="footer__link">常见问题</router-link></li>
              <li><a href="#" class="footer__link">租车须知</a></li>
              <li><a href="#" class="footer__link">保险说明</a></li>
              <li><a href="#" class="footer__link">违章处理</a></li>
            </ul>
          </div>

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
          <p class="footer__copyright">
            &copy; 2026 爱租车汽车租赁服务平台 &middot; 粤ICP备XXXXXXXX号
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isScrolled = ref(false)

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* ================================================================
   Navbar – white glass-morphism
   ================================================================ */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(14px) saturate(160%);
  -webkit-backdrop-filter: blur(14px) saturate(160%);
  border-bottom: 1px solid var(--border);
  transition: background 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
}

.navbar--scrolled {
  background: rgba(255, 255, 255, 0.94);
  box-shadow: var(--shadow-md);
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
  color: var(--dark);
  flex-shrink: 0;
}

.navbar__logo-icon {
  font-size: 24px;
  color: var(--accent);
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
  color: var(--text);
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
  background: var(--accent);
  border-radius: 1px;
  transition: width 0.25s ease;
}

.navbar__links a:hover,
.navbar__links a.router-link-active {
  color: var(--accent);
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
  padding: 8px 20px;
  border-radius: 8px;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.navbar__btn--login {
  color: var(--text);
  background: transparent;
}

.navbar__btn--login:hover {
  background: var(--gray-100);
  color: var(--dark);
}

.navbar__btn--register {
  color: #ffffff;
  background: var(--accent);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.35);
}

.navbar__btn--register:hover {
  background: var(--primary);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.5);
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
  background: var(--text);
  border-radius: 2px;
  transition: transform 0.25s ease, opacity 0.25s ease;
}

/* ================================================================
   Hero
   ================================================================ */
.hero {
  margin-top: 64px;
  background: var(--surface);
  padding: 72px 24px 64px;
  text-align: center;
}

.hero__inner {
  max-width: 1200px;
  margin: 0 auto;
}

.hero__tag {
  display: inline-block;
  background: rgba(245, 158, 11, 0.12);
  color: #d97706;
  font-size: 0.8125rem;
  font-weight: 600;
  padding: 4px 14px;
  border-radius: 100px;
  margin-bottom: 16px;
}

.hero__title {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--dark);
  margin: 0 0 12px;
  letter-spacing: 0.03em;
}

.hero__subtitle {
  font-size: 1.125rem;
  color: var(--text-muted);
  margin: 0;
}

/* ================================================================
   Company Story
   ================================================================ */
.story {
  padding: 80px 24px;
}

.story__inner {
  max-width: 900px;
  margin: 0 auto;
  text-align: center;
}

.story__heading {
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 24px;
}

.story__text {
  font-size: 1.0625rem;
  line-height: 1.85;
  color: var(--text);
  margin: 0 0 48px;
}

.story__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 32px 16px;
  transition: box-shadow 0.25s ease, transform 0.25s ease;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-card__value {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--accent);
  margin-bottom: 6px;
}

.stat-card__label {
  font-size: 0.9375rem;
  color: var(--text-muted);
}

/* ================================================================
   Core Values
   ================================================================ */
.values {
  background: var(--surface);
  padding: 80px 24px;
}

.values__inner {
  max-width: 1080px;
  margin: 0 auto;
}

.values__heading {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 48px;
}

.values__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.value-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 36px 28px;
  text-align: center;
  transition: box-shadow 0.25s ease, transform 0.25s ease;
}

.value-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
}

.value-card__icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 28px;
  color: #ffffff;
}

.value-card__icon--green {
  background: linear-gradient(135deg, #10b981, #059669);
}

.value-card__icon--amber {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.value-card__icon--blue {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.value-card__title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 12px;
}

.value-card__desc {
  font-size: 0.9375rem;
  color: var(--text-muted);
  line-height: 1.7;
  margin: 0;
}

/* ================================================================
   Team
   ================================================================ */
.team {
  padding: 80px 24px;
}

.team__inner {
  max-width: 1000px;
  margin: 0 auto;
}

.team__heading {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 48px;
}

.team__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.team-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 36px 24px;
  text-align: center;
  transition: box-shadow 0.25s ease, transform 0.25s ease;
}

.team-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.team-card__avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 2rem;
  font-weight: 700;
  color: #ffffff;
}

.team-card__name {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 4px;
}

.team-card__title {
  font-size: 0.875rem;
  color: var(--accent);
  font-weight: 600;
  margin: 0 0 12px;
}

.team-card__desc {
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.65;
  margin: 0;
}

/* ================================================================
   Footer
   ================================================================ */
.footer {
  background: #0f172a;
  color: #94a3b8;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui, -apple-system, sans-serif;
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
  color: var(--accent);
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
  color: var(--accent);
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
  background: var(--accent);
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
  color: #94a3b8;
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

/* ================================================================
   Responsive
   ================================================================ */
@media (max-width: 768px) {
  .navbar__links,
  .navbar__actions {
    display: none;
  }

  .navbar__hamburger {
    display: flex;
  }

  .hero__title {
    font-size: 1.75rem;
  }

  .story__stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .values__grid,
  .team__grid {
    grid-template-columns: 1fr;
  }

  .footer__grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 540px) {
  .story__stats {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .stat-card {
    padding: 20px 12px;
  }

  .stat-card__value {
    font-size: 1.375rem;
  }

  .footer__grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .footer__col {
    text-align: center;
  }

  .footer__brand {
    justify-content: center;
  }

  .footer__links {
    align-items: center;
  }

  .footer__heading::after {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
