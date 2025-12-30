import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'
import api from '@/services/api'

vi.mock('@/services/api', () => ({
  default: {
    post: vi.fn()
  }
}))

describe('Auth Store - Error Scenarios', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fails login and clears token', async () => {
    const store = useAuthStore()
    vi.mocked(api.post).mockRejectedValue(new Error('401'))

    await expect(store.login({ email: 'err@test.com', password: '123' }))
      .rejects.toThrow()
    
    expect(store.token).toBe('')
    expect(store.isAuthenticated).toBe(false)
  })
})