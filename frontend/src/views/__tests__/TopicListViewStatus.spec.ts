import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import TopicListView from '../topic/TopicListView.vue'
import { useTopicStore } from '@/stores/topic'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({ components, directives })

describe('TopicListView - Status Rendering', () => {
  const globalOptions = {
    plugins: [createTestingPinia({ stubActions: false }), vuetify],
    stubs: {
      'v-pagination': true,
      'v-dialog': true,
      'BaseChip': { template: '<span class="status-chip"><slot /></span>' },
      'FavoriteButton': true
    }
  }

  it('should display correct text for CLOSED status', async () => {
    const wrapper = mount(TopicListView, {
      global: globalOptions
    })

    const store = useTopicStore()
    store.topics = [
      { id: '1', title: 'Closed Topic', status: 'CLOSED', author: 'Author', course: 'Java', createdAt: new Date().toISOString() }
    ]
    store.loading = false

    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('CLOSED')
  })

  it('should display correct text for OPEN status', async () => {
    const wrapper = mount(TopicListView, {
      global: {
        plugins: [createTestingPinia({ stubActions: false }), vuetify],
        stubs: {
          'v-pagination': true,
          'v-dialog': true
        }
      }
    })

    const store = useTopicStore()
    store.topics = [
      { id: '2', title: 'Open Topic', status: 'OPEN', author: 'Author', course: 'Java', createdAt: new Date().toISOString() }
    ]
    store.loading = false

    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('OPEN')
  })
})
