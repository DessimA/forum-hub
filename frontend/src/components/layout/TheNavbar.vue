<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { useDisplay } from 'vuetify'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import NotificationBell from '@/components/common/NotificationBell.vue'
import AnimatedLogo from '@/components/common/AnimatedLogo.vue'

const { mobile } = useDisplay()
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const drawer = ref(false)

const emit = defineEmits<{
  (e: 'open-new-topic'): void
}>()

const isTopicListView = computed(() => route.path === '/topics')

const handleLogout = () => {
  authStore.logout()
  router.push('/')
  drawer.value = false
}

const navItems = [
  { id: 'explorar', title: 'Explorar Tópicos', icon: 'mdi-home-outline', to: '/topics' },
  { id: 'favoritos', title: 'Meus Favoritos', icon: 'mdi-star-outline', to: '/favorites' },
  { id: 'perfil', title: 'Meu Perfil', icon: 'mdi-account-outline', to: '/profile' },
]

const adminItems = [
  { title: 'Gestão de Usuários', icon: 'mdi-account-group-outline', to: '/admin/users' },
  { title: 'Gestão de Cursos', icon: 'mdi-book-education-outline', to: '/admin/courses' },
  { title: 'Auditoria', icon: 'mdi-shield-check-outline', to: '/admin/logs' },
]
</script>

<template>
  <v-app-bar color="rgba(var(--v-theme-surface), 0.8)" flat class="border-b px-2 px-md-6" height="48" style="backdrop-filter: blur(4px);">
    <template v-if="authStore.isAuthenticated">
      <!-- Left: Hamburger + Logo -->
      <div class="d-flex align-center">
        <v-btn 
          icon="mdi-menu" 
          variant="text" 
          size="small" 
          @click="drawer = !drawer" 
          data-cy="hamburger-menu"
        ></v-btn>
        
        <div 
          class="ml-2 d-flex align-center cursor-pointer" 
          @click="router.push('/topics')" 
          data-cy="navbar-logo"
        >
          <AnimatedLogo :size="16" />
        </div>
      </div>

      <v-spacer></v-spacer>

      <!-- Right: Actions -->
      <div class="d-flex align-center gap-2">
        <!-- Responsive New Topic Button -->
        <v-btn 
          v-if="isTopicListView"
          color="primary" 
          variant="flat" 
          :height="32"
          :class="mobile ? 'rounded-circle' : 'px-4 font-bold rounded-pill'"
          :icon="mobile ? 'mdi-plus' : undefined"
          :prepend-icon="mobile ? undefined : 'mdi-plus'"
          @click="emit('open-new-topic')"
          data-cy="nav-new-topic"
        >
          <span v-if="!mobile">Novo Tópico</span>
        </v-btn>

        <!-- Theme Toggle -->
        <ThemeToggle class="hidden-sm-and-down" />

        <!-- Notification Bell -->
        <NotificationBell />

        <!-- Avatar -->
        <v-avatar 
          color="primary" 
          size="32" 
          class="cursor-pointer border ml-1" 
          @click="drawer = !drawer"
          data-cy="user-avatar"
        >
          <v-icon color="white" size="18">mdi-account</v-icon>
        </v-avatar>
      </div>
    </template>
    
    <template v-else>
      <div class="ml-4 cursor-pointer" @click="router.push('/')">
        <AnimatedLogo :size="20" />
      </div>
    </template>
  </v-app-bar>

  <!-- Navigation Drawer -->
  <v-navigation-drawer 
    v-model="drawer" 
    temporary 
    color="surface" 
    width="280"
    data-cy="nav-drawer"
    class="d-flex flex-column"
  >
    <!-- Header: Fixed -->
    <div class="flex-shrink-0 pa-4 d-flex justify-space-between align-center border-b" style="height: 64px">
      <AnimatedLogo :size="18" />
      <v-btn icon="mdi-close" variant="text" size="small" @click="drawer = false"></v-btn>
    </div>

    <!-- Scrollable Menu Content -->
    <div class="flex-grow-1 overflow-y-auto pa-2">
      <v-list density="compact" nav bg-color="transparent">
        <v-list-subheader class="text-caption font-weight-bold text-uppercase text-text-secondary pb-2">Menu</v-list-subheader>
        
        <v-list-item 
          v-for="item in navItems" 
          :key="item.id" 
          :to="item.to" 
          :data-cy="'nav-item-' + item.id"
          class="rounded-sm mb-1"
          @click="drawer = false"
        >
          <template v-slot:prepend>
            <div class="d-flex align-center justify-center" style="width: 32px">
              <v-icon size="20">{{ item.icon }}</v-icon>
            </div>
          </template>
          <v-list-item-title class="text-sm font-medium ml-2">{{ item.title }}</v-list-item-title>
        </v-list-item>
        
        <template v-if="authStore.isAdmin">
          <v-list-subheader class="text-caption font-weight-bold text-uppercase text-text-secondary mt-4 pb-2">Administração</v-list-subheader>
          <v-list-item 
            v-for="item in adminItems" 
            :key="item.to" 
            :to="item.to" 
            :prepend-icon="item.icon" 
            class="rounded-sm mb-1"
            @click="drawer = false"
          >
            <template v-slot:prepend>
              <div class="d-flex align-center justify-center" style="width: 32px">
                <v-icon size="20">{{ item.icon }}</v-icon>
              </div>
            </template>
            <v-list-item-title class="text-sm font-medium ml-2">{{ item.title }}</v-list-item-title>
          </v-list-item>
        </template>
      </v-list>
    </div>

    <!-- Footer: Fixed -->
    <div class="flex-shrink-0 pa-4 border-t bg-surface">
      <div class="d-flex align-center justify-space-between mb-6 px-2">
        <span class="text-sm font-medium">Tema</span>
        <ThemeToggle />
      </div>
      
      <v-btn 
        block 
        variant="outlined" 
        color="error" 
        prepend-icon="mdi-logout" 
        height="44"
        class="font-bold text-sm"
        @click="handleLogout"
        data-cy="logout-button"
      >
        SAIR DA CONTA
      </v-btn>
      
      <p class="text-center text-[10px] text-text-secondary mt-4 uppercase tracking-widest">
        v1.2.0 • FórumHub
      </p>
    </div>
  </v-navigation-drawer>
</template>

<style scoped>
.gap-2 { gap: 8px; }
.cursor-pointer { cursor: pointer; }
</style>