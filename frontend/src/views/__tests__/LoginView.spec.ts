import { describe, it, expect, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import LoginView from '../auth/LoginView.vue'
import { createTestingPinia } from '@pinia/testing'
import { useAuthStore } from '@/stores/auth'

// Mock Vuetify minimal
const VContainer = { template: '<div><slot /></div>' }
const VRow = { template: '<div><slot /></div>' }
const VCol = { template: '<div><slot /></div>' }
const VCard = { template: '<div><slot /></div>' }
const VForm = { template: '<form><slot /></form>' }
const VTextField = { 
  props: ['modelValue'], 
  template: '<input :value="modelValue" @input="$emit(\'update:modelValue\', $event.target.value)" />'
}
const VAlert = { template: '<div class="alert"><slot /></div>' }
const VBtn = { template: '<button type="submit"><slot /><slot name="loader" /></button>' }
const AnimatedLogo = { template: '<div />' }
const ThemeToggle = { template: '<div />' }

const globalComponents = {
  VContainer, VRow, VCol, VCard, VForm, VTextField, VAlert, VBtn, AnimatedLogo, ThemeToggle
}

const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush })
}))

describe('LoginView', () => {
  it('submits form with credentials', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createTestingPinia({ stubActions: false })],
        components: globalComponents
      }
    })
    
    const authStore = useAuthStore()
    authStore.login = vi.fn().mockResolvedValue(true)

    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('test@test.com')
    await inputs[1].setValue('password')
    
    // Pass mock event with preventDefault to handle @submit.prevent
    await wrapper.find('form').trigger('submit', {
      preventDefault: vi.fn()
    })
    await flushPromises()

    expect(authStore.login).toHaveBeenCalled()
    expect(mockPush).toHaveBeenCalledWith('/topics')
  })

  it('displays error on failure', async () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [createTestingPinia({ stubActions: false })],
        components: globalComponents
      }
    })
    
    const authStore = useAuthStore()
    authStore.login = vi.fn().mockRejectedValue({
      response: { data: { message: 'Falha na autenticação. Verifique suas credenciais.' } }
    })

    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('test@test.com')
    await inputs[1].setValue('wrong')
    
    await wrapper.find('form').trigger('submit', {
      preventDefault: vi.fn()
    })
    await flushPromises()

    expect(wrapper.find('.alert').exists()).toBe(true)
    expect(wrapper.find('.alert').text()).toContain('Falha na autenticação. Verifique suas credenciais.')
  })
})