<template>
  <router-link :to="`/vehicle/${vehicle.id}`" class="vehicle-card">
    <div class="card-image">
      <img
        v-if="vehicleImage"
        :src="vehicleImage"
        :alt="vehicle.model"
        class="vehicle-img"
      />
      <div v-else class="image-fallback">
        <span class="car-emoji">🚗</span>
      </div>
      <span v-if="vehicle.status !== 'AVAILABLE'" class="status-badge">
        {{ vehicle.status }}
      </span>
    </div>
    <div class="card-body">
      <h3 class="vehicle-title">
        {{ vehicle.brand }} {{ vehicle.model }}
      </h3>
      <div class="vehicle-meta">
        <span class="meta-item">{{ vehicle.color }}</span>
        <span class="meta-divider">|</span>
        <span class="meta-item">{{ vehicle.seatCount }}座</span>
        <span class="meta-divider">|</span>
        <span class="meta-item">{{ vehicle.plateNumber }}</span>
      </div>
      <div class="card-footer">
        <span class="daily-price">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ vehicle.dailyPrice }}</span>
          <span class="price-unit">/天</span>
        </span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  vehicle: {
    type: Object,
    required: true,
  },
})

const UPLOAD_FILES = [
  '20260714_0ea2c5b3.jpg','20260714_292125e4.jpg','20260714_3c12e78e.jpg',
  '20260714_4f342939.jpg','20260714_58b3de9c.jpg','20260714_5a18ba75.jpg',
  '20260714_6ba438a6.jpg','20260714_826103fa.jpg','20260714_8f852483.jpg',
  '20260714_94293e3d.jpg','20260714_9bfc047b.jpg','20260714_b8a0dfb1.jpg',
  '20260714_c2b7012e.jpg','20260714_d00dd179.jpg','20260714_ec01e985.jpg',
  '20260714_fb841242.jpg'
]

const vehicleImage = computed(() => {
  // 统一使用本地 uploads/vehicles 文件夹下的真实车辆图片
  const idx = (props.vehicle.id || 1) % UPLOAD_FILES.length
  return `/uploads/vehicles/${UPLOAD_FILES[idx]}`
})
</script>

<style scoped>
.vehicle-card {
  display: block;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-decoration: none;
  color: inherit;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  cursor: pointer;
}

.vehicle-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.14);
}

.card-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.vehicle-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.image-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e8e8e8;
}

.car-emoji {
  font-size: 56px;
  line-height: 1;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 10px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.card-body {
  padding: 14px 16px 16px;
}

.vehicle-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #222;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.vehicle-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-item {
  white-space: nowrap;
}

.meta-divider {
  color: #ddd;
}

.card-footer {
  display: flex;
  align-items: baseline;
}

.daily-price {
  display: flex;
  align-items: baseline;
  gap: 1px;
}

.price-symbol {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
  line-height: 1;
}

.price-unit {
  font-size: 12px;
  color: #999;
  margin-left: 2px;
}
</style>
