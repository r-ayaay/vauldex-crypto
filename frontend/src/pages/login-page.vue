<!-- Login.vue -->
<template>
  <div class="flex items-center justify-center min-h-screen bg-gray-100">
    <div class="w-full max-w-md bg-white p-8 rounded-md shadow-lg">
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div class="flex justify-center items-center m-auto p-8 stroke-[#1C274C]">
          <div class="h-16"></div>
          <h1 class="text-2xl font-semibold">CryptoTracker</h1>
        </div>

        <!-- Username -->
        <div>
          <label class="block text-sm font-medium mb-1">Username</label>
          <div class="flex">
            <span
              class="inline-flex items-center px-3 bg-gray-200 border rounded-s-sm border-gray-300"
            >
              <UserIcon />
            </span>
            <input
              type="text"
              v-model="username"
              class="rounded-e-sm bg-gray-50 border text-gray-900 block w-full text-sm border-gray-300 p-2.5"
              required
            />
          </div>
        </div>

        <!-- Password -->
        <div>
          <label class="block text-sm font-medium mb-1">Password</label>
          <div class="flex">
            <span
              class="inline-flex items-center px-3 bg-gray-200 border rounded-s-sm border-gray-300"
            >
              <LockIcon />
            </span>
            <input
              type="password"
              v-model="password"
              class="rounded-e-sm bg-gray-50 border text-gray-900 block w-full text-sm border-gray-300 p-2.5"
              required
            />
          </div>
        </div>

        <!-- Actions -->
        <div class="flex items-center justify-between">
          <router-link to="/register" class="text-sm text-blue-600 hover:underline">
            Register
          </router-link>
          <button
            type="submit"
            class="text-white bg-blue-600 hover:bg-blue-700 font-medium rounded-sm text-sm px-5 py-2.5"
          >
            Login
          </button>
        </div>
      </form>

      <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

import UserIcon from '../components/ui/icons/Login/user-icon.vue'
import LockIcon from '../components/ui/icons/Login/lock-icon.vue'

const username = ref('')
const password = ref('')
const error = ref('')

const auth = useAuthStore()
const router = useRouter()

function isAxiosError(err: unknown): err is { response?: { data?: { message?: string } } } {
  return typeof err === 'object' && err !== null && 'response' in err
}

async function handleLogin() {
  try {
    await auth.login(username.value, password.value)
    router.push('/dashboard')
  } catch (err: unknown) {
    if (isAxiosError(err)) {
      error.value = err.response?.data?.message ?? 'Login failed'
    } else {
      error.value = 'Login failed'
    }
  }
}
</script>
