import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080', // change later if needed
})

// read token from localStorage if exists
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
