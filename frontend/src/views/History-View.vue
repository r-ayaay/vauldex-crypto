<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../lib/api'

interface Transaction {
  id: number
  symbol: string
  amount: number
  type: string
  priceUsd: number
  createdAt: string
}

const auth = useAuthStore()
const transactions = ref<Transaction[]>([])

// Fetch transactions on mount
onMounted(async () => {
  if (!auth.user || !auth.token) return

  try {
    const res = await api.get<Transaction[]>(`/portfolio/${auth.user.id}/transactions`)
    transactions.value = Array.isArray(res.data) ? res.data : []
  } catch (err) {
    console.error('Failed to fetch transactions:', err)
  }
})

// Compute value and formatted datetime
const processedTransactions = computed(() =>
  Array.isArray(transactions.value)
    ? transactions.value.map((tx) => {
        const date = new Date(tx.createdAt)
        const formatted = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1)
          .toString()
          .padStart(
            2,
            '0',
          )}/${date.getFullYear()} ${date.getHours().toString().padStart(2, '0')}:${date
          .getMinutes()
          .toString()
          .padStart(2, '0')}`
        return {
          ...tx,
          value: Math.abs(tx.amount * tx.priceUsd),
          datetime: formatted,
        }
      })
    : [],
)
</script>

<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-4">Transaction History</h1>

    <table class="w-full border-collapse border bg-[#1B2028] rounded-xl overflow-hidden">
      <thead>
        <tr class="bg-[#2A2F3A]">
          <th class="p-4 text-gray-400 font-thin text-left rounded-tl-xl">Date/Time</th>
          <th class="p-4 text-gray-400 font-thin text-left">Symbol</th>
          <th class="p-4 text-gray-400 font-thin text-right">Amount</th>
          <th class="p-4 text-gray-400 font-thin text-right">Price (USD)</th>
          <th class="p-4 text-gray-400 font-thin text-right rounded-tr-xl">Value (USD)</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="tx in processedTransactions"
          :key="tx.id"
          :class="
            tx.type === 'BUY'
              ? 'bg-[#1ECB4F] hover:bg-green-400'
              : 'bg-[#F46D22] hover:bg-orange-400'
          "
          class="transition-colors duration-200"
        >
          <td class="p-4">{{ tx.datetime }}</td>
          <td class="p-4">{{ tx.symbol }}</td>
          <td class="p-4 text-right">{{ tx.amount }}</td>
          <td class="p-4 text-right">${{ tx.priceUsd.toLocaleString() }}</td>
          <td class="p-4 text-right">${{ tx.value.toLocaleString() }}</td>
        </tr>
        <tr v-if="processedTransactions.length === 0">
          <td colspan="5" class="text-center p-4 text-gray-500">No transactions yet</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
