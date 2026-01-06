import { vi } from 'vitest'
import { config } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({
  components,
  directives,
})

config.global.plugins = [vuetify]

// Configurar mocks globais do Browser
class ResizeObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}

class IntersectionObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}

vi.stubGlobal('ResizeObserver', ResizeObserverMock)
vi.stubGlobal('IntersectionObserver', IntersectionObserverMock)

// Mock matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// Mock do Vue Router e outros componentes globais
config.global.mocks = {
  $router: {
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn()
  },
  $route: {
    path: '/',
    params: {},
    query: {}
  }
}
