import { defineStore } from 'pinia'
import api from '../lib/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null') as null | {
      id: number
      username: string
    },
    token: localStorage.getItem('token') || null,
  }),

  actions: {
    async login(username: string, password: string) {
      const res = await api.post('/auth/login', { username, password })

      this.token = res.data.token
      localStorage.setItem('token', res.data.token)

      this.user = {
        id: res.data.user_id,
        username: res.data.username,
      }

      // Store user in localStorage
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})
