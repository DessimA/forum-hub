import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import TopicDetailView from '../topic/TopicDetailView.vue'
import { createTestingPinia } from '@pinia/testing'
import api from '@/services/api'

vi.mock('@/services/api')
vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  useRouter: () => ({ push: vi.fn() })
}))
vi.mock('@/components/layout/TheNavbar.vue', () => ({ default: { template: '<div></div>' } }))

// Mock Vuetify (simplified set)
const VLayout = { template: '<div><slot></slot></div>' }
const VMain = { template: '<div><slot></slot></div>' }
const VContainer = { template: '<div><slot></slot></div>' }
const VCard = { template: '<div><slot></slot></div>' }
const VChip = { template: '<div><slot></slot></div>' }
const VIcon = { template: '<i></i>' }
const VBtn = { template: '<button @click="$emit(\'click\')"><slot></slot></button>' }
const VDivider = { template: '<hr />' }
const VTextarea = { template: '<textarea></textarea>' }
const VDialog = { template: '<div><slot></slot></div>' }
const VCardTitle = { template: '<div><slot></slot></div>' }
const VCardText = { template: '<div><slot></slot></div>' }
const VCardActions = { template: '<div><slot></slot></div>' }
const VTextField = { template: '<input />' }
const VSpacer = { template: '<div></div>' }

const globalComponents = {
  VLayout, VMain, VContainer, VCard, VChip, VIcon, VBtn, VDivider, VTextarea,
  VDialog, VCardTitle, VCardText, VCardActions, VTextField, VSpacer
}

describe('TopicDetailView', () => {
  it('renders topic details', async () => {
    // @ts-ignore
    api.get.mockResolvedValue({
      data: {
        id: 1, title: 'Title', message: 'Message', createdAt: new Date().toISOString(),
        status: 'OPEN', author: 'Author', authorEmail: 'auth@test.com',
        course: 'Java', answers: []
      }
    })

    const wrapper = mount(TopicDetailView, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })

    // wait for onMounted
    await new Promise(resolve => setTimeout(resolve, 0))
    await wrapper.vm.$nextTick()

    expect(wrapper.text()).toContain('Title')
    expect(wrapper.text()).toContain('Message')
    expect(wrapper.text()).toContain('Author')
  })
})