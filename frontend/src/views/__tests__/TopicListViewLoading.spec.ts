import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import TopicListView from '../topic/TopicListView.vue'
import { useTopicStore } from '@/stores/topic'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({ components, directives })

describe('TopicListView - Loading State', () => {
  it('should show progress circular when loading is true', async () => {
    const wrapper = mount(TopicListView, {
      global: {
        plugins: [createTestingPinia({ stubActions: false }), vuetify],
        stubs: { 'v-pagination': true, 'v-dialog': true }
      }
    })

    const store = useTopicStore()
    store.loading = true
    
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.v-progress-circular').exists()).toBe(true)
  })

  it('should hide progress circular when loading is false', async () => {
    const wrapper = mount(TopicListView, {
      global: {
        plugins: [createTestingPinia({ stubActions: false }), vuetify],
        stubs: { 'v-pagination': true, 'v-dialog': true }
      }
    })

    const store = useTopicStore()
    store.loading = false
    
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.v-progress-circular').exists()).toBe(false)
  })
})
