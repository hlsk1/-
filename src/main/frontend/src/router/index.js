import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomePage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterPage.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPwd',
    component: () => import('@/views/ForgotPwdPage.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/AboutPage.vue')
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('@/views/ContactPage.vue')
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('@/views/ReviewsPage.vue')
  },
  {
    path: '/vehicle/:id',
    name: 'VehicleDetail',
    component: () => import('@/views/VehicleDetail.vue')
  },
  {
    path: '/user',
    name: 'UserHome',
    component: () => import('@/views/UserHome.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'vehicles', name: 'AdminVehicles', component: () => import('@/views/admin/Vehicles.vue') },
      { path: 'customers', name: 'AdminCustomers', component: () => import('@/views/admin/Customers.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/Orders.vue') },
      { path: 'violations', name: 'AdminViolations', component: () => import('@/views/admin/Violations.vue') },
      { path: 'statistics', name: 'AdminStatistics', component: () => import('@/views/admin/Statistics.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') },
      { path: 'config', name: 'AdminConfig', component: () => import('@/views/admin/Config.vue') }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null')

  if (to.meta.requiresAuth && !user) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && user) {
    const role = user.role
    if (role !== 'SUPER' && role !== 'ADMIN') {
      next('/user')
      return
    }
  }

  next()
})

export default router
