<template>
  <div class="admin-page">
    <div class="page-header">
      <h2 class="page-title">客户管理</h2>
    </div>

    <div class="toast" v-if="toast.show" :class="toast.type">{{ toast.msg }}</div>

    <div class="toolbar">
      <div class="toolbar-left">
        <button class="btn btn-primary" @click="openAddModal">+ 新增客户</button>
      </div>
      <div class="toolbar-right">
        <input
          v-model="searchKey"
          type="text"
          class="search-input"
          placeholder="搜索姓名/手机号..."
          @input="onSearch"
        />
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <table v-else class="data-table">
      <thead>
        <tr>
          <th>姓名</th>
          <th>手机号</th>
          <th>身份证</th>
          <th>驾照</th>
          <th>积分</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in filteredCustomers" :key="c.id">
          <td>{{ c.name }}</td>
          <td>{{ c.phone }}</td>
          <td>{{ c.idCard || '-' }}</td>
          <td>{{ c.license || '-' }}</td>
          <td>{{ c.points ?? 0 }}</td>
          <td>
            <span class="badge" :class="c.status === 'BLACKLIST' ? 'badge-red' : 'badge-green'">
              {{ c.status === 'BLACKLIST' ? '黑名单' : '正常' }}
            </span>
          </td>
          <td class="action-cell">
            <button class="btn btn-sm btn-outline" @click="openEditModal(c)">编辑</button>
            <button class="btn btn-sm btn-primary" @click="handleAddPoints(c)">积分</button>
            <button
              v-if="c.status !== 'BLACKLIST'"
              class="btn btn-sm btn-warning"
              @click="handleBlacklist(c)"
            >拉黑</button>
            <button
              v-if="c.status === 'BLACKLIST'"
              class="btn btn-sm btn-success"
              @click="handleUnblacklist(c)"
            >移出</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal -->
    <div class="modal-overlay" v-if="modalVisible" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑客户' : '新增客户' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>姓名 <span class="required">*</span></label>
            <input v-model="form.name" class="form-input" placeholder="姓名" />
          </div>
          <div class="form-group">
            <label>手机号 <span class="required">*</span></label>
            <input v-model="form.phone" class="form-input" placeholder="手机号" />
          </div>
          <div class="form-group">
            <label>身份证</label>
            <input v-model="form.idCard" class="form-input" placeholder="身份证号" />
          </div>
          <div class="form-group">
            <label>驾照</label>
            <input v-model="form.license" class="form-input" placeholder="驾照号" />
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="form.remark" class="form-input form-textarea" placeholder="备注信息"></textarea>
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
import { customerApi } from '@/api'

function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

const customers = ref([])
const searchKey = ref('')
const loading = ref(false)
const modalVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const toast = reactive({ show: false, msg: '', type: 'success' })

const filteredCustomers = computed(() => {
  if (!searchKey.value) return customers.value
  const kw = searchKey.value.toLowerCase()
  return customers.value.filter(c =>
    (c.name || '').toLowerCase().includes(kw) ||
    (c.phone || '').toLowerCase().includes(kw)
  )
})

const form = reactive({
  name: '',
  phone: '',
  idCard: '',
  license: '',
  remark: ''
})

function showToast(msg, type = 'success') {
  toast.show = true
  toast.msg = msg
  toast.type = type
  setTimeout(() => { toast.show = false }, 2500)
}

function resetForm() {
  form.name = ''
  form.phone = ''
  form.idCard = ''
  form.license = ''
  form.remark = ''
}

async function loadCustomers() {
  loading.value = true
  try {
    const res = await customerApi.list()
    customers.value = res.data || []
  } catch (e) {
    showToast('加载客户列表失败', 'error')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  // computed handles filtering
}

function openAddModal() {
  isEdit.value = false
  editId.value = null
  resetForm()
  modalVisible.value = true
}

function openEditModal(c) {
  isEdit.value = true
  editId.value = c.id
  form.name = c.name || ''
  form.phone = c.phone || ''
  form.idCard = c.idCard || ''
  form.license = c.license || ''
  form.remark = c.remark || ''
  modalVisible.value = true
}

function closeModal() {
  modalVisible.value = false
  resetForm()
}

async function handleSubmit() {
  if (!form.name || !form.phone) {
    showToast('请填写姓名和手机号', 'error')
    return
  }
  submitting.value = true
  try {
    let res
    if (isEdit.value) {
      res = await customerApi.edit(editId.value, form)
    } else {
      res = await customerApi.add(form)
    }
    if (res.code === 200) {
      showToast(isEdit.value ? '编辑成功' : '新增成功')
      closeModal()
      await loadCustomers()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  } finally {
    submitting.value = false
  }
}

async function handleAddPoints(c) {
  const points = prompt(`请输入为 ${c.name} 增加的积分数：`)
  if (!points) return
  const num = parseInt(points)
  if (isNaN(num)) {
    showToast('请输入有效数字', 'error')
    return
  }
  try {
    const res = await customerApi.addPoints(c.id, num)
    if (res.code === 200) {
      showToast('积分更新成功')
      await loadCustomers()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

async function handleBlacklist(c) {
  const reason = prompt(`请输入将 ${c.name} 加入黑名单的原因：`)
  if (!reason) return
  try {
    const res = await customerApi.blacklist(c.id, reason)
    if (res.code === 200) {
      showToast('已加入黑名单')
      await loadCustomers()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

async function handleUnblacklist(c) {
  try {
    const res = await customerApi.unblacklist(c.id)
    if (res.code === 200) {
      showToast('已移出黑名单')
      await loadCustomers()
    } else {
      showToast(res.msg || '操作失败', 'error')
    }
  } catch (e) {
    showToast('操作失败', 'error')
  }
}

onMounted(() => {
  loadCustomers()
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
.badge-red { background: #ff4d4f; }

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
  background: #fff; border-radius: 12px; width: 520px; max-height: 85vh;
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
</style>
