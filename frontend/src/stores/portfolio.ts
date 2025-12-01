import { defineStore } from 'pinia'
import api from '../lib/api'

export interface Holding {
  symbol: string
  amount: number
  valueUsd: number
}

export const usePortfolioStore = defineStore('portfolio', {
  state: () => ({
    holdings: [] as Holding[],
    loading: false,
  }),

  actions: {
    async fetchMyPortfolio() {
      this.loading = true
      try {
        const res = await api.get('/portfolio/me')
        this.holdings = res.data
      } finally {
        this.loading = false
      }
    },

    async addHolding(userId: number, symbol: string, amount: number) {
      const res = await api.post(`/portfolio/${userId}`, {
        symbol,
        amount,
      })

      // re-fetch after adding
      await this.fetchMyPortfolio()
      return res.data
    },
  },
})
