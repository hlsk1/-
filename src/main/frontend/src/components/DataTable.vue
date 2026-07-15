<template>
  <div class="datatable-wrapper">
    <!-- Loading overlay -->
    <div v-if="loading" class="datatable-loading">
      <div class="datatable-spinner" />
      <span class="datatable-loading-text">加载中...</span>
    </div>

    <!-- Table -->
    <table v-else class="datatable-table" :class="{ 'datatable-empty-table': !hasData }">
      <thead>
        <tr class="datatable-header-row">
          <th
            v-for="col in columns"
            :key="col.key"
            class="datatable-header-cell"
            :style="{ width: col.width }"
          >
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <template v-if="hasData">
          <tr
            v-for="(row, rowIndex) in data"
            :key="rowIndex"
            class="datatable-body-row"
          >
            <td
              v-for="col in columns"
              :key="col.key"
              class="datatable-body-cell"
              :style="{ width: col.width }"
            >
              <slot :name="col.key" :row="row" :value="row[col.key]" :index="rowIndex">
                {{ row[col.key] ?? '' }}
              </slot>
            </td>
          </tr>
        </template>
        <tr v-else>
          <td :colspan="columns.length" class="datatable-empty-cell">
            <div class="datatable-empty-state">
              <svg class="datatable-empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
              </svg>
              <span>{{ emptyText }}</span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  columns: {
    type: Array,
    required: true,
    validator: (val) => val.every((col) => col.key && col.label)
  },
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  }
})

const hasData = computed(() => props.data && props.data.length > 0)
</script>

<style scoped>
.datatable-wrapper {
  position: relative;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

/* ---- Loading ---- */
.datatable-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
  gap: 16px;
}

.datatable-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: datatable-spin 0.7s linear infinite;
}

@keyframes datatable-spin {
  to {
    transform: rotate(360deg);
  }
}

.datatable-loading-text {
  font-size: 14px;
  color: #9ca3af;
}

/* ---- Table ---- */
.datatable-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}

/* Header */
.datatable-header-row {
  background: #f9fafb;
  border-bottom: 2px solid #e5e7eb;
}

.datatable-header-cell {
  padding: 12px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  text-align: left;
  white-space: nowrap;
  user-select: none;
}

/* Body */
.datatable-body-row {
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.15s;
}

.datatable-body-row:last-child {
  border-bottom-color: #e5e7eb;
}

.datatable-body-row:hover {
  background-color: #f9fafb;
}

.datatable-body-cell {
  padding: 14px 16px;
  font-size: 14px;
  color: #374151;
  vertical-align: middle;
}

/* Empty */
.datatable-empty-table .datatable-header-row {
  border-bottom-color: #e5e7eb;
}

.datatable-empty-cell {
  padding: 0;
}

.datatable-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 56px 24px;
  gap: 12px;
  color: #9ca3af;
  font-size: 14px;
}

.datatable-empty-icon {
  width: 48px;
  height: 48px;
  opacity: 0.4;
}
</style>
