import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import TopicDetailView from '../topic/TopicDetailView.vue'
import { createTestingPinia } from '@pinia/testing'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

// Mock Vuetify
const VContainer = { template: '<div><slot /></div>' }
const VCard = { template: '<div><slot /></div>' }
const VBtn = { 
  props: ['icon'],
  template: '<button :data-icon="icon"><slot /></button>' 
}
const VIcon = { template: '<i />' }
const VChip = { template: '<span><slot /></span>' }
const VDivider = { template: '<hr />' }
const VTextarea = { props: ['modelValue'], template: '<textarea :value="modelValue" />' }
const VTextField = { props: ['modelValue'], template: '<input :value="modelValue" />' }
const VDialog = { props: ['modelValue'], template: '<div><slot v-if="modelValue" /></div>' }

const globalComponents = { VContainer, VCard, VBtn, VIcon, VChip, VDivider, VTextarea, VTextField, VDialog }

vi.mock('@/services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn()
  }
}))

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  useRouter: () => ({ push: vi.fn() })
}))

describe('TopicDetailView - Moderator Rules', () => {
  const mockTopic = {
    id: 1,
    title: 'Test Topic',
    author: 'Author',
    authorId: 1,
    authorEmail: 'author@test.com',
    answers: [
      { id: 10, author: 'Other User', authorId: 2, authorEmail: 'other@test.com', message: 'Hello', solution: false, createdAt: new Date().toISOString() }
    ],
    createdAt: new Date().toISOString(),
    course: 'Java',
    status: 'OPEN'
  }

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(api.get).mockResolvedValue({ data: mockTopic })
  })

  it('shows punishment button for MODERATOR on other users answers', async () => {
    const pinia = createTestingPinia({ stubActions: false })
    const authStore = useAuthStore()
    
    // Use any to bypass TypeScript read-only check on getters in tests
    const authStoreAny = authStore as any
    authStoreAny.isModerator = true
    authStoreAny.isAdmin = false
    authStoreAny.userEmail = 'mod@test.com'

    const wrapper = mount(TopicDetailView, {
      global: {
        plugins: [pinia],
        components: globalComponents
      }
    })
    
    await flushPromises()
    const buttons = wrapper.findAll('button')
    const hasBlockBtn = buttons.some(b => b.attributes('data-icon')?.includes('mdi-account-cancel'))
    expect(hasBlockBtn).toBe(true)
  })

  it('hides punishment button for REGULAR USER', async () => {
    const pinia = createTestingPinia({ stubActions: false })
    const authStore = useAuthStore()
    
    const authStoreAny = authStore as any
    authStoreAny.isModerator = false
    authStoreAny.isAdmin = false
    authStoreAny.userEmail = 'user@test.com'

    const wrapper = mount(TopicDetailView, {
      global: {
        plugins: [pinia],
        components: globalComponents
      }
    })
    
    await flushPromises()
    const buttons = wrapper.findAll('button')
    const hasBlockBtn = buttons.some(b => b.attributes('data-icon')?.includes('mdi-account-cancel'))
    expect(hasBlockBtn).toBe(false)
  })
})