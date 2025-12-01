/* eslint-disable @typescript-eslint/no-explicit-any */

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../lib/api'

const auth = useAuthStore()
const showModal = ref(false)
const selectedSymbol = ref('BTC')
const amount = ref(0)
const symbols = ['BTC', 'ETH', 'SHIB', 'DOGE']

const increment = () => (amount.value = Math.round((amount.value + 0.1) * 10) / 10)
const decrement = () => (amount.value = Math.max(0, Math.round((amount.value - 0.1) * 10) / 10))

// Transaction type
interface Transaction {
  id: number
  symbol: string
  amount: number
  type: string
  priceUsd: number
  createdAt: string
}

const transactions = ref<Transaction[]>([])

const fetchTransactions = async () => {
  if (!auth.user) return
  try {
    const res = await api.get<Transaction[]>(`/portfolio/${auth.user.id}/transactions`)
    transactions.value = Array.isArray(res.data) ? res.data : []
  } catch (err) {
    console.error('Failed to fetch transactions:', err)
  }
}

// Initial fetch
fetchTransactions()

const buy = async () => {
  if (!auth.user) return
  try {
    await api.post(`/portfolio/${auth.user.id}`, {
      symbol: selectedSymbol.value,
      amount: amount.value,
    })
    alert(`Bought ${amount.value} ${selectedSymbol.value}`)
    showModal.value = false
    fetchTransactions()
  } catch (err: unknown) {
    console.error(err)
    alert((err as any)?.response?.data?.message || 'Failed to buy')
  }
}

const sell = async () => {
  if (!auth.user) return
  try {
    await api.post(`/portfolio/${auth.user.id}/sell`, {
      symbol: selectedSymbol.value,
      amount: amount.value,
    })
    alert(`Sold ${amount.value} ${selectedSymbol.value}`)
    showModal.value = false
    fetchTransactions()
  } catch (err: unknown) {
    console.error(err)
    alert((err as any)?.response?.data?.message || 'Failed to sell')
  }
}
</script>

<template>
  <button @click="showModal = true" class="px-4 py-2 bg-blue-500 text-white rounded">
    Open Trade
  </button>

  <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-black/50">
    <div class="bg-white p-6 rounded-lg w-80 relative text-black">
      <h2 class="text-xl font-bold mb-4">Trade Crypto</h2>

      <select v-model="selectedSymbol" class="w-full border border-gray-300 p-2 rounded mb-4">
        <option v-for="sym in symbols" :key="sym" :value="sym">{{ sym }}</option>
      </select>

      <div class="flex items-center mb-4">
        <button @click="decrement" class="px-3 py-1 bg-gray-200 rounded-l">-</button>
        <input
          type="number"
          v-model.number="amount"
          step="0.1"
          class="w-full border-t border-b border-gray-300 text-center"
        />
        <button @click="increment" class="px-3 py-1 bg-gray-200 rounded-r">+</button>
      </div>

      <div class="flex justify-between">
        <button @click="buy" class="px-4 py-2 bg-green-500 text-white rounded w-1/2 mr-2">
          Buy
        </button>
        <button @click="sell" class="px-4 py-2 bg-red-500 text-white rounded w-1/2 ml-2">
          Sell
        </button>
      </div>

      <button
        @click="showModal = false"
        class="absolute top-2 right-2 text-gray-500 hover:text-gray-800"
      >
        ✕
      </button>
    </div>
  </div>
</template>
