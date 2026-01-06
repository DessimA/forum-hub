<script setup lang="ts">
import TheNavbar from '@/components/layout/TheNavbar.vue'
import { useAuthStore } from '@/stores/auth'
import { useTopicStore } from '@/stores/topic'
import { useTheme } from 'vuetify'
import { computed } from 'vue'

const authStore = useAuthStore()
const topicStore = useTopicStore()
const theme = useTheme()

const isLightMode = computed(() => theme.global.current.value.dark === false)

const handleOpenNewTopic = () => {
  topicStore.openDialog()
}
</script>

<template>
  <v-app class="app-container">
    <div class="global-background" :class="{ 'invert-bg': isLightMode }"></div>
    <div class="global-overlay"></div>
    
    <TheNavbar v-if="authStore.isAuthenticated" @open-new-topic="handleOpenNewTopic" />
    
    <v-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </v-main>
  </v-app>
</template>

<style>
.app-container {
  background: transparent !important;
}

.global-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('@/assets/background.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  z-index: 0;
  transition: filter 0.5s ease;
}

.invert-bg {
  filter: invert(1) hue-rotate(180deg) brightness(1.2);
}

.global-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(var(--v-theme-background), 0.7);
  z-index: 1;
}

.main-content {
  position: relative;
  z-index: 2;
  min-height: 100vh;
}

.v-application__wrap {
  position: relative;
  z-index: 3;
}

/* Page Transitions */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

html, body {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  overflow-x: hidden;
}

/* Scrollbar synchronization with theme */
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(var(--v-border-color), 0.3);
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--v-border-color), 0.5);
}

.white-space-pre {
  white-space: pre-wrap;
}

/* Base link styles */
a {
  color: rgb(var(--v-theme-primary));
  text-decoration: none;
  transition: all 0.2s ease;
}
a:hover {
  filter: brightness(1.2);
}

/* Responsive Utilities */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>