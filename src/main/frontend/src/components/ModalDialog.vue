<template>
  <Teleport to="body">
    <Transition name="modal-scale">
      <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-card" :style="{ width: width }" role="dialog" aria-modal="true" :aria-label="title">
          <div class="modal-header">
            <h3 class="modal-title">{{ title }}</h3>
            <button class="modal-close" @click="$emit('close')" aria-label="Close">&times;</button>
          </div>
          <div class="modal-body">
            <slot />
          </div>
          <div v-if="$slots.footer" class="modal-footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  visible: {
    type: Boolean,
    default: false
  },
  width: {
    type: String,
    default: '560px'
  }
})

defineEmits(['close'])
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 8000;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.modal-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  max-width: calc(100vw - 48px);
  max-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0 24px;
}

.modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  line-height: 1;
  color: #9ca3af;
  cursor: pointer;
  padding: 0 2px;
  transition: color 0.2s;
}

.modal-close:hover {
  color: #374151;
}

.modal-body {
  padding: 20px 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 24px 20px 24px;
}

/* Scale-in / scale-out transition */
.modal-scale-enter-active {
  animation: modal-scale-in 0.3s ease-out;
}

.modal-scale-leave-active {
  animation: modal-scale-out 0.2s ease-in;
}

@keyframes modal-scale-in {
  from {
    opacity: 0;
    transform: scale(0.92);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes modal-scale-out {
  from {
    opacity: 1;
    transform: scale(1);
  }
  to {
    opacity: 0;
    transform: scale(0.92);
  }
}
</style>
