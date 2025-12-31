import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTopicStore } from '../topic'
import api from '@/services/api'

vi.mock('@/services/api')

describe('Topic Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should fetch topics and update state', async () => {
    const store = useTopicStore()
    const mockTopics = [{ id: 1, title: 'Test Topic' }]
    
    // @ts-ignore
    api.get.mockResolvedValue({ 
      data: { 
        content: mockTopics,
        number: 0,
        totalPages: 1,
        totalElements: 1
      } 
    })

    await store.fetchTopics()

    expect(store.topics).toHaveLength(1)
    expect(store.topics[0].title).toBe('Test Topic')
    expect(store.loading).toBe(false)
    expect(api.get).toHaveBeenCalledWith('/topics?page=0&size=10&sort=createdAt,desc')
  })

  it('should fetch topics with pagination parameters', async () => {
    const store = useTopicStore()
    // @ts-ignore
    api.get.mockResolvedValue({ data: { content: [] } })

    await store.fetchTopics(2, 20)

    expect(api.get).toHaveBeenCalledWith('/topics?page=2&size=20&sort=createdAt,desc')
  })

  it('should handle fetch error', async () => {
    const store = useTopicStore()
    // @ts-ignore
    api.get.mockRejectedValue(new Error('Network error'))

    await store.fetchTopics()

    expect(store.topics).toEqual([])
    expect(store.loading).toBe(false)
  })

  it('should set loading state correctly', async () => {
    const store = useTopicStore()
    
    let resolvePromise: any
    const promise = new Promise(resolve => { resolvePromise = resolve })
    
    // @ts-ignore
    api.get.mockReturnValue(promise)

    const fetchPromise = store.fetchTopics()
    
    expect(store.loading).toBe(true)
    
    resolvePromise({ data: { content: [] } })
    await fetchPromise
    
    expect(store.loading).toBe(false)
  })
})
