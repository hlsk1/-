<template>
  <div class="users-page">
    <!-- Toolbar -->
    <div class="toolbar">
      <button class="btn btn-primary" @click="openAdd">
        <i class="fas fa-plus"></i> 新增账号
      </button>
      <input
        v-model="searchKey"
        type="text"
        placeholder="搜索用户名 / 姓名 / 手机号..."
        @input="onSearch"
      />
    </div>

    <!-- Table -->
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>用户名</th>
            <th>姓名</th>
            <th>手机号</th>
            <th>角色</th>
            <th>状态</th>
            <th>最后登录</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="empty">加载中...</td>
          </tr>
          <tr v-else-if="filteredList.length === 0">
            <td colspan="7" class="empty">暂无数据</td>
          </tr>
          <tr v-for="user in filteredList" :key="user.id">
            <td>{{ user.username }}</td>
            <td>{{ user.realName || '-' }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>
              <span :class="['badge', roleBadgeClass(user.role)]">{{ user.role }}</span>
            </td>
            <td>
              <span :class="['badge', statusBadgeClass(user.status)]">
                {{ user.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ fmtTime(user.lastLoginTime) }}</td>
            <td class="actions-cell">
              <button class="btn btn-xs btn-outline" @click="openEdit(user)">编辑</button>
              <button class="btn btn-xs btn-warning" @click="handleResetPwd(user)">重置密码</button>
              <button class="btn btn-xs btn-ghost" @click="handleViewLogs(user)">日志</button>
              <button class="btn btn-xs btn-danger" @click="handleDelete(user)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add / Edit Modal -->
    <div v-if="modalVisible" class="modal-mask" @click.self="closeModal">
      <div class="modal-box">
        <h3>{{ isEdit ? '编辑账号' : '新增账号' }}</h3>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label">用户名 <span style="color: var(--danger);">*</span></label>
            <input v-model="form.username" type="text" class="form-input" required />
          </div>
          <div class="form-group">
            <label class="form-label">
              密码
              <span v-if="!isEdit" style="color: var(--danger);">*</span>
              <span v-else style="color: var(--text-muted); font-weight: 400;">（留空不修改）</span>
            </label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              :placeholder="isEdit ? '留空不修改' : ''"
              :required="!isEdit"
            />
          </div>
          <div class="form-group">
            <label class="form-label">姓名</label>
            <input v-model="form.realName" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input v-model="form.phone" type="text" class="form-input" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">角色</label>
              <select v-model="form.role" class="form-select">
                <option value="SUPER">SUPER</option>
                <option value="ADMIN">ADMIN</option>
                <option value="NORMAL">NORMAL</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">状态</label>
              <select v-model="form.status" class="form-select">
                <option :value="1">启用</option>
                <option :value="0">禁用</option>
              </select>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-outline" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { userApi } from '@/api'

const users = ref([])
const loading = ref(false)
const searchKey = ref('')
const modalVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)

const form = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  role: 'NORMAL',
  status: 1
})

const filteredList = computed(() => {
  if (!searchKey.value.trim()) return users.value
  const kw = searchKey.value.trim().toLowerCase()
  return users.value.filter(u =>
    (u.username || '').toLowerCase().includes(kw) ||
    (u.realName || '').toLowerCase().includes(kw) ||
    (u.phone || '').toLowerCase().includes(kw)
  )
})

function roleBadgeClass(role) {
  const map = {
    SUPER: 'badge-red',
    ADMIN: 'badge-blue',
    NORMAL: 'badge-gray',
    CUSTOMER: 'badge-gray'
  }
  return map[role] || 'badge-gray'
}

function statusBadgeClass(status) {
  return status === 1 ? 'badge-green' : 'badge-gray'
}

function fmtTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

function resetForm() {
  form.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    role: 'NORMAL',
    status: 1
  }
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await userApi.list({ page: 1, pageSize: 9999 })
    if (res && res.code === 200 && res.data) {
      users.value = res.data.records || res.data.list || res.data || []
    }
  } finally {
    loading.value = false
  }
}

function onSearch() {
  // filteredList is computed, no explicit action needed
}

function openAdd() {
  isEdit.value = false
  editId.value = null
  resetForm()
  modalVisible.value = true
}

function openEdit(user) {
  isEdit.value = true
  editId.value = user.id
  form.value = {
    username: user.username || '',
    password: '',
    realName: user.realName || '',
    phone: user.phone || '',
    role: user.role || 'NORMAL',
    status: user.status ?? 1
  }
  modalVisible.value = true
}

function closeModal() {
  modalVisible.value = false
  editId.value = null
  resetForm()
}

async function handleSubmit() {
  if (!form.value.username.trim()) return
  if (!isEdit.value && !form.value.password) return

  submitting.value = true
  try {
    let res
    if (isEdit.value) {
      const payload = { ...form.value }
      if (!payload.password) delete payload.password
      res = await userApi.edit(editId.value, payload)
    } else {
      res = await userApi.add(form.value)
    }
    if (res && res.code === 200) {
      closeModal()
      await fetchUsers()
      alert(isEdit.value ? '编辑成功' : '新增成功')
    } else {
      alert((res && res.msg) || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

async function handleResetPwd(user) {
  if (!confirm(`确定重置用户「${user.username}」的密码吗？`)) return
  const res = await userApi.resetPassword(user.id)
  if (res && res.code === 200) {
    const tempPwd = (res.data && res.data.password) || res.data || '请查看返回数据'
    alert(`密码已重置，临时密码：${tempPwd}`)
  } else {
    alert((res && res.msg) || '操作失败')
  }
}

async function handleViewLogs(user) {
  const res = await userApi.logs(user.id)
  if (res && res.code === 200 && res.data) {
    const log = res.data
    const time = fmtTime(log.lastLoginTime || log.loginTime)
    const ip = log.lastLoginIp || log.loginIp || '-'
    alert(`最后登录时间：${time}\n最后登录IP：${ip}`)
  } else {
    alert((res && res.msg) || '获取日志失败')
  }
}

async function handleDelete(user) {
  if (!confirm(`确定删除用户「${user.username}」吗？此操作不可撤销。`)) return
  const res = await userApi.delete(user.id)
  if (res && res.code === 200) {
    await fetchUsers()
    alert('删除成功')
  } else {
    alert((res && res.msg) || '删除失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-page {
  padding: 0;
}

.actions-cell {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.empty {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}
</style>
