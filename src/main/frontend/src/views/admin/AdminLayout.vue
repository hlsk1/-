<template>
  <div class="admin-layout">
    <!-- ===== Sidebar ===== -->
    <aside class="admin-sidebar">
      <!-- Brand -->
      <div class="sidebar-brand">
        <div class="sidebar-brand__logo">
          <i class="fa-solid fa-car"></i>
        </div>
        <span class="sidebar-brand__name">爱租车</span>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <!-- 业务管理 -->
        <div class="nav-section">业务管理</div>
        <router-link to="/admin" class="nav-item" exact-active-class="active">
          <span class="nav-icon">📊</span>
          <span>数据看板</span>
        </router-link>
        <router-link to="/admin/vehicles" class="nav-item" active-class="active">
          <span class="nav-icon">🚗</span>
          <span>车辆管理</span>
        </router-link>
        <router-link to="/admin/customers" class="nav-item" active-class="active">
          <span class="nav-icon">👥</span>
          <span>客户管理</span>
        </router-link>
        <router-link to="/admin/orders" class="nav-item" active-class="active">
          <span class="nav-icon">📋</span>
          <span>订单管理</span>
        </router-link>
        <router-link to="/admin/violations" class="nav-item" active-class="active">
          <span class="nav-icon">⚠️</span>
          <span>违章管理</span>
        </router-link>

        <!-- 系统管理 -->
        <div class="nav-section">系统管理</div>
        <router-link to="/admin/statistics" class="nav-item" active-class="active">
          <span class="nav-icon">📈</span>
          <span>统计报表</span>
        </router-link>
        <router-link
          v-if="auth.isSuper"
          to="/admin/users"
          class="nav-item"
          active-class="active"
        >
          <span class="nav-icon">👤</span>
          <span>用户管理</span>
        </router-link>
        <router-link
          v-if="auth.isSuper"
          to="/admin/config"
          class="nav-item"
          active-class="active"
        >
          <span class="nav-icon">⚙️</span>
          <span>系统配置</span>
        </router-link>
      </nav>

      <!-- Footer: user info + logout -->
      <div class="sidebar-footer">
        <div class="avatar avatar-accent">{{ auth.userAvatar }}</div>
        <div class="sidebar-footer__info">
          <div class="sidebar-footer__name">{{ auth.userName }}</div>
          <div class="sidebar-footer__role">{{ auth.roleLabel }}</div>
        </div>
        <button class="sidebar-footer__logout" title="退出登录" @click="handleLogout">
          <i class="fa-solid fa-right-from-bracket"></i>
        </button>
      </div>
    </aside>

    <!-- ===== Main Area ===== -->
    <div class="admin-main">
      <!-- Topbar -->
      <header class="admin-topbar">
        <div class="breadcrumb">
          <i class="fa-solid fa-house"></i>
          <span class="breadcrumb__sep">/</span>
          <span class="breadcrumb__current">{{ currentPageName }}</span>
        </div>
        <div class="topbar-clock">{{ currentTime }}</div>
      </header>

      <!-- Content -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { ref, computed, onMounted, onUnmounted } from 'vue'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

// ---- Page name mapping ----
const pageNameMap = {
  Dashboard: '数据看板',
  AdminVehicles: '车辆管理',
  AdminCustomers: '客户管理',
  AdminOrders: '订单管理',
  AdminViolations: '违章管理',
  AdminStatistics: '统计报表',
  AdminUsers: '用户管理',
  AdminConfig: '系统配置'
}

const currentPageName = computed(() => {
  return pageNameMap[route.name] || route.name || '管理后台'
})

// ---- Live clock ----
const currentTime = ref('')

function pad(n) {
  return String(n).padStart(2, '0')
}

function updateClock() {
  const now = new Date()
  const y = now.getFullYear()
  const mo = pad(now.getMonth() + 1)
  const d = pad(now.getDate())
  const h = pad(now.getHours())
  const mi = pad(now.getMinutes())
  const s = pad(now.getSeconds())
  currentTime.value = `${y}-${mo}-${d} ${h}:${mi}:${s}`
}

let clockTimer = null

onMounted(() => {
  updateClock()
  clockTimer = setInterval(updateClock, 1000)
})

onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
})

// ---- Logout ----
function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
/* ===== Layout ===== */
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-main {
  flex: 1;
  margin-left: 240px;
  min-height: 100vh;
  background: var(--bg);
}

/* ===== Sidebar ===== */
.admin-sidebar {
  width: 240px;
  background: #0f172a;
  color: #fff;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  z-index: 100;
  overflow-y: auto;
}

/* Brand */
.sidebar-brand {
  padding: 1.25rem 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.625rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-brand__logo {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.125rem;
  color: #0f172a;
  flex-shrink: 0;
}

.sidebar-brand__name {
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.04em;
  white-space: nowrap;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 0.75rem 0;
}

.nav-section {
  padding: 0.75rem 1.5rem 0.25rem;
  font-size: 0.625rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: rgba(255, 255, 255, 0.25);
  font-weight: 600;
  white-space: nowrap;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.5rem 1.5rem;
  margin: 2px 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.55);
  transition: all 0.15s;
  text-decoration: none;
  white-space: nowrap;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
}

.nav-item.active {
  background: rgba(37, 99, 235, 0.25);
  color: #60a5fa;
  font-weight: 500;
}

.nav-icon {
  width: 20px;
  text-align: center;
  font-size: 0.875rem;
  flex-shrink: 0;
}

/* Footer */
.sidebar-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  gap: 0.625rem;
  font-size: 0.8125rem;
  color: rgba(255, 255, 255, 0.45);
}

.sidebar-footer__info {
  flex: 1;
  min-width: 0;
}

.sidebar-footer__name {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  font-size: 0.8125rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-footer__role {
  font-size: 0.6875rem;
  color: rgba(255, 255, 255, 0.35);
  white-space: nowrap;
}

.sidebar-footer__logout {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.35);
  cursor: pointer;
  padding: 4px 6px;
  border-radius: 4px;
  font-size: 0.875rem;
  transition: color 0.15s, background 0.15s;
  flex-shrink: 0;
}

.sidebar-footer__logout:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

/* ===== Topbar ===== */
.admin-topbar {
  background: #fff;
  border-bottom: 1px solid var(--border);
  padding: 0 1.5rem;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 50;
}

.breadcrumb {
  font-size: 0.8125rem;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 0.375rem;
}

.breadcrumb__sep {
  color: var(--gray-300);
}

.breadcrumb__current {
  color: var(--text);
  font-weight: 600;
}

.topbar-clock {
  font-size: 0.8125rem;
  color: var(--text-muted);
  font-variant-numeric: tabular-nums;
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
}

/* ===== Content ===== */
.admin-content {
  padding: 1.5rem;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .admin-sidebar {
    width: 56px;
  }

  .sidebar-brand__name,
  .nav-item span:not(.nav-icon),
  .nav-section,
  .sidebar-footer__info {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 0.5rem;
    margin: 2px 0.25rem;
  }

  .sidebar-brand {
    justify-content: center;
    padding: 1rem 0.5rem;
  }

  .sidebar-footer {
    justify-content: center;
    padding: 0.75rem 0.5rem;
  }

  .admin-main {
    margin-left: 56px;
  }
}
</style>
