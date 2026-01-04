import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useFavoriteStore } from '../favoriteStore'
import api from '@/services/api'

vi.mock('@/services/api')

describe('Favorite Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should add favorite and update set', async () => {
    const store = useFavoriteStore()
    const topicId = '123'
    
    // @ts-ignore
    api.post.mockResolvedValue({})

    await store.addFavorite(topicId)

    expect(api.post).toHaveBeenCalledWith('/favorites/topics/123')
    expect(store.isFavorited(topicId)).toBe(true)
  })

  it('should remove favorite and update set', async () => {
    const store = useFavoriteStore()
    const topicId = '123'
    store.favoriteTopicIds.add(topicId)
    
    // @ts-ignore
    api.delete.mockResolvedValue({})

    await store.removeFavorite(topicId)

    expect(api.delete).toHaveBeenCalledWith('/favorites/topics/123')
    expect(store.isFavorited(topicId)).toBe(false)
  })

  it('should load favorites', async () => {
    const store = useFavoriteStore()
    const mockContent = [{ id: '123' }, { id: '456' }]
    
    // @ts-ignore
    api.get.mockResolvedValue({ 
      data: { content: mockContent } 
    })

    await store.loadUserFavorites()

    expect(store.isFavorited('123')).toBe(true)
    expect(store.isFavorited('456')).toBe(true)
    expect(store.isFavorited('789')).toBe(false)
  })
})
