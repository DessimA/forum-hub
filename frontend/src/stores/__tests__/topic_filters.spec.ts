import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useTopicStore } from '../topic'
import api from '@/services/api'

vi.mock('@/services/api')

describe('Topic Store - Filters', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should initialize with null filters', () => {
    const store = useTopicStore()
    expect(store.filters.status).toBeNull()
    expect(store.filters.courseId).toBeNull()
  })

  it('should include filters in API call', async () => {
    const store = useTopicStore()
    store.filters.status = 'RESOLVED'
    store.filters.courseId = 'mock-id'
    store.filters.search = 'test query'
    store.filters.sort = 'title,asc'
    
    // @ts-ignore
    api.get.mockResolvedValue({ data: { content: [], number: 0, totalPages: 0, totalElements: 0 } })

    await store.fetchTopics(0)

    expect(api.get).toHaveBeenCalledWith(expect.stringContaining('status=RESOLVED'))
    expect(api.get).toHaveBeenCalledWith(expect.stringContaining('courseId=mock-id'))
    expect(api.get).toHaveBeenCalledWith(expect.stringContaining('title=test%20query'))
    expect(api.get).toHaveBeenCalledWith(expect.stringContaining('sort=title,asc'))
  })

  it('should reset filters correctly', async () => {
    const store = useTopicStore()
    store.filters.status = 'OPEN'
    
    // @ts-ignore
    api.get.mockResolvedValue({ data: { content: [], number: 0, totalPages: 0, totalElements: 0 } })

    await store.resetFilters()

    expect(store.filters.status).toBeNull()
    expect(api.get).toHaveBeenCalledWith(expect.stringContaining('page=0'))
    // URL should not contain status after reset
    const callUrl = (api.get as any).mock.calls[0][0]
    expect(callUrl).not.toContain('status=')
  })

  it('should call close topic API', async () => {
    const store = useTopicStore()
    // @ts-ignore
    api.patch.mockResolvedValue({ data: {} })

    await store.closeTopic('123')

    expect(api.patch).toHaveBeenCalledWith('/topics/123/close')
  })

  it('should call reopen topic API', async () => {
    const store = useTopicStore()
    // @ts-ignore
    api.patch.mockResolvedValue({ data: {} })

    await store.reopenTopic('456')

    expect(api.patch).toHaveBeenCalledWith('/topics/456/reopen')
  })
})
