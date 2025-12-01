<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import type { Holding } from '../composables/UsePortfolioSocket'
import { usePortfolioSocket } from '../composables/UsePortfolioSocket'
import portfolioModal from '@/components/modals/portfolio-modal.vue'

const auth = useAuthStore()
let socket: ReturnType<typeof usePortfolioSocket> | null = null
const portfolio = ref<Holding[]>([])

// Connect WebSocket once user and token are available
watch(
  () => auth.user,
  (user) => {
    if (user && auth.token) {
      socket = usePortfolioSocket(auth.token)
      portfolio.value = socket.portfolio.value

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

// Map symbol to full coin name
const getCoinName = (symbol: string) => {
  const map: Record<string, string> = {
    BTC: 'Bitcoin',
    ETH: 'Ethereum',
    SHIB: 'Shiba Inu',
    DOGE: 'Dogecoin',
  }
  return map[symbol.toUpperCase()] || symbol
}

// Get coin image from assets
const getCoinImage = (symbol: string) =>
  new URL(`/src/assets/${symbol.toLowerCase()}.png`, import.meta.url).href
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between mb-4 items-center">
      <h1 class="text-2xl font-bold">My Portfolio</h1>
      <div class="flex items-center gap-4">
        <span class="text-2xl font-bold"> ${{ totalValue.toLocaleString() }} </span>
        <portfolio-modal />
      </div>
    </div>

    <table class="w-full border bg-[#1B2028] rounded-xl overflow-hidden">
      <thead>
        <tr class="bg-[#2A2F3A]">
          <th class="p-4 text-gray-400 font-thin text-left rounded-tl-xl">Coin</th>
          <th class="p-4 text-gray-400 font-thin text-right">Amount</th>
          <th class="p-4 text-gray-400 font-thin text-right">Price (USD)</th>
          <th class="p-4 text-gray-400 font-thin text-right rounded-tr-xl">Total Value (USD)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="holding in portfolio" :key="holding.id" class="hover:bg-gray-700">
          <td class="p-4 flex items-center gap-3">
            <img
              :src="getCoinImage(holding.symbol)"
              :alt="holding.symbol"
              class="w-8 h-8 rounded-full"
            />
            <div class="flex flex-col">
              <span class="font-bold text-white">{{ getCoinName(holding.symbol) }}</span>
              <span class="text-gray-400 text-sm">{{ holding.symbol }}</span>
            </div>
          </td>
          <td class="p-4 text-right text-white">{{ holding.amount }}</td>
          <td class="p-4 text-right text-white">${{ holding.currentPrice.toLocaleString() }}</td>
          <td class="p-4 text-right text-white">${{ holding.totalValue.toLocaleString() }}</td>
        </tr>
        <tr v-if="portfolio.length === 0">
          <td colspan="4" class="text-center p-4 text-gray-500">No holdings yet</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
