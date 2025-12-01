<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Button from './Button-component.vue'

interface HeaderProps {
  username: string
  onLogout: () => void
}

const props = defineProps<HeaderProps>()

const open = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

function toggle() {
  open.value = !open.value
}

// Close dropdown when clicking outside
function handleClickOutside(e: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    open.value = false
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})
</script>

<template>
  <header class="flex items-center justify-between h-16 px-6 md:px-8 shadow-sm">
    <header class="">
      <div class="flex stroke-white items-center gap-2 lg:hidden"></div>
      <div>
        <h1 class="text-2xl font-semibold">Dashboard</h1>
      </div>
    </header>

    <div class="relative" ref="dropdownRef">
      <Button
        :label="props.username"
        @click="toggle"
        classNames="flex items-center gap-2 px-3 py-1.5 text-white rounded-md hover:bg-white hover:text-[#1C274C] focus:ring-2 focus:ring-[#1C274C]/40 transition-all font-medium"
      ></Button>

      <div
        v-if="open"
        class="absolute right-0 mt-2 bg-white border border-gray-200 rounded-lg shadow-md z-50 min-w-[120px] overflow-hidden"
      >
        <Button
          label="Logout"
          @click="((open = false), props.onLogout())"
          classNames="flex w-full items-center gap-2 px-4 py-2 text-[#1C274C] hover:bg-[#f0f2f8] focus:bg-[#e3e7f3] transition-colors stroke-[#1C274C]"
        ></Button>
      </div>
    </div>
  </header>
</template>
