<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import type { Holding } from '../composables/UsePortfolioSocket'
import { usePortfolioSocket } from '../composables/UsePortfolioSocket'
import portfolioModal from '@/components/modals/portfolio-modal.vue'

const auth = useAuthStore()
let socket: ReturnType<typeof usePortfolioSocket> | null = null

// Bind portfolio ref reactively to the socket
const portfolio = ref<Holding[]>([])

// Connect WebSocket once user and token are available
watch(
  () => auth.user,
  (user) => {
    if (user && auth.token) {
      socket = usePortfolioSocket(auth.token)
      // Bind directly to socket's reactive portfolio ref
      portfolio.value = socket.portfolio.value

      // Optional: sync updates reactively
      watch(
        () => socket?.portfolio.value,
        (newVal) => {
          if (newVal) portfolio.value = newVal
        },
        { immediate: true },
      )
    }
  },
  { immediate: true },
)

// Compute total portfolio value
const totalValue = computed(() =>
  portfolio.value.reduce((sum, holding) => sum + holding.totalValue, 0),
)
</script>

<template>
  <div class="p-4">
    <div class="flex justify-between">
      <h1 class="text-2xl font-bold mb-4">My Portfolio</h1>
      <span class="text-2xl font-bold"> ${{ totalValue.toLocaleString() }} </span>
    </div>
    <portfolio-modal />
    <table class="w-full border-collapse border">
      <thead>
        <tr class="bg-[#1B2028]">
          <th class="border border-gray-300 p-2 text-left">Symbol</th>
          <th class="border border-gray-300 p-2 text-right">Amount</th>
          <th class="border border-gray-300 p-2 text-right">Price (USD)</th>
          <th class="border border-gray-300 p-2 text-right">Total Value (USD)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="holding in portfolio" :key="holding.id" class="hover:bg-gray-50">
          <td class="border border-gray-300 p-2">{{ holding.symbol }}</td>
          <td class="border border-gray-300 p-2 text-right">{{ holding.amount }}</td>
          <td class="border border-gray-300 p-2 text-right">
            ${{ holding.currentPrice.toLocaleString() }}
          </td>
          <td class="border border-gray-300 p-2 text-right">
            ${{ holding.totalValue.toLocaleString() }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
