<template>
  <div class="dashboard">
    <!-- ===== Stat Cards ===== -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon stat-icon--blue">
          <span>📋</span>
        </div>
        <div class="stat-info">
          <div class="stat-val">{{ overview.totalOrders ?? '--' }}</div>
          <div class="stat-lbl">总订单</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--amber">
          <span>💰</span>
        </div>
        <div class="stat-info">
          <div class="stat-val">{{ formatRevenue }}</div>
          <div class="stat-lbl">总收入</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--green">
          <span>🚗</span>
        </div>
        <div class="stat-info">
          <div class="stat-val">{{ overview.activeVehicles ?? '--' }}</div>
          <div class="stat-lbl">可用车辆</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon stat-icon--purple">
          <span>👥</span>
        </div>
        <div class="stat-info">
          <div class="stat-val">{{ overview.totalCustomers ?? '--' }}</div>
          <div class="stat-lbl">客户总数</div>
        </div>
      </div>
    </div>

    <!-- ===== Charts Row ===== -->
    <div class="charts-row">
      <div class="chart-box">
        <h4>车辆状态分布</h4>
        <div ref="statusChartRef" class="chart-dom"></div>
      </div>

      <div class="chart-box">
        <h4>品牌车辆分布（Top 10）</h4>
        <div ref="brandChartRef" class="chart-dom"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi, vehicleApi } from '@/api'

// ---- Overview data ----
const overview = ref({})

const formatRevenue = computed(() => {
  const val = overview.value.totalRevenue
  if (val == null) return '--'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
})

// ---- Chart refs ----
const statusChartRef = ref(null)
const brandChartRef = ref(null)

let statusChart = null
let brandChart = null

// ---- Status label & color maps ----
const STATUS_MAP = {
  AVAILABLE: { label: '可租', color: '#16a34a' },
  RENTED: { label: '已租', color: '#3b82f6' },
  REPAIR: { label: '维修中', color: '#f59e0b' },
  OFFLINE: { label: '已下架', color: '#94a3b8' }
}

// ---- Fetch overview ----
async function fetchOverview() {
  const res = await statisticsApi.overview()
  if (res.code === 200 && res.data) {
    overview.value = res.data
  }
}

// ---- Fetch vehicles & build charts ----
async function fetchVehiclesAndCharts() {
  const res = await vehicleApi.list()
  const vehicles = Array.isArray(res.data) ? res.data : (res.data?.records || [])

  if (vehicles.length === 0) return

  buildStatusChart(vehicles)
  buildBrandChart(vehicles)
}

// ---- Status Pie Chart ----
function buildStatusChart(vehicles) {
  if (!statusChartRef.value) return

  const counts = { AVAILABLE: 0, RENTED: 0, REPAIR: 0, OFFLINE: 0 }
  vehicles.forEach(v => {
    const s = (v.status || '').toUpperCase()
    if (counts.hasOwnProperty(s)) counts[s]++
  })

  const data = Object.entries(counts)
    .filter(([, c]) => c > 0)
    .map(([key, count]) => ({
      name: (STATUS_MAP[key] || {}).label || key,
      value: count,
      itemStyle: { color: (STATUS_MAP[key] || {}).color || '#94a3b8' }
    }))

  if (!statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }

  statusChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 辆 ({d}%)'
    },
    legend: {
      bottom: 0,
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { fontSize: 12, color: '#64748b' }
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['50%', '48%'],
        avoidLabelOverlap: false,
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        },
        data
      }
    ]
  })
}

// ---- Brand Bar Chart ----
function buildBrandChart(vehicles) {
  if (!brandChartRef.value) return

  const brandCounts = {}
  vehicles.forEach(v => {
    const brand = v.brand || v.brandName || '未知'
    brandCounts[brand] = (brandCounts[brand] || 0) + 1
  })

  const sorted = Object.entries(brandCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)

  const brands = sorted.map(([name]) => name)
  const counts = sorted.map(([, c]) => c)

  if (!brandChart) {
    brandChart = echarts.init(brandChartRef.value)
  }

  brandChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c} 辆'
    },
    grid: {
      left: '3%',
      right: '8%',
      bottom: '3%',
      top: '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: brands,
      axisLabel: {
        fontSize: 11,
        color: '#64748b',
        rotate: brands.length > 5 ? 30 : 0
      },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '辆',
      nameTextStyle: { fontSize: 11, color: '#94a3b8' },
      axisLabel: { fontSize: 11, color: '#64748b' },
      splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } },
      minInterval: 1
    },
    series: [
      {
        type: 'bar',
        barWidth: '50%',
        data: counts,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3b82f6' },
            { offset: 1, color: '#93c5fd' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2563eb' },
              { offset: 1, color: '#60a5fa' }
            ])
          }
        }
      }
    ]
  })
}

// ---- Handle resize ----
function handleResize() {
  statusChart?.resize()
  brandChart?.resize()
}

// ---- Lifecycle ----
onMounted(async () => {
  await fetchOverview()
  await nextTick()
  await fetchVehiclesAndCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
  brandChart?.dispose()
  statusChart = null
  brandChart = null
})
</script>

<style scoped>
/* ===== Dashboard ===== */
.dashboard {
  --icon-blue-bg: #eff6ff;
  --icon-amber-bg: #fffbeb;
  --icon-green-bg: #f0fdf4;
  --icon-purple-bg: #f5f3ff;
}

/* ===== Stat Cards ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: var(--card-bg);
  border-radius: var(--radius);
  padding: 1.125rem 1.25rem;
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 0.875rem;
  transition: box-shadow 0.15s;
}

.stat-card:hover {
  box-shadow: var(--shadow-sm);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.125rem;
  flex-shrink: 0;
}

.stat-icon--blue  { background: var(--icon-blue-bg); }
.stat-icon--amber { background: var(--icon-amber-bg); }
.stat-icon--green { background: var(--icon-green-bg); }
.stat-icon--purple { background: var(--icon-purple-bg); }

.stat-info {
  min-width: 0;
}

.stat-val {
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1.2;
  color: var(--text);
}

.stat-lbl {
  font-size: 0.75rem;
  color: var(--text-muted);
}

/* ===== Charts ===== */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.chart-box {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 1rem;
}

.chart-box h4 {
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
  color: var(--text);
  font-weight: 600;
}

.chart-dom {
  width: 100%;
  height: 320px;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .stat-grid {
    grid-template-columns: 1fr 1fr;
  }

  .charts-row {
    grid-template-columns: 1fr;
  }

  .chart-dom {
    height: 280px;
  }
}
</style>
