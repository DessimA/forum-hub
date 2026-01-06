import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import LandingPage from '../LandingPage.vue'
import { createTestingPinia } from '@pinia/testing'

const VBtn = { template: '<button @click="$emit(\'click\')"><slot /></button>' }
const VRow = { template: '<div><slot /></div>' }
const VCol = { template: '<div><slot /></div>' }
const VCard = { template: '<div><slot /></div>' }
const VIcon = { template: '<i />' }
const AnimatedLogo = { template: '<div />' }
const ThemeToggle = { template: '<div />' }

const globalComponents = {
  VBtn, VRow, VCol, VCard, VIcon, AnimatedLogo, ThemeToggle
}

const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush })
}))

describe('LandingPage', () => {
  it('renders correctly', () => {
    const wrapper = mount(LandingPage, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })
    expect(wrapper.text()).toContain('Conectando conhecimento')
    expect(wrapper.text()).toContain('Discussões Organizadas')
  })

  it('navigates to register on CTA click', async () => {
    const wrapper = mount(LandingPage, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })
    
    const registerBtn = wrapper.findAll('button').find(b => b.text().includes('Criar Conta'))
    await registerBtn?.trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/register')
  })

  it('navigates to login on enter click', async () => {
    const wrapper = mount(LandingPage, {
      global: {
        plugins: [createTestingPinia()],
        components: globalComponents
      }
    })
    
    const loginBtn = wrapper.findAll('button').find(b => b.text().includes('Entrar'))
    await loginBtn?.trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/login')
  })
})