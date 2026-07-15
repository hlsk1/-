<template>
  <Teleport to="body">
    <Transition name="toast-slide">
      <div v-if="visible" :class="['toast', `toast--${type}`]" role="alert">
        <span class="toast__icon">
          <template v-if="type === 'success'">&#10003;</template>
          <template v-else-if="type === 'error'">&#10007;</template>
          <template v-else-if="type === 'warning'">&#9888;</template>
        </span>
        <span class="toast__message">{{ message }}</span>
        <button class="toast__close" @click="$emit('close')" aria-label="Close">&times;</button>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'success',
    validator: (value) => ['success', 'error', 'warning'].includes(value)
  },
  visible: {
    type: Boolean,
    default: false
  }
})

defineEmits(['close'])
</script>

<style scoped>
.toast {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 280px;
  max-width: 420px;
  padding: 14px 18px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  pointer-events: auto;
}

.toast--success {
  background-color: #10b981;
}

.toast--error {
  background-color: #ef4444;
}

.toast--warning {
  background-color: #f59e0b;
  color: #1f2937;
}

.toast__icon {
  flex-shrink: 0;
  font-size: 18px;
  line-height: 1;
}

.toast__message {
  flex: 1;
  word-break: break-word;
}

.toast__close {
  flex-shrink: 0;
  background: none;
  border: none;
  color: inherit;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  opacity: 0.7;
  padding: 0 2px;
  transition: opacity 0.2s;
}

.toast__close:hover {
  opacity: 1;
}

/* Slide-in / slide-out transition */
.toast-slide-enter-active {
  animation: toast-slide-in 0.35s ease-out;
}

.toast-slide-leave-active {
  animation: toast-slide-out 0.25s ease-in;
}

@keyframes toast-slide-in {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes toast-slide-out {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(100%);
  }
}
</style>
