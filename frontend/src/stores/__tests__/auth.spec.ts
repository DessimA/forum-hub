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
    vi.clearAllMocks()
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

    await store.login({ email: 'test@test.com', password: 'password' })

    expect(store.token).toBe(mockToken)
    expect(window.localStorage.getItem('token')).toBe(mockToken)
    expect(store.isAuthenticated).toBe(true)
  })

  it('should handle login error', async () => {
    const store = useAuthStore()
    // @ts-ignore
    api.post.mockRejectedValue(new Error('Login failed'))

    await expect(store.login({ email: 'test@test.com', password: 'wrong' })).rejects.toThrow('Login failed')
    expect(store.token).toBe('')
    expect(store.loading).toBe(false)
  })

  it('should clear token on logout', () => {
    const store = useAuthStore()
    store.token = 'some-token'
    
    store.logout()

    expect(store.token).toBe('')
    expect(window.localStorage.getItem('token')).toBeNull()
  })

  it('should correctly identify admin role', () => {
    const store = useAuthStore()
    // {"role":"ROLE_ADMIN"}
    const adminToken = 'header.eyJyb2xlIjoiUk9MRV9BRE1JTiJ9.signature'
    store.token = adminToken
    
    expect(store.isAdmin).toBe(true)
    expect(store.isModerator).toBe(true)
  })

  it('should correctly identify user role', () => {
    const store = useAuthStore()
    // {"role":"ROLE_USER"}
    const userToken = 'header.eyJyb2xlIjoiUk9MRV9VU0VSIn0=.signature'
    store.token = userToken
    
    expect(store.isAdmin).toBe(false)
    expect(store.isModerator).toBe(false)
  })

  it('should extract user email from token', () => {
    const store = useAuthStore()
    // {"sub":"test@test.com"}
    const token = 'header.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0=.signature'
    store.token = token
    
    expect(store.userEmail).toBe('test@test.com')
  })
})
