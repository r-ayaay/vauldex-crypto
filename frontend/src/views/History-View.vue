<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

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
import api from '../lib/api'

// Fetch transactions on mount
onMounted(async () => {
  if (!auth.user || !auth.token) return

  try {
    const res = await api.get<Transaction[]>(`/portfolio/${auth.user.id}/transactions`)
    transactions.value = Array.isArray(res.data) ? res.data : []
    console.log('hewoooooo? ' + res.data)
  } catch (err) {
    console.error('Failed to fetch transactions:', err)
  }
})

// Compute value and formatted time
const processedTransactions = computed(() =>
  Array.isArray(transactions.value)
    ? transactions.value.map((tx) => ({
        ...tx,
        value: Math.abs(tx.amount * tx.priceUsd), // <- always positive
        time: new Date(tx.createdAt).toLocaleTimeString('en-GB', {
          hour: '2-digit',
          minute: '2-digit',
        }),
      }))
    : [],
)
</script>

<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-4">Transaction History</h1>
    <table class="w-full border-collapse border border-gray-300">
      <thead>
        <tr class="bg-[#1B2028]">
          <th class="border border-gray-300 p-2 text-left">Time</th>
          <th class="border border-gray-300 p-2 text-left">Symbol</th>
          <th class="border border-gray-300 p-2 text-right">Amount</th>
          <th class="border border-gray-300 p-2 text-right">Price (USD)</th>
          <th class="border border-gray-300 p-2 text-right">Value (USD)</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="tx in processedTransactions"
          :key="tx.id"
          :class="
            tx.type === 'BUY' ? 'bg-[#1ECB4F] hover:bg-green-400' : 'bg-[#F46D22] hover:bg-red-400'
          "
        >
          <td class="border border-gray-300 p-2">{{ tx.time }}</td>
          <td class="border border-gray-300 p-2">{{ tx.symbol }}</td>
          <td class="border border-gray-300 p-2 text-right">{{ tx.amount }}</td>
          <td class="border border-gray-300 p-2 text-right">${{ tx.priceUsd.toLocaleString() }}</td>
          <td class="border border-gray-300 p-2 text-right">${{ tx.value.toLocaleString() }}</td>
        </tr>
        <tr v-if="processedTransactions.length === 0">
          <td colspan="5" class="text-center p-4 text-gray-500">No transactions yet</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
