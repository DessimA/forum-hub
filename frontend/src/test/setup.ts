import { vi } from 'vitest'
import { config } from '@vue/test-utils'

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
