import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import TheNavbar from '../layout/TheNavbar.vue'
import { createTestingPinia } from '@pinia/testing'

// Extreme simplify mocks to avoid JSDOM parsing issues
const VAppBar = { template: '<div><slot /></div>' }
const VAppBarTitle = { template: '<div><slot /></div>' }
const VSpacer = { template: '<div />' }
const VBtn = { template: '<button @click="$emit(\'click\')"><slot /></button>' }
const VMenu = { template: '<div><slot name="activator" /> <slot /></div>' }
const VAvatar = { template: '<div class="avatar"><slot /></div>' }
const VIcon = { template: '<i />' }
const VList = { template: '<div><slot /></div>' }
const VListItem = { template: '<div />' }
const VDivider = { template: '<hr />' }

const globalComponents = {
  VAppBar, VAppBarTitle, VSpacer, VBtn, VMenu, VAvatar, VIcon, VList, VListItem, VDivider
}

const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush })
}))

describe('TheNavbar', () => {
  it('renders correctly', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.text()).toContain('FórumHub')
  })

  it('shows user menu when authenticated', () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: {
            auth: { token: 'valid.token.here' }
          }
        })],
        components: globalComponents
      }
    })
    
    expect(wrapper.find('.avatar').exists()).toBe(true)
  })

  it('emits open-new-topic event', async () => {
    const wrapper = mount(TheNavbar, {
      global: {
        plugins: [createTestingPinia({
          initialState: {
            auth: { token: 'valid.token.here' }
          }
        })],
        components: globalComponents
      }
    })

    const btn = wrapper.find('button')
    await btn.trigger('click')
    expect(wrapper.emitted()).toHaveProperty('open-new-topic')
  })
})
