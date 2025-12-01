/* eslint-disable @typescript-eslint/no-explicit-any */

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../lib/api'

const auth = useAuthStore()
const showModal = ref(false)
const selectedSymbol = ref('BTC')
const amount = ref(0)
const symbols = ['BTC', 'ETH', 'SHIB', 'DOGE', 'PEPE', 'XRP', 'ADA', 'SOL']

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
  <button @click="showModal = true" class="px-4 py-2 bg-blue-500 text-white rounded-lg">
    Open Trade
  </button>

  <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-black/50">
    <div class="bg-[#1B2028] p-6 rounded-xl w-80 relative text-white shadow-lg">
      <h2 class="text-xl font-bold mb-4 text-center">Trade Crypto</h2>

      <select
        v-model="selectedSymbol"
        class="w-full border border-gray-600 p-2 rounded mb-4 bg-[#2A2F3A] text-white"
      >
        <option v-for="sym in symbols" :key="sym" :value="sym">{{ sym }}</option>
      </select>

      <input
        type="number"
        v-model.number="amount"
        step="0.1"
        placeholder="Amount"
        class="w-full border border-gray-600 p-2 rounded mb-4 text-center bg-[#2A2F3A] text-white"
      />

      <div class="flex gap-2">
        <button @click="buy" class="flex-1 bg-green-500 hover:bg-green-600 p-2 rounded-lg">
          Buy
        </button>
        <button @click="sell" class="flex-1 bg-red-500 hover:bg-red-600 p-2 rounded-lg">
          Sell
        </button>
      </div>

      <button
        @click="showModal = false"
        class="absolute top-2 right-2 text-gray-400 hover:text-white text-lg"
      >
        ✕
      </button>
    </div>
  </div>
</template>
