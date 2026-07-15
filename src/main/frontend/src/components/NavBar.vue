<template>
  <nav class="navbar" :class="{ 'navbar--scrolled': isScrolled }">
    <div class="navbar__inner">
      <!-- Brand / Logo -->
      <router-link to="/" class="navbar__brand">
        <i class="fa-solid fa-car navbar__logo-icon"></i>
        <span class="navbar__brand-text">爱租车</span>
      </router-link>

      <!-- Desktop nav links -->
      <ul class="navbar__links">
        <li><router-link to="/">首页</router-link></li>
        <li><router-link to="/service">服务</router-link></li>
        <li><router-link to="/cars">车型</router-link></li>
        <li><router-link to="/about">关于</router-link></li>
        <li><router-link to="/contact">联系</router-link></li>
      </ul>

      <!-- Auth buttons -->
      <div class="navbar__actions">
        <router-link to="/login" class="navbar__btn navbar__btn--login">登录</router-link>
        <router-link to="/register" class="navbar__btn navbar__btn--register">免费注册</router-link>
      </div>

      <!-- Mobile hamburger (visual only, you can wire up a menu later) -->
      <button class="navbar__hamburger" aria-label="Toggle menu">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isScrolled = ref(false)

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  handleScroll() // in case the page is already scrolled on mount
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* -----------------------------------------------
   Variables
----------------------------------------------- */
:root {
  --nav-height: 64px;
  --nav-bg: rgba(18, 18, 18, 0.65);
  --nav-bg-scrolled: rgba(18, 18, 18, 0.88);
  --nav-text: #f1f5f9;
  --nav-text-hover: #ffffff;
  --accent: #3b82f6;
  --accent-hover: #2563eb;
}

/* -----------------------------------------------
   Base
----------------------------------------------- */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--nav-height);
  background: var(--nav-bg);
  backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  transition: background 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', system-ui,
    -apple-system, sans-serif;
}

.navbar--scrolled {
  background: var(--nav-bg-scrolled);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.35);
}

/* -----------------------------------------------
   Inner container
----------------------------------------------- */
.navbar__inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

/* -----------------------------------------------
   Brand
----------------------------------------------- */
.navbar__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: var(--nav-text);
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

/* -----------------------------------------------
   Nav links
----------------------------------------------- */
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
  color: var(--nav-text);
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
  color: var(--nav-text-hover);
}

.navbar__links a:hover::after,
.navbar__links a.router-link-active::after {
  width: 100%;
}

/* -----------------------------------------------
   Auth buttons
----------------------------------------------- */
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
  color: var(--nav-text);
  background: transparent;
}

.navbar__btn--login:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--nav-text-hover);
}

.navbar__btn--register {
  color: #ffffff;
  background: var(--accent);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.35);
}

.navbar__btn--register:hover {
  background: var(--accent-hover);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.5);
}

/* -----------------------------------------------
   Hamburger (mobile)
----------------------------------------------- */
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
  background: var(--nav-text);
  border-radius: 2px;
  transition: transform 0.25s ease, opacity 0.25s ease;
}

/* -----------------------------------------------
   Responsive
----------------------------------------------- */
@media (max-width: 768px) {
  .navbar__links {
    display: none;
  }

  .navbar__actions {
    display: none;
  }

  .navbar__hamburger {
    display: flex;
  }
}
</style>
