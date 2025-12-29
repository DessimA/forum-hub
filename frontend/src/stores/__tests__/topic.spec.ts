import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTopicStore } from '../topic'
import api from '@/services/api'

vi.mock('@/services/api')

describe('Topic Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should fetch topics and update state', async () => {
    const store = useTopicStore()
    const mockTopics = [
      { id: 1, title: 'Test Topic' }
    ]
    
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
  })
})
