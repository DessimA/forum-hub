import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCourseStore } from '../course'
import { courseService } from '@/services/api'

vi.mock('@/services/api', () => ({
  courseService: {
    list: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    remove: vi.fn()
  }
}))

describe('Course Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetches courses successfully', async () => {
    const store = useCourseStore()
    const mockData = { content: [{ id: 1, name: 'Java', category: 'Backend' }], totalPages: 1, number: 0 }
    // Cast to any to avoid full AxiosResponse requirement in tests
    vi.mocked(courseService.list).mockResolvedValue({ data: mockData } as any)

    await store.fetchCourses()

    expect(store.courses).toHaveLength(1)
    expect(store.courses[0].name).toBe('Java')
  })

  it('creates a course and refreshes list', async () => {
    const store = useCourseStore()
    vi.mocked(courseService.create).mockResolvedValue({} as any)
    vi.mocked(courseService.list).mockResolvedValue({ data: { content: [], totalPages: 0, number: 0 } } as any)

    await store.createCourse({ name: 'Vue', category: 'Frontend' })

    expect(courseService.create).toHaveBeenCalled()
    expect(courseService.list).toHaveBeenCalled()
  })
})