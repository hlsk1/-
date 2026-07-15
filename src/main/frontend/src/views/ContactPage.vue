<template>
  <div class="contact-page">
    <!-- ===== Navbar ===== -->
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
        <h1 class="hero__title">联系我们</h1>
        <p class="hero__subtitle">期待与您的沟通</p>
      </div>
    </section>

    <!-- ===== Content Area (2-column) ===== -->
    <section class="content">
      <div class="content__inner">
        <!-- Left: Contact Form -->
        <div class="content__left">
          <div class="form-card">
            <h2 class="form-card__heading"><i class="fa-solid fa-paper-plane"></i> 发送消息</h2>
            <form @submit.prevent="handleSubmit">
              <div class="form-group">
                <label class="form-group__label">姓名 <span class="required">*</span></label>
                <input
                  v-model="form.name"
                  type="text"
                  class="form-group__input"
                  placeholder="请输入您的姓名"
                  required
                />
              </div>

              <div class="form-group">
                <label class="form-group__label">手机号 <span class="required">*</span></label>
                <input
                  v-model="form.phone"
                  type="tel"
                  class="form-group__input"
                  placeholder="请输入您的手机号"
                  required
                />
              </div>

              <div class="form-group">
                <label class="form-group__label">咨询类型</label>
                <select v-model="form.type" class="form-group__input form-group__select">
                  <option value="rental">租车咨询</option>
                  <option value="service">售后服务</option>
                  <option value="cooperation">合作洽谈</option>
                  <option value="other">其他</option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-group__label">留言内容 <span class="required">*</span></label>
                <textarea
                  v-model="form.message"
                  class="form-group__input form-group__textarea"
                  rows="5"
                  placeholder="请描述您的问题或需求..."
                  required
                ></textarea>
              </div>

              <button type="submit" class="form-card__submit">
                <i class="fa-solid fa-paper-plane"></i> 发送消息
              </button>
            </form>
          </div>
        </div>

        <!-- Right: Contact Info -->
        <div class="content__right">
          <div class="info-card">
            <div class="info-card__icon">&#x1F4DE;</div>
            <div class="info-card__body">
              <h3 class="info-card__title">服务热线</h3>
              <p class="info-card__value">400-888-6688</p>
              <p class="info-card__note">周一至周日 8:00-22:00</p>
            </div>
          </div>

          <div class="info-card">
            <div class="info-card__icon">&#x1F4E7;</div>
            <div class="info-card__body">
              <h3 class="info-card__title">电子邮箱</h3>
              <p class="info-card__value">service@aizuche.com</p>
              <p class="info-card__note">24小时内回复</p>
            </div>
          </div>

          <div class="info-card">
            <div class="info-card__icon">&#x1F4CD;</div>
            <div class="info-card__body">
              <h3 class="info-card__title">公司地址</h3>
              <p class="info-card__value">广东省广州市天河区体育西路123号</p>
              <p class="info-card__note">欢迎预约参观</p>
            </div>
          </div>

          <div class="info-card">
            <div class="info-card__icon">&#x1F550;</div>
            <div class="info-card__body">
              <h3 class="info-card__title">营业时间</h3>
              <p class="info-card__value">周一至周日 8:00 - 22:00</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ===== FAQ Section ===== -->
    <section class="faq">
      <div class="faq__inner">
        <h2 class="faq__heading">常见问题</h2>

        <div class="faq__list">
          <details class="faq-item">
            <summary class="faq-item__question">
              <span>租车需要准备哪些证件？</span>
              <i class="fa-solid fa-chevron-down faq-item__arrow"></i>
            </summary>
            <div class="faq-item__answer">
              <p>租车时请准备好以下证件：</p>
              <ul>
                <li>本人有效身份证原件</li>
                <li>本人有效驾驶证原件（驾龄需满1年以上）</li>
                <li>本人名下银行卡或信用卡（用于支付押金和租金）</li>
              </ul>
              <p>部分车型可能还需要提供其他证明材料，具体以租车时门店要求为准。</p>
            </div>
          </details>

          <details class="faq-item">
            <summary class="faq-item__question">
              <span>可以异地还车吗？</span>
              <i class="fa-solid fa-chevron-down faq-item__arrow"></i>
            </summary>
            <div class="faq-item__answer">
              <p>我们支持部分城市之间的异地还车服务。异地还车将根据还车城市与取车城市之间的距离收取一定的异地还车费用，
              具体费用标准请在预订时咨询客服或查看订单详情。建议您在下单前确认目的地城市是否支持异地还车。</p>
            </div>
          </details>

          <details class="faq-item">
            <summary class="faq-item__question">
              <span>押金什么时候退还？</span>
              <i class="fa-solid fa-chevron-down faq-item__arrow"></i>
            </summary>
            <div class="faq-item__answer">
              <p>车辆归还后，如无违章记录和车辆损坏情况，押金将在<strong>15个工作日</strong>内原路退还至您的支付账户。
              如有未处理的违章，押金退还时间将相应延长，待违章处理完毕后予以退还。</p>
            </div>
          </details>

          <details class="faq-item">
            <summary class="faq-item__question">
              <span>如何延长租期？</span>
              <i class="fa-solid fa-chevron-down faq-item__arrow"></i>
            </summary>
            <div class="faq-item__answer">
              <p>如需延长租期，请您提前联系客服热线 <strong>400-888-6688</strong> 或通过在线客服渠道申请。
              延期是否成功取决于车辆后续的预订情况。建议您尽早申请，以便我们为您协调车辆安排。
              延期费用将按照原订单的日租金标准计算。</p>
            </div>
          </details>
        </div>
      </div>
    </section>

    <!-- ===== Footer ===== -->
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'

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

// ---- Form state ----
const form = reactive({
  name: '',
  phone: '',
  type: 'rental',
  message: ''
})

function handleSubmit() {
  alert('感谢您的留言，我们会尽快回复！')
  form.name = ''
  form.phone = ''
  form.type = 'rental'
  form.message = ''
}
</script>

<style scoped>
/* ================================================================
   Navbar
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
   Content Area (2-column)
   ================================================================ */
.content {
  padding: 64px 24px;
}

.content__inner {
  max-width: 1100px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 40px;
  align-items: start;
}

/* ---- Left: Form ---- */
.form-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 36px 32px;
}

.form-card__heading {
  font-size: 1.375rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 28px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-card__heading i {
  color: var(--accent);
}

.form-group {
  margin-bottom: 20px;
}

.form-group__label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.required {
  color: #ef4444;
}

.form-group__input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  font-size: 0.9375rem;
  font-family: inherit;
  color: var(--text);
  background: #ffffff;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.form-group__input:focus {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
}

.form-group__input::placeholder {
  color: var(--gray-400);
}

.form-group__select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%236b7280' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 36px;
}

.form-group__textarea {
  resize: vertical;
  min-height: 120px;
}

.form-card__submit {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: var(--accent);
  color: #ffffff;
  border: none;
  border-radius: var(--radius);
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.form-card__submit:hover {
  background: var(--primary);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.35);
}

/* ---- Right: Info Cards ---- */
.info-card {
  display: flex;
  gap: 16px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 16px;
  transition: box-shadow 0.2s ease;
}

.info-card:hover {
  box-shadow: var(--shadow-sm);
}

.info-card__icon {
  font-size: 28px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface);
  border-radius: 12px;
  flex-shrink: 0;
}

.info-card__body {
  flex: 1;
}

.info-card__title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-muted);
  margin: 0 0 4px;
}

.info-card__value {
  font-size: 1.0625rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 4px;
}

.info-card__note {
  font-size: 0.8125rem;
  color: var(--gray-400);
  margin: 0;
}

/* ================================================================
   FAQ
   ================================================================ */
.faq {
  background: var(--surface);
  padding: 80px 24px;
}

.faq__inner {
  max-width: 800px;
  margin: 0 auto;
}

.faq__heading {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  color: var(--dark);
  margin: 0 0 40px;
}

.faq__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.faq-item {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  transition: box-shadow 0.2s ease;
}

.faq-item[open] {
  box-shadow: var(--shadow-sm);
}

.faq-item__question {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  color: var(--dark);
  user-select: none;
  list-style: none;
}

.faq-item__question::-webkit-details-marker {
  display: none;
}

.faq-item__question::marker {
  display: none;
}

.faq-item__arrow {
  font-size: 0.8125rem;
  color: var(--text-muted);
  transition: transform 0.25s ease;
  flex-shrink: 0;
}

.faq-item[open] .faq-item__arrow {
  transform: rotate(180deg);
}

.faq-item__answer {
  padding: 0 24px 20px;
  font-size: 0.9375rem;
  line-height: 1.75;
  color: var(--text);
}

.faq-item__answer p {
  margin: 0 0 8px;
}

.faq-item__answer p:last-child {
  margin-bottom: 0;
}

.faq-item__answer ul {
  margin: 0 0 8px;
  padding-left: 20px;
}

.faq-item__answer li {
  margin-bottom: 4px;
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

  .content__inner {
    grid-template-columns: 1fr;
  }

  .form-card {
    padding: 24px 20px;
  }

  .footer__grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 540px) {
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
