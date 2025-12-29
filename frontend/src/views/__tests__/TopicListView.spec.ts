import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TopicListView from '../topic/TopicListView.vue'
import { createTestingPinia } from '@pinia/testing'
import api from '@/services/api'

vi.mock('@/services/api')
vi.mock('@/components/layout/TheNavbar.vue', () => ({ default: { template: '<div><slot></slot></div>' } }))

// Mock Vuetify
const VLayout = { template: '<div><slot></slot></div>' }
const VMain = { template: '<div><slot></slot></div>' }
const VContainer = { template: '<div><slot></slot></div>' }
const VRow = { template: '<div><slot></slot></div>' }
const VCol = { template: '<div><slot></slot></div>' }
const VChip = { template: '<div><slot></slot></div>' }
const VCard = { template: '<div><slot></slot></div>' }
const VCardText = { template: '<div><slot></slot></div>' }
const VProgressCircular = { template: '<div class="loading"></div>' }
const VPagination = { template: '<div></div>' }
const VDialog = { template: '<div><slot></slot></div>' }
const VCardTitle = { template: '<div><slot></slot></div>' }
const VCardActions = { template: '<div><slot></slot></div>' }
const VForm = { template: '<form><slot></slot></form>' }
const VTextField = { template: '<input />' }
const VSelect = { template: '<select></select>' }
const VTextarea = { template: '<textarea></textarea>' }
const VSpacer = { template: '<div></div>' }
const VBtn = { template: '<button></button>' }
const VIcon = { template: '<i></i>' }

const globalComponents = {
  VLayout, VMain, VContainer, VRow, VCol, VChip, VCard, VCardText, VProgressCircular,
  VPagination, VDialog, VCardTitle, VCardActions, VForm, VTextField, VSelect,
  VTextarea, VSpacer, VBtn, VIcon
}

describe('TopicListView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders topics list', async () => {
    const wrapper = mount(TopicListView, {
      global: {
        plugins: [createTestingPinia({
          initialState: {
            topic: {
              topics: [
                { id: 1, title: 'Topic 1', message: 'Msg 1', author: 'Author', createdAt: new Date().toISOString(), status: 'OPEN', course: 'Java' }
              ],
              loading: false,
              pagination: { totalPages: 1, totalElements: 1, page: 0 }
            }
          }
        })],
        components: globalComponents
      }
    })
    
    // fetchCourses is called on mounted
    expect(api.get).toHaveBeenCalledWith('/courses')
    
    expect(wrapper.text()).toContain('Topic 1')
    expect(wrapper.text()).toContain('Author')
  })

  it('shows loading state', () => {
    const wrapper = mount(TopicListView, {
      global: {
        plugins: [createTestingPinia({
          initialState: {
            topic: {
              topics: [],
              loading: true,
              pagination: { totalPages: 0, totalElements: 0, page: 0 }
            }
          }
        })],
        components: globalComponents
      }
    })

    expect(wrapper.find('.loading').exists()).toBe(true)
  })
})
