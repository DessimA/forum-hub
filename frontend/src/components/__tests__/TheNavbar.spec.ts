import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TheNavbar from '../layout/TheNavbar.vue'
import { createTestingPinia } from '@pinia/testing'

// Mock Vuetify minimal
const VAppBar = { template: '<header v-bind="$attrs"><slot /></header>' }
const VAppBarTitle = { template: '<div v-bind="$attrs"><slot /></div>' }
const VAppBarNavIcon = { template: '<button v-bind="$attrs" @click="$emit(\'click\')"><slot /></button>' }
const VNavigationDrawer = { props: ['modelValue'], template: '<div v-bind="$attrs"><slot v-if="modelValue" /></div>' }
const VSpacer = { template: '<div v-bind="$attrs" />' }
const VBtn = { 
  template: '<button v-bind="$attrs" @click="$emit(\'click\')"><slot /></button>'
}
const VAvatar = { template: '<div v-bind="$attrs" @click="$emit(\'click\')"><slot /></div>' }
const VIcon = { template: '<i v-bind="$attrs" />' }
const VList = { template: '<div v-bind="$attrs"><slot /></div>' }
const VListItem = { props: ['to'], template: '<div v-bind="$attrs" :to="to"><slot /></div>' }
const VDivider = { template: '<hr v-bind="$attrs" />' }
const NotificationBell = { template: '<div />' }
const ThemeToggle = { template: '<div />' }
const AnimatedLogo = { template: '<div class="animated-logo">FórumHub</div>' }

const globalComponents = {
  VAppBar, VAppBarTitle, VAppBarNavIcon, VNavigationDrawer, VSpacer, VBtn, VAvatar, VIcon, VList, VListItem, VDivider,
  NotificationBell, ThemeToggle, AnimatedLogo
}

const mockPush = vi.fn()
let mockRoutePath = '/topics'

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
  useRoute: () => ({ path: mockRoutePath })
}))

import { createVuetify } from 'vuetify'
const vuetify = createVuetify()

describe('TheNavbar', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockRoutePath = '/topics'
  })

  it('renders correctly', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia(), vuetify],
        components: globalComponents
      }
    })
    expect(wrapper.text()).toContain('FórumHub')
  })

  it('shows user menu when authenticated', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { token: 'abc' } }
        }), vuetify],
        components: globalComponents
      }
    })
    expect(wrapper.find('[data-cy="user-avatar"]').exists()).toBe(true)
  })

  it('emits open-new-topic event only on topics path', async () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { token: 'abc' } }
        }), vuetify],
        components: globalComponents
      }
    })
    
    const btn = wrapper.find('[data-cy="nav-new-topic"]') 
    await btn.trigger('click')
    expect(wrapper.emitted('open-new-topic')).toBeTruthy()
  })

  it('hides new topic button when not on topics path', () => {
    mockRoutePath = '/admin/courses'
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: { auth: { token: 'abc' } }
        }), vuetify],
        components: globalComponents
      }
    })
    
    const hasNewTopicBtn = wrapper.find('[data-cy="nav-new-topic"]').exists()
    expect(hasNewTopicBtn).toBe(false)
  })
})