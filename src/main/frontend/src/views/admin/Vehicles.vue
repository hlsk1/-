<template>
  <div class="admin-page">
    <div class="page-header">
      <h2 class="page-title">车辆管理</h2>
    </div>

    <div class="toast" v-if="toast.show" :class="toast.type">{{ toast.msg }}</div>

    <div class="toolbar">
      <div class="toolbar-left">
        <button class="btn btn-primary" @click="openAddModal">+ 新增车辆</button>
        <button class="btn btn-outline" @click="handleExport">导出</button>
        <div class="view-toggle">
          <button class="toggle-btn" :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'">列表</button>
          <button class="toggle-btn" :class="{ active: viewMode === 'card' }" @click="viewMode = 'card'">卡片</button>
        </div>
      </div>
      <div class="toolbar-right">
        <input
          v-model="searchKey"
          type="text"
          class="search-input"
          placeholder="搜索品牌/型号/车牌..."
          @input="onSearch"
        />
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <!-- List View -->
    <table v-else-if="viewMode === 'list'" class="data-table">
      <thead>
        <tr>
          <th>品牌/型号</th>
          <th>车牌</th>
          <th>颜色</th>
          <th>座位</th>
          <th>日租金</th>
          <th>状态</th>
          <th>车位</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="v in filteredVehicles" :key="v.id">
          <td>{{ v.brand }} {{ v.model }}</td>
          <td>{{ v.plateNumber }}</td>
          <td>{{ v.color }}</td>
          <td>{{ v.seatCount }}</td>
          <td>&yen;{{ v.dailyPrice }}</td>
          <td><span class="badge" :class="statusBadgeClass(v.status)">{{ statusMap(v.status) }}</span></td>
          <td>{{ v.parkingSpot || '-' }}</td>
          <td class="action-cell">
            <button class="btn btn-sm btn-outline" @click="openEditModal(v)">编辑</button>
            <button
              v-if="v.status !== 'REPAIR'"
              class="btn btn-sm btn-warning"
              @click="handleSetRepair(v)"
            >维修</button>
            <button
              v-if="v.status === 'REPAIR'"
              class="btn btn-sm btn-success"
              @click="handleCompleteRepair(v)"
            >完成维修</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Card View -->
    <div v-else class="card-grid">
      <div v-for="v in filteredVehicles" :key="v.id" class="vehicle-card">
        <div class="card-img">
          <img v-if="getVehicleImg(v)" :src="getVehicleImg(v)" :alt="v.model" />
          <div v-else class="img-fallback">&#x1F698;</div>
          <span class="card-status-badge" :class="statusBadgeClass(v.status)">{{ statusMap(v.status) }}</span>
        </div>
        <div class="card-body">
          <h4>{{ v.brand }} {{ v.model }}</h4>
          <div class="card-meta">
            <span>{{ v.plateNumber }} | {{ v.color }} | {{ v.seatCount }}座</span>
          </div>
          <div class="card-footer">
            <strong class="price">&yen;{{ v.dailyPrice }}<small>/天</small></strong>
            <div class="card-actions">
              <button class="btn btn-sm btn-outline" @click="openEditModal(v)">编辑</button>
              <button
                v-if="v.status !== 'REPAIR'"
                class="btn btn-sm btn-warning"
                @click="handleSetRepair(v)"
              >维修</button>
              <button
                v-if="v.status === 'REPAIR'"
                class="btn btn-sm btn-success"
                @click="handleCompleteRepair(v)"
              >完成维修</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal-overlay" v-if="modalVisible" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑车辆' : '新增车辆' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>品牌 <span class="required">*</span></label>
            <input v-model="form.brand" class="form-input" placeholder="品牌" />
          </div>
          <div class="form-group">
            <label>型号 <span class="required">*</span></label>
            <input v-model="form.model" class="form-input" placeholder="型号" />
          </div>
          <div class="form-group">
            <label>车牌号 <span class="required">*</span></label>
            <input v-model="form.plateNumber" class="form-input" placeholder="车牌号" />
          </div>
          <div class="form-group">
            <label>颜色</label>
            <input v-model="form.color" class="form-input" placeholder="颜色" />
          </div>
          <div class="form-group">
            <label>座位数</label>
            <input v-model.number="form.seatCount" type="number" class="form-input" placeholder="座位数" />
          </div>
          <div class="form-group">
            <label>日租金 <span class="required">*</span></label>
            <input v-model.number="form.dailyPrice" type="number" class="form-input" placeholder="日租金" />
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="form.status" class="form-input">
              <option value="AVAILABLE">可用</option>
              <option value="RENTED">已租</option>
              <option value="REPAIR">维修中</option>
              <option value="OFFLINE">下架</option>
            </select>
          </div>
          <div class="form-group">
            <label>车位</label>
            <input v-model="form.parkingSpot" class="form-input" placeholder="车位" />
          </div>
          <div class="form-group">
            <label>停车时间</label>
            <input v-model="form.parkingTime" type="datetime-local" class="form-input" />
          </div>
          <div class="form-group">
            <label>车辆图片</label>
            <div class="upload-area" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
              <input
                ref="fileInputRef"
                type="file"
                accept="image/*"
                multiple
                style="display:none"
                @change="handleFileSelect"
              />
              <div v-if="uploadedUrls.length > 0" class="upload-previews">
                <div v-for="(url, i) in uploadedUrls" :key="i" class="upload-preview-item">
                  <img :src="url" class="upload-preview-img" />
                  <button class="upload-preview-remove" @click.stop="removeUploadedUrl(i)">&times;</button>
                </div>
                <div class="upload-preview-item upload-add-btn" @click.stop="triggerFileInput">
                  <span>+</span>
                </div>
              </div>
              <div v-else class="upload-placeholder">
                <i class="fa fa-cloud-upload"></i>
                <span>点击或拖拽图片到此处上传</span>
                <small>支持 JPG/PNG，可多选</small>
              </div>
            </div>
            <div v-if="uploading" class="upload-status">
              <i class="fa fa-spinner fa-spin"></i> 上传中...
            </div>
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="form.description" class="form-input form-textarea" placeholder="描述信息"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="handleSubmit" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { vehicleApi, uploadApi } from '@/api'

const STATUS_MAP = { AVAILABLE: '可用', RENTED: '已租', REPAIR: '维修中', OFFLINE: '下架' }
const statusMap = (s) => STATUS_MAP[s] || s

function statusBadgeClass(s) {
  if (s === 'AVAILABLE') return 'badge-green'
  if (s === 'RENTED') return 'badge-blue'
  if (s === 'REPAIR') return 'badge-amber'
  return 'badge-gray'
}

function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

const vehicles = ref([])
const searchKey = ref('')
const viewMode = ref('list')
const loading = ref(false)
const modalVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const uploading = ref(false)
const fileInputRef = ref(null)
const uploadedUrls = ref([])
const toast = reactive({ show: false, msg: '', type: 'success' })

const UPLOAD_FILES = [
  '20260714_0ea2c5b3.jpg','20260714_292125e4.jpg','20260714_3c12e78e.jpg',
  '20260714_4f342939.jpg','20260714_58b3de9c.jpg','20260714_5a18ba75.jpg',
  '20260714_6ba438a6.jpg','20260714_826103fa.jpg','20260714_8f852483.jpg',
  '20260714_94293e3d.jpg','20260714_9bfc047b.jpg','20260714_b8a0dfb1.jpg',
  '20260714_c2b7012e.jpg','20260714_d00dd179.jpg','20260714_ec01e985.jpg',
  '20260714_fb841242.jpg'
]

function getVehicleImg(v) {
  if (v.imageUrls) {
    if (Array.isArray(v.imageUrls) && v.imageUrls[0]) return v.imageUrls[0]
    const first = v.imageUrls.split(',')[0]?.trim()
    if (first) return first
  }
  // Fallback: random uploads image
  const idx = (v.id || 1) % UPLOAD_FILES.length
  return `/uploads/vehicles/${UPLOAD_FILES[idx]}`
}

const filteredVehicles = computed(() => {
  if (!searchKey.value) return vehicles.value
  const kw = searchKey.value.toLowerCase()
  return vehicles.value.filter(v =>
    (v.brand || '').toLowerCase().includes(kw) ||
    (v.model || '').toLowerCase().includes(kw) ||
    (v.plateNumber || '').toLowerCase().includes(kw)
  )
})

const form = reactive({
  brand: '',
  model: '',
  plateNumber: '',
  color: '',
  seatCount: null,
  dailyPrice: null,
  status: 'AVAILABLE',
  parkingSpot: '',
  parkingTime: '',
  imageUrls: '',
  description: ''
})

function showToast(msg, type = 'success') {
  toast.show = true
  toast.msg = msg
  toast.type = type
  setTimeout(() => { toast.show = false }, 2500)
}

function resetForm() {
  form.brand = ''
  form.model = ''
  form.plateNumber = ''
  form.color = ''
  form.seatCount = null
  form.dailyPrice = null
  form.status = 'AVAILABLE'
  form.parkingSpot = ''
  form.parkingTime = ''
  form.description = ''
  uploadedUrls.value = []
  uploading.value = false
}

async function loadVehicles() {
  loading.value = true
  try {
    const res = await vehicleApi.list()
    vehicles.value = res.data || []
  } catch (e) {
    showToast('加载车辆列表失败', 'error')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  // computed property handles filtering
}

function openAddModal() {
  isEdit.value = false
  editId.value = null
  resetForm()
  modalVisible.value = true
}

function openEditModal(v) {
  isEdit.value = true
  editId.value = v.id
  form.brand = v.brand || ''
  form.model = v.model || ''
  form.plateNumber = v.plateNumber || ''
  form.color = v.color || ''
  form.seatCount = v.seatCount
  form.dailyPrice = v.dailyPrice
  form.status = v.status || 'AVAILABLE'
  form.parkingSpot = v.parkingSpot || ''
  form.parkingTime = v.parkingTime ? v.parkingTime.substring(0, 16) : ''
  form.description = v.description || ''
  // Populate existing images
  if (v.imageUrls) {
    if (Array.isArray(v.imageUrls)) {
      uploadedUrls.value = [...v.imageUrls]
    } else if (typeof v.imageUrls === 'string' && v.imageUrls.trim()) {
      uploadedUrls.value = v.imageUrls.split(',').map(s => s.trim()).filter(Boolean)
    } else {
      uploadedUrls.value = []
    }
  } else {
    uploadedUrls.value = []
  }
  modalVisible.value = true
}

function closeModal() {
  modalVisible.value = false
  resetForm()
}

function triggerFileInput() {
  fileInputRef.value?.click()
}

function handleFileSelect(e) {
  const files = e.target.files
  if (files && files.length > 0) {
    uploadFiles(Array.from(files))
  }
}

function handleDrop(e) {
  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    uploadFiles(Array.from(files))
  }
}

async function uploadFiles(files) {
  uploading.value = true
  try {
    const res = await uploadApi.images(files)
    if (res.code === 200 && res.data?.urls) {
      uploadedUrls.value = [...uploadedUrls.value, ...res.data.urls]
      showToast(`成功上传 ${res.data.urls.length} 张图片`)
    } else {
      showToast(res.msg || '上传失败', 'error')
    }
  } catch {
    showToast('上传失败', 'error')
  } finally {
    uploading.value = false
  }
}

function removeUploadedUrl(index) {
  uploadedUrls.value.splice(index, 1)
}

async function handleSubmit() {
  if (!form.brand || !form.model || !form.plateNumber || !form.dailyPrice) {
    showToast('请填写必填字段（品牌、型号、车牌号、日租金）', 'error')
    return
  }
  submitting.value = true
  try {
    const data = {
      ...form,
      imageUrls: uploadedUrls.value.join(',')
    }
    let res
    if (isEdit.value) {
      res = await vehicleApi.edit(editId.value, data)
    } else {
      res = await vehicleApi.add(data)
    }
    if (res.code === 200) {
      showToast(isEdit.value ? '编辑成功' : '新增成功')
      closeModal()
      await loadVehicles()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  } finally {
    submitting.value = false
  }
}

async function handleSetRepair(v) {
  const reason = prompt('请输入维修原因：')
  if (!reason) return
  const expectedTime = prompt('请输入预计完成时间（如：2026-07-20T10:00）：')
  try {
    const res = await vehicleApi.repair({ id: v.id, reason, expectedRepairTime: expectedTime || undefined })
    if (res.code === 200) {
      showToast('已标记为维修中')
      await loadVehicles()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

async function handleCompleteRepair(v) {
  try {
    const res = await vehicleApi.repairComplete(v.id)
    if (res.code === 200) {
      showToast('维修已完成，车辆已恢复为可用')
      await loadVehicles()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

async function handleExport() {
  try {
    const res = await vehicleApi.export()
    if (res.code === 200 && res.data) {
      window.open(res.data, '_blank')
      showToast('导出成功')
    } else {
      showToast(res.msg || '导出失败', 'error')
    }
  } catch (e) {
    showToast('导出失败', 'error')
  }
}

onMounted(() => {
  loadVehicles()
})
</script>

<style scoped>
.admin-page { padding: 20px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 22px; font-weight: 700; color: #1a1a1a; margin: 0; }

.toast {
  position: fixed; top: 20px; right: 20px; z-index: 9999;
  padding: 12px 24px; border-radius: 8px; color: #fff; font-size: 14px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15); transition: opacity 0.3s;
}
.toast.success { background: #52c41a; }
.toast.error { background: #ff4d4f; }

.toolbar {
  display: flex; justify-content: space-between; align-items: center;
  flex-wrap: wrap; gap: 12px; margin-bottom: 20px;
}
.toolbar-left { display: flex; align-items: center; gap: 10px; }
.toolbar-right { display: flex; align-items: center; gap: 10px; }
.search-input {
  padding: 8px 14px; border: 1px solid #d9d9d9; border-radius: 6px;
  font-size: 14px; width: 240px; outline: none; transition: border-color 0.2s;
}
.search-input:focus { border-color: #4096ff; }

.view-toggle { display: flex; border: 1px solid #d9d9d9; border-radius: 6px; overflow: hidden; }
.toggle-btn {
  padding: 7px 16px; border: none; background: #fff; cursor: pointer;
  font-size: 13px; color: #666; transition: all 0.2s;
}
.toggle-btn.active { background: #4096ff; color: #fff; }
.toggle-btn:not(:last-child) { border-right: 1px solid #d9d9d9; }

.loading { text-align: center; padding: 60px 0; color: #999; font-size: 15px; }

.data-table {
  width: 100%; border-collapse: collapse; background: #fff;
  border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.data-table th, .data-table td {
  padding: 12px 14px; text-align: left; border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}
.data-table th { background: #fafafa; font-weight: 600; color: #333; }
.data-table tr:hover td { background: #fafafa; }
.action-cell { display: flex; gap: 6px; flex-wrap: wrap; }

.badge {
  display: inline-block; padding: 3px 10px; border-radius: 12px;
  font-size: 12px; font-weight: 600; color: #fff;
}
.badge-green { background: #52c41a; }
.badge-blue { background: #1677ff; }
.badge-amber { background: #fa8c16; }
.badge-gray { background: #8c8c8c; }

.card-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.vehicle-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.2s, box-shadow 0.2s;
}
.vehicle-card:hover { transform: translateY(-4px); box-shadow: 0 6px 20px rgba(0,0,0,0.12); }

.card-img {
  position: relative; width: 100%; height: 180px; overflow: hidden; background: #f5f5f5;
}
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.img-fallback {
  width: 100%; height: 100%; display: flex; align-items: center;
  justify-content: center; font-size: 52px; background: #e8e8e8;
}
.card-status-badge {
  position: absolute; top: 10px; right: 10px;
  padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: #fff;
}

.card-body { padding: 14px 16px 16px; }
.card-body h4 { margin: 0 0 6px; font-size: 16px; font-weight: 600; color: #222; }
.card-meta { font-size: 13px; color: #888; margin-bottom: 12px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.price { font-size: 18px; color: #f56c6c; }
.price small { font-size: 12px; color: #999; margin-left: 2px; }
.card-actions { display: flex; gap: 6px; }

.btn {
  padding: 8px 18px; border: none; border-radius: 6px; font-size: 14px;
  cursor: pointer; transition: all 0.2s; display: inline-flex; align-items: center; gap: 4px;
}
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary { background: #4096ff; color: #fff; }
.btn-primary:hover:not(:disabled) { background: #1677ff; }
.btn-outline { background: #fff; color: #333; border: 1px solid #d9d9d9; }
.btn-outline:hover:not(:disabled) { border-color: #4096ff; color: #4096ff; }
.btn-sm { padding: 5px 12px; font-size: 12px; }
.btn-warning { background: #fa8c16; color: #fff; }
.btn-warning:hover:not(:disabled) { background: #d87a0d; }
.btn-success { background: #52c41a; color: #fff; }
.btn-success:hover:not(:disabled) { background: #49aa19; }

.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 1000;
  display: flex; align-items: center; justify-content: center;
}
.modal {
  background: #fff; border-radius: 12px; width: 560px; max-height: 85vh;
  overflow-y: auto; box-shadow: 0 8px 30px rgba(0,0,0,0.18);
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 24px; border-bottom: 1px solid #f0f0f0;
}
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; }
.modal-close {
  background: none; border: none; font-size: 22px; cursor: pointer;
  color: #999; line-height: 1;
}
.modal-close:hover { color: #333; }
.modal-body { padding: 20px 24px; }
.modal-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 14px 24px; border-top: 1px solid #f0f0f0;
}

.form-group { margin-bottom: 14px; }
.form-group label { display: block; margin-bottom: 4px; font-size: 13px; font-weight: 500; color: #333; }
.form-group .required { color: #ff4d4f; }
.form-input {
  width: 100%; padding: 8px 12px; border: 1px solid #d9d9d9; border-radius: 6px;
  font-size: 14px; outline: none; box-sizing: border-box; transition: border-color 0.2s;
}
.form-input:focus { border-color: #4096ff; }
.form-textarea { min-height: 80px; resize: vertical; }

/* Upload area */
.upload-area {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: border-color 0.2s;
  min-height: 100px;
}
.upload-area:hover { border-color: #4096ff; }
.upload-placeholder {
  display: flex; flex-direction: column; align-items: center;
  gap: 8px; color: #999; padding: 20px 0;
}
.upload-placeholder i { font-size: 28px; }
.upload-placeholder small { font-size: 12px; color: #bbb; }
.upload-previews {
  display: flex; flex-wrap: wrap; gap: 8px;
}
.upload-preview-item {
  width: 100px; height: 100px; border-radius: 6px;
  overflow: hidden; position: relative;
  border: 1px solid #e8e8e8;
}
.upload-preview-img {
  width: 100%; height: 100%; object-fit: cover;
}
.upload-preview-remove {
  position: absolute; top: 2px; right: 2px;
  width: 22px; height: 22px; border-radius: 50%;
  border: none; background: rgba(0,0,0,0.5); color: #fff;
  font-size: 14px; line-height: 1; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.upload-add-btn {
  display: flex; align-items: center; justify-content: center;
  background: #fafafa; border: 1px dashed #d9d9d9;
  font-size: 32px; color: #bbb; cursor: pointer;
}
.upload-add-btn:hover { border-color: #4096ff; color: #4096ff; }
.upload-status { margin-top: 8px; font-size: 13px; color: #4096ff; }
</style>
