<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'
import NotificationBell from '@/components/common/NotificationBell.vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const emit = defineEmits<{
  (e: 'open-new-topic'): void
}>()

// O botão "Novo Tópico" deve aparecer apenas na listagem de tópicos (rota home '/')
const isTopicListView = computed(() => route.path === '/')

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <v-app-bar color="background" flat class="border-b-soft px-4">
    <v-app-bar-title class="font-weight-bold cursor-pointer" @click="router.push('/')">
      <span class="logo-text">Fórum</span><span class="text-white">Hub</span>
    </v-app-bar-title>

    <v-spacer></v-spacer>

    <v-btn 
      v-if="authStore.isAuthenticated && isTopicListView"
      variant="text" 
      color="primary" 
      class="mr-2" 
      prepend-icon="mdi-plus-circle"
      @click="emit('open-new-topic')"
    >
      Novo Tópico
    </v-btn>

    <NotificationBell v-if="authStore.isAuthenticated" />

    <v-menu v-if="authStore.isAuthenticated">
      <template v-slot:activator="{ props }">
        <v-avatar v-bind="props" color="surface" class="cursor-pointer border-subtle" size="40">
          <v-icon color="primary">mdi-account</v-icon>
        </v-avatar>
      </template>
      <v-list bg-color="surface" min-width="200">
        <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-account-group" title="Gestão Usuários" to="/admin/users"></v-list-item>
        <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-book-education" title="Gestão Cursos" to="/admin/courses"></v-list-item>
        <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-shield-check" title="Auditoria" to="/admin/logs"></v-list-item>
        <v-divider v-if="authStore.isAdmin"></v-divider>
        <v-list-item 
          prepend-icon="mdi-star-outline" 
          title="Meus Favoritos" 
          to="/favorites"
        ></v-list-item>
        <v-list-item 
          prepend-icon="mdi-account-outline" 
          title="Meu Perfil"  
          value="profile"
          @click="router.push('/profile')"
        ></v-list-item>
        <v-divider></v-divider>
        <v-list-item 
          prepend-icon="mdi-logout" 
          title="Sair" 
          value="logout" 
          color="error"
          @click="handleLogout"
        ></v-list-item>
      </v-list>
    </v-menu>
  </v-app-bar>
</template>

<style scoped>
.border-b-soft {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
}

.border-subtle {
  border: 1px solid rgba(0, 240, 255, 0.2);
}

.logo-text {
  color: #00f0ff;
  letter-spacing: 1px;
}

.cursor-pointer {
  cursor: pointer;
}
</style>