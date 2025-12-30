<script setup lang="ts">
import TheNavbar from '@/components/layout/TheNavbar.vue'
import { useAuthStore } from '@/stores/auth'
import { useTopicStore } from '@/stores/topic'

const authStore = useAuthStore()
const topicStore = useTopicStore()

const handleOpenNewTopic = () => {
  topicStore.openDialog()
}
</script>

<template>
  <v-app class="app-container">
    <TheNavbar v-if="authStore.isAuthenticated" @open-new-topic="handleOpenNewTopic" />
    
    <v-main class="main-content">
      <router-view />
    </v-main>
  </v-app>
</template>

<style>
/* Força a transparência no sistema de cores do Vuetify */
:root {
  --v-theme-background: transparent !important;
}

html, body {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  /* Fundo aplicado no nível mais baixo, com gradiente menos opaco para dar visibilidade */
  background: linear-gradient(rgba(10, 14, 39, 0.7), rgba(10, 14, 39, 0.7)), 
              url('@/assets/background.jpg') no-repeat center center fixed !important;
  background-size: cover !important;
}

/* Garante que todos os wrappers do Vuetify sejam transparentes */
.v-application,
.v-application__wrap,
.main-content {
  background: transparent !important;
}

.bg-background {
  background-color: transparent !important;
}

.min-vh-100 {
  min-height: 100vh;
}

/* Ajustes de scrollbar sutis */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(0, 240, 255, 0.2);
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 240, 255, 0.4);
}
</style>