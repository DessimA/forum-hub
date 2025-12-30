import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TheNavbar from '../layout/TheNavbar.vue'
import { createTestingPinia } from '@pinia/testing'

// Mock Vuetify minimal
const VAppBar = { template: '<header><slot /></header>' }
const VAppBarTitle = { template: '<div><slot /></div>' }
const VSpacer = { template: '<div />' }
const VBtn = { template: '<button @click="$emit(\'click\')"><slot /></button>' }
const VMenu = { template: '<div><slot name="activator" :props="{}" /><slot /></div>' }
const VAvatar = { template: '<div v-bind="$attrs"><slot /></div>' }
const VIcon = { template: '<i />' }
const VList = { template: '<div><slot /></div>' }
const VListItem = { props: ['to'], template: '<div :to="to"><slot /></div>' }
const VDivider = { template: '<hr />' }

const globalComponents = {
  VAppBar, VAppBarTitle, VSpacer, VBtn, VMenu, VAvatar, VIcon, VList, VListItem, VDivider
}

const mockPush = vi.fn()
let mockRoutePath = '/'

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
  useRoute: () => ({ path: mockRoutePath })
}))

describe('TheNavbar', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockRoutePath = '/'
  })

  it('renders correctly', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })
    expect(wrapper.text()).toContain('FórumHub')
  })

  it('shows user menu when authenticated', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { user: { name: 'User' }, token: 'abc' } }
        })],
        components: globalComponents
      }
    })
    expect(wrapper.findComponent(VMenu).exists()).toBe(true)
  })

  it('emits open-new-topic event only on home path', async () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { user: { name: 'User' }, token: 'abc' } }
        })],
        components: globalComponents
      }
    })
    
    const btn = wrapper.find('button') // Novo Tópico button
    await btn.trigger('click')
    expect(wrapper.emitted('open-new-topic')).toBeTruthy()
  })

  it('hides new topic button when not on home path', () => {
    mockRoutePath = '/admin/courses'
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { user: { name: 'User' }, token: 'abc' } }
        })],
        components: globalComponents
      }
    })
    
    const buttons = wrapper.findAll('button')
    const hasNewTopicBtn = buttons.some(b => b.text().includes('Novo Tópico'))
    expect(hasNewTopicBtn).toBe(false)
  })
})
