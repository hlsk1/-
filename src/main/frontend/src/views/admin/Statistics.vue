<template>
  <div class="statistics-page">
    <!-- Stat Cards -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: #eff6ff; color: #2563eb;">📋</div>
        <div class="stat-info">
          <div class="stat-val">{{ overview.totalOrders ?? '-' }}</div>
          <div class="stat-lbl">总订单量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #fffbeb; color: #d97706;">💰</div>
        <div class="stat-info">
          <div class="stat-val">{{ fmtMoney(overview.totalRevenue) }}</div>
          <div class="stat-lbl">总收入</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #f0fdf4; color: #16a34a;">📈</div>
        <div class="stat-info">
          <div class="stat-val">{{ fmtMoney(estimatedProfit) }}</div>
          <div class="stat-lbl">预估利润</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #f5f3ff; color: #7c3aed;">🚗</div>
        <div class="stat-info">
          <div class="stat-val">{{ overview.availableVehicles ?? '-' }}</div>
          <div class="stat-lbl">可用车辆</div>
        </div>
      </div>
    </div>

    <!-- Charts -->
    <div class="charts-grid">
      <div class="chart-box">
        <h4>📈 订单收入趋势</h4>
        <div ref="lineChartRef" class="chart-canvas"></div>
      </div>
      <div class="chart-box">
        <h4>🚗 车辆出租率</h4>
        <div ref="gaugeChartRef" class="chart-canvas"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { statisticsApi, orderApi, vehicleApi } from '@/api'
import * as echarts from 'echarts'

const overview = ref({})
const lineChartRef = ref(null)
const gaugeChartRef = ref(null)
let lineChart = null
let gaugeChart = null

const estimatedProfit = computed(() => {
  const rev = Number(overview.value.totalRevenue) || 0
  return rev * 0.3
})

function fmtMoney(val) {
  if (val == null || val === '') return '-'
  const n = Number(val)
  if (isNaN(n)) return '-'
  return '¥' + n.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
}

async function loadOverview() {
  const res = await statisticsApi.overview()
  if (res && res.code === 200 && res.data) {
    overview.value = res.data
  }
}

async function loadOrderTrend() {
  const now = new Date()
  const fifteenDaysAgo = new Date(now)
  fifteenDaysAgo.setDate(fifteenDaysAgo.getDate() - 14)

  const params = {
    page: 1,
    pageSize: 9999
  }

  const res = await orderApi.list(params)
  if (!res || res.code !== 200 || !res.data) return

  const orders = res.data.records || res.data.list || res.data || []
  const cutoff = fifteenDaysAgo.getTime()

  const dailyMap = {}
  for (let i = 0; i < 14; i++) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    const key = d.toISOString().substring(0, 10)
    dailyMap[key] = 0
  }

  orders.forEach(order => {
    const createTime = order.createTime || order.createdAt || order.orderTime
    if (!createTime) return
    const t = new Date(createTime).getTime()
    if (t < cutoff) return
    const key = createTime.substring(0, 10)
    if (dailyMap.hasOwnProperty(key)) {
      dailyMap[key] += Number(order.totalAmount || order.totalPrice || order.amount || 0)
    }
  })

  const sortedKeys = Object.keys(dailyMap).sort()
  const dates = sortedKeys.map(k => k.substring(5))
  const values = sortedKeys.map(k => dailyMap[k])

  if (!lineChartRef.value) return
  if (!lineChart) {
    lineChart = echarts.init(lineChartRef.value)
  }

  lineChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: params => {
        const p = params[0]
        return `${p.axisValue}<br/>收入: ¥${p.value.toLocaleString('zh-CN', { minimumFractionDigits: 2 })}`
      }
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisTick: { show: false },
      axisLabel: { fontSize: 11, color: '#64748b' }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        fontSize: 11,
        color: '#64748b',
        formatter: v => '¥' + (v / 1000).toFixed(0) + 'k'
      },
      splitLine: { lineStyle: { color: '#f1f5f9' } }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#2563eb', width: 2.5 },
      itemStyle: { color: '#2563eb' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(37, 99, 235, 0.2)' },
          { offset: 1, color: 'rgba(37, 99, 235, 0.01)' }
        ])
      }
    }]
  })
}

async function loadGauge() {
  const res = await vehicleApi.list({ page: 1, pageSize: 9999 })
  if (!res || res.code !== 200 || !res.data) return

  const vehicles = res.data.records || res.data.list || res.data || []
  const total = vehicles.length
  if (total === 0) {
    if (gaugeChartRef.value && gaugeChart) {
      gaugeChart.setOption({
        series: [{
          type: 'gauge',
          detail: { formatter: '0%' },
          data: [{ value: 0 }]
        }]
      })
    }
    return
  }

  const rented = vehicles.filter(v => v.status === 'RENTED' || v.status === '已租').length
  const rate = Math.round((rented / total) * 100)

  if (!gaugeChartRef.value) return
  if (!gaugeChart) {
    gaugeChart = echarts.init(gaugeChartRef.value)
  }

  gaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 210,
      endAngle: -30,
      center: ['50%', '55%'],
      radius: '90%',
      min: 0,
      max: 100,
      axisLine: {
        show: true,
        lineStyle: {
          width: 18,
          color: [
            [0.5, '#f59e0b'],
            [0.8, '#2563eb'],
            [1, '#16a34a']
          ]
        }
      },
      pointer: {
        itemStyle: { color: '#0f172a' }
      },
      axisTick: {
        distance: -18,
        length: 6,
        lineStyle: { width: 1, color: '#cbd5e1' }
      },
      splitLine: {
        distance: -22,
        length: 14,
        lineStyle: { width: 2, color: '#cbd5e1' }
      },
      axisLabel: {
        distance: 25,
        fontSize: 10,
        color: '#64748b'
      },
      detail: {
        valueAnimation: true,
        formatter: '{value}%',
        fontSize: 28,
        fontWeight: 'bold',
        color: '#0f172a',
        offsetCenter: [0, '68%']
      },
      data: [{ value: rate }]
    }]
  })
}

function handleResize() {
  lineChart?.resize()
  gaugeChart?.resize()
}

onMounted(async () => {
  await loadOverview()
  await Promise.all([loadOrderTrend(), loadGauge()])
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  gaugeChart?.dispose()
  lineChart = null
  gaugeChart = null
})
</script>

<style scoped>
.statistics-page {
  padding: 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.chart-canvas {
  width: 100%;
  height: 340px;
}

@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
