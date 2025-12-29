/**
 * @vitest-environment jsdom
 */
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'
import api from '@/services/api'

vi.mock('@/services/api')

describe('Auth Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    window.localStorage.clear()
  })

  it('should initialize with empty token', () => {
    const store = useAuthStore()
    expect(store.token).toBe('')
    expect(store.isAuthenticated).toBe(false)
  })

  it('should store token on successful login', async () => {
    const store = useAuthStore()
    const mockToken = 'mock.jwt.token'
    
    // @ts-ignore
    api.post.mockResolvedValue({ data: { token: mockToken } })

    await store.login({ email: 'test@test.com', senha: 'password' })

    expect(store.token).toBe(mockToken)
    expect(window.localStorage.getItem('token')).toBe(mockToken)
    expect(store.isAuthenticated).toBe(true)
  })

  it('should clear token on logout', () => {
    const store = useAuthStore()
    store.token = 'some-token'
    
    store.logout()

    expect(store.token).toBe('')
    expect(window.localStorage.getItem('token')).toBeNull()
  })
})
