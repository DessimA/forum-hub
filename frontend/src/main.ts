import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import './assets/styles/utilities.css'
import './assets/styles/mobile.css'

import App from './App.vue'
import router from './router'

const lightTheme = {
  dark: false,
  colors: {
    primary: '#0284C7',    // Sky 700
    secondary: '#059669',  // Emerald 600
    accent: '#0284C7',
    background: '#FFFFFF', // Pure White
    surface: '#F3F4F6',    // Gray 100
    'text-primary': '#000000', // Pure Black
    'text-secondary': '#4B5563',
    'border-color': '#E5E7EB',
    error: '#DC2626',
    warning: '#D97706',
  }
}

const darkTheme = {
  dark: true,
  colors: {
    primary: '#38BDF8',    // Sky 400
    secondary: '#10B981',  // Emerald 500
    accent: '#38BDF8',
    background: '#000000', // Pure Black
    surface: '#111111',    // Near Black
    'text-primary': '#FFFFFF', // Pure White
    'text-secondary': '#94A3B8',
    'border-color': '#222222',
    error: '#F87171',
    warning: '#FBBF24',
  }
}

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: localStorage.getItem('theme') || 'dark',
    themes: {
      light: lightTheme,
      dark: darkTheme,
    }
  },
  defaults: {
    VCard: {
      rounded: 'sm',
      elevation: 0,
      variant: 'outlined',
    },
    VBtn: {
      rounded: 'sm',
      elevation: 0,
      variant: 'flat',
      density: 'comfortable',
      height: 40,
    },
    VTextField: {
      rounded: 'sm',
      variant: 'outlined',
      density: 'compact',
      hideDetails: 'auto',
    },
    VSelect: {
      rounded: 'sm',
      variant: 'outlined',
      density: 'compact',
      hideDetails: 'auto',
    },
    VTextarea: {
      rounded: 'sm',
      variant: 'outlined',
      density: 'compact',
      hideDetails: 'auto',
    },
    VChip: {
      rounded: 'sm',
    }
  },
  display: {
    mobileBreakpoint: 'sm',
    thresholds: {
      xs: 0,
      sm: 600,
      md: 960,
      lg: 1280,
      xl: 1920,
    },
  }
})

const app = createApp(App)

app.use(createPinia())
app.use(vuetify)
app.use(router)

app.mount('#app')