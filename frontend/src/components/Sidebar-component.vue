<script setup lang="ts">
import { useRoute } from 'vue-router'

import Button from '../components/Button-component.vue'

const route = useRoute()

const links = [
  // { to: "/dashboard", label: "Home", icon: Logo },

  { to: '/dashboard', label: 'Overview' },
  { to: '/dashboard/history', label: 'History' },
]

const isActive = (linkTo: string) => {
  switch (linkTo) {
    // case "/dashboard":
    // return route.name === "DashboardHome";
    case '/dashboard':
      return route.name === 'overview'
    case '/dashboard/history':
      return route.name === 'history'
    default:
      return false
  }
}
</script>

<template>
  <aside class="flex flex-col px-4 h-full text-white">
    <!-- Header -->
    <header class="flex stroke-white items-center gap-2 py-4 h-16">
      <Logo />
      <h1 class="text-2xl font-semibold">CryptApp</h1>
    </header>

    <!-- Navigation -->
    <nav class="flex flex-col gap-2 mt-4">
      <RouterLink v-for="link in links" :key="link.to" :to="link.to" custom v-slot="{ navigate }">
        <Button
          :label="link.label"
          :class-names="[
            'w-full text-left px-3 py-2 rounded-md flex items-center gap-2 transition',
            isActive(link.to)
              ? 'stroke-[#1C274C] bg-[#3A6FF8] text-white font-semibold'
              : 'hover:bg-white text-[#9E9E9E] hover:text-[#1C274C]',
          ]"
          @click="navigate"
        ></Button>
      </RouterLink>
    </nav>
  </aside>
</template>
