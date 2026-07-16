import axios from 'axios'

const http = axios.create({
  baseURL: '',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error.message)
    return Promise.reject(error)
  }
)

async function request(method, url, data = null, params = null) {
  try {
    const res = await http({ method, url, data, params })
    return res.data
  } catch (e) {
    return { code: 500, msg: e.message || '网络请求失败', data: null }
  }
}

export const $get = (url, params) => request('get', url, null, params)
export const $post = (url, data) => request('post', url, data)
export const $put = (url, data) => request('put', url, data)
export const $del = (url) => request('delete', url)

// ====== User API ======
export const userApi = {
  login: (data) => $post('/api/user/login', data),
  list: (params) => $get('/api/user/list', params),
  getById: (id) => $get(`/api/user/${id}`),
  add: (data) => $post('/api/user/add', data),
  edit: (id, data) => $put(`/api/user/edit/${id}`, data),
  resetPassword: (id) => $post(`/api/user/resetPassword/${id}`),
  delete: (id) => $del(`/api/user/delete/${id}`),
  logs: (id) => $get(`/api/user/logs/${id}`)
}

// ====== Vehicle API ======
export const vehicleApi = {
  list: (params) => $get('/api/vehicle/list', params),
  detail: (id) => $get(`/api/vehicle/detail/${id}`),
  add: (data) => $post('/api/vehicle/add', data),
  edit: (id, data) => $put(`/api/vehicle/edit/${id}`, data),
  parking: (params) => $post('/api/vehicle/parking', null, params),
  repair: (params) => $post('/api/vehicle/repair', null, params),
  repairComplete: (id) => $post(`/api/vehicle/repairComplete/${id}`),
  statusSummary: () => $get('/api/vehicle/statusSummary'),
  export: (fields) => $get('/api/vehicle/export', fields)
}

// ====== Customer API ======
export const customerApi = {
  list: (params) => $get('/api/customer/list', params),
  detail: (id) => $get(`/api/customer/detail/${id}`),
  add: (data) => $post('/api/customer/add', data),
  edit: (id, data) => $put(`/api/customer/edit/${id}`, data),
  blacklist: (id, reason) => $post(`/api/customer/blacklist/${id}`, null, { reason }),
  unblacklist: (id) => $post(`/api/customer/unblacklist/${id}`),
  getBlacklist: () => $get('/api/customer/blacklist'),
  addPoints: (id, points) => $post(`/api/customer/addPoints/${id}`, null, { points })
}

// ====== Order API ======
export const orderApi = {
  create: (data) => $post('/api/order/create', data),
  pickup: (id) => $post(`/api/order/pickup/${id}`),
  return: (params) => $post('/api/order/return', null, params),
  cancel: (id, reason) => $post(`/api/order/cancel/${id}`, null, { reason }),
  list: (params) => $get('/api/order/list', params),
  detail: (id) => $get(`/api/order/detail/${id}`),
  todayPickup: () => $get('/api/order/todayPickup'),
  todayReturn: () => $get('/api/order/todayReturn'),
  markAbnormal: (params) => $post('/api/order/markAbnormal', null, params)
}

// ====== Violation API ======
export const violationApi = {
  list: (params) => $get('/api/violation/list', params),
  pending: () => $get('/api/violation/pending'),
  detail: (id) => $get(`/api/violation/detail/${id}`),
  add: (data) => $post('/api/violation/add', data),
  edit: (id, data) => $put(`/api/violation/edit/${id}`, data),
  resolve: (id, resolver) => $post(`/api/violation/resolve/${id}`, null, { resolver }),
  statsByVehicle: () => $get('/api/violation/statistics/byVehicle'),
  statsByCustomer: () => $get('/api/violation/statistics/byCustomer')
}

// ====== Review API ======
export const reviewApi = {
  list: () => $get('/api/review/list'),
  popular: () => $get('/api/review/popular'),
  byVehicle: (vehicleId) => $get(`/api/review/vehicle/${vehicleId}`),
  byCustomer: (customerId) => $get(`/api/review/customer/${customerId}`),
  byOrder: (orderId) => $get(`/api/review/order/${orderId}`),
  check: (orderId) => $get(`/api/review/check/${orderId}`),
  add: (data) => $post('/api/review/add', data),
  like: (id) => $post(`/api/review/like/${id}`),
  view: (id) => $post(`/api/review/view/${id}`)
}

// ====== Statistics API ======
export const statisticsApi = {
  overview: () => $get('/api/statistics/overview'),
  orderTrend: () => $get('/api/statistics/orderTrend'),
  vehicleUtilization: () => $get('/api/statistics/vehicleUtilization')
}

// ====== Config API ======
export const configApi = {
  list: () => $get('/api/config/list'),
  map: () => $get('/api/config/map'),
  getByKey: (key) => $get(`/api/config/get/${key}`),
  save: (key, value, description) => $post('/api/config/save', null, { key, value, description }),
  saveBatch: (data) => $post('/api/config/saveBatch', data),
  delete: (key) => $del(`/api/config/delete/${key}`)
}

// ====== Upload API ======
export const uploadApi = {
  image: async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await http.post('/api/upload/image', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      return res.data
    } catch (e) {
      return { code: 500, msg: '上传失败', data: null }
    }
  },
  images: async (files) => {
    const formData = new FormData()
    files.forEach(f => formData.append('files', f))
    try {
      const res = await http.post('/api/upload/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      return res.data
    } catch (e) {
      return { code: 500, msg: '上传失败', data: null }
    }
  }
}

// ====== Auth API ======
export const authApi = {
  register: (data) => $post('/api/register', data),
  forgotPassword: (username) => $post('/api/forgot-password', null, { username })
}

export default http
