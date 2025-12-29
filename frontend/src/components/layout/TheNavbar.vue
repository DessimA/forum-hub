<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()
const emit = defineEmits(['open-new-topic'])

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <v-app-bar color="background" flat class="border-b-neon px-4">
    <v-app-bar-title class="font-weight-bold cursor-pointer" @click="router.push('/')">
      <span class="neon-text">Fórum</span><span class="text-white">Hub</span>
    </v-app-bar-title>

    <v-spacer></v-spacer>

    <v-btn 
      v-if="authStore.isAuthenticated"
      variant="text" 
      color="primary" 
      class="mr-2" 
      prepend-icon="mdi-plus-circle"
      @click="emit('open-new-topic')"
    >
      Novo Tópico
    </v-btn>

    <v-menu v-if="authStore.isAuthenticated">
      <template v-slot:activator="{ props }">
        <v-avatar v-bind="props" color="surface" class="cursor-pointer border-neon" size="40">
          <v-icon color="primary">mdi-account</v-icon>
        </v-avatar>
      </template>
      <v-list bg-color="surface">
      <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-account-group" title="Gestão Usuários" to="/admin/users"></v-list-item>
      <v-list-item v-if="authStore.isAdmin" prepend-icon="mdi-shield-check" title="Auditoria" to="/admin/logs"></v-list-item>
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
.border-b-neon {
  border-bottom: 1px solid rgba(0, 240, 255, 0.3) !important;
}

.border-neon {
  border: 1px solid #00f0ff;
}

.neon-text {
  color: #00f0ff;
  text-shadow: 0 0 8px rgba(0, 240, 255, 0.5);
}

.cursor-pointer {
  cursor: pointer;
}
</style>
