<template>
  <button
    :disabled="disabled"
    @click="handleClick"
    :class="[
      'flex items-center gap-2 rounded-lg text-sm transition-all',
      disabled ? 'opacity-50 !cursor-not-allowed' : 'cursor-pointer',
      classNames,
    ]"
  >
    <!-- Label -->
    <span v-if="label">{{ label }}</span>
  </button>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps<{
  label?: string
  onClick?: () => void
  classNames?: string | string[]
  disabled?: boolean
}>()

const handleClick = () => {
  if (!props.disabled && props.onClick) props.onClick()
}
</script>
