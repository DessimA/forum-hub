import { defineStore } from 'pinia'
import api from '@/services/api'
import type { LoginRequest, AuthResponse } from '@/types/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    loading: false
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    userEmail: (state) => {
      if (!state.token) return null
      try {
        const payload = JSON.parse(atob(state.token.split('.')[1]))
        return payload.sub
      } catch (e) { return null }
    },
    isAdmin: (state) => {
      if (!state.token) return false
      try {
        const payload = JSON.parse(atob(state.token.split('.')[1]))
        return payload.role === 'ROLE_ADMIN'
      } catch (e) { return false }
    },
    isModerator: (state) => {
      if (!state.token) return false
      try {
        const payload = JSON.parse(atob(state.token.split('.')[1]))
        return payload.role === 'ROLE_MODERATOR' || payload.role === 'ROLE_ADMIN'
      } catch (e) { return false }
    }
  },

  actions: {
    async login(credentials: LoginRequest) {
      this.loading = true
      try {
        const response = await api.post<AuthResponse>('/auth/login', credentials)
        this.token = response.data.token
        localStorage.setItem('token', this.token)
        return true
      } catch (error) {
        console.error('Login failed', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    logout() {
      this.token = ''
      localStorage.removeItem('token')
    }
  }
})