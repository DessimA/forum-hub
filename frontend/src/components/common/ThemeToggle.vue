<template>
  <v-btn
    icon
    variant="outlined"
    size="36"
    class="theme-toggle rounded-circle"
    @click="toggleTheme"
    :title="isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'"
    data-cy="theme-toggle"
  >
    <transition name="rotate-fade" mode="out-in">
      <v-icon v-if="isDark" key="sun" size="18">mdi-white-balance-sunny</v-icon>
      <v-icon v-else key="moon" size="18">mdi-moon-waning-crescent</v-icon>
    </transition>
  </v-btn>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useTheme } from 'vuetify'

const theme = useTheme()
const isDark = computed(() => theme.global.name.value === 'dark')

const toggleTheme = () => {
  const newTheme = theme.global.current.value.dark ? 'light' : 'dark'
  theme.global.name.value = newTheme
  localStorage.setItem('theme', newTheme)
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) {
    theme.global.name.value = savedTheme
  } else {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    theme.global.name.value = prefersDark ? 'dark' : 'light'
  }
})
</script>

<style scoped>
.theme-toggle {
  border-color: rgba(var(--v-border-color), 0.2) !important;
  transition: all 0.3s ease-in-out !important;
  background: rgba(var(--v-theme-surface), 0.5) !important;
  backdrop-filter: blur(4px);
}

.theme-toggle:hover {
  filter: brightness(1.1);
  transform: scale(1.05);
}

.rotate-fade-enter-active,
.rotate-fade-leave-active {
  transition: all 0.3s ease-in-out;
}

.rotate-fade-enter-from {
  opacity: 0;
  transform: rotate(-180deg) scale(0.5);
}

.rotate-fade-leave-to {
  opacity: 0;
  transform: rotate(180deg) scale(0.5);
}
</style>
