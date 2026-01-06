<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notificationStore'
import { useDisplay } from 'vuetify'

const { mobile } = useDisplay()
const store = useNotificationStore()
const router = useRouter()
const menu = ref(false)

const handleNotificationClick = async (notification: any) => {
  if (!notification.isRead) {
    await store.markAsRead(notification.id)
  }
  menu.value = false
  if (notification.topicId) {
    router.push(`/topics/${notification.topicId}`)
  }
}

onMounted(() => {
  store.startPolling()
  store.fetchNotifications()
})

onUnmounted(() => {
  store.stopPolling()
})
</script>

<template>
  <v-menu v-model="menu" :close-on-content-click="false" location="bottom end">
    <template v-slot:activator="{ props }">
      <v-btn icon variant="text" v-bind="props" data-cy="notification-bell">
        <v-badge
          :content="store.unreadCount"
          :model-value="store.unreadCount > 0"
          color="error"
          overlap
        >
          <v-icon size="24">mdi-bell-outline</v-icon>
        </v-badge>
      </v-btn>
    </template>

    <v-card 
      :width="mobile ? '100vw' : '360'" 
      :max-height="mobile ? '100vh' : '500'" 
      class="overflow-y-auto border rounded-sm" 
      color="surface"
    >
      <v-card-title class="d-flex justify-space-between align-center text-base pa-4 text-text-primary border-b">
        <div class="d-flex align-center">
          <v-btn v-if="mobile" icon="mdi-close" variant="text" size="small" class="mr-2" @click="menu = false"></v-btn>
          <span>Notificações</span>
        </div>
        <v-btn 
          v-if="store.unreadCount > 0" 
          variant="text" 
          size="small" 
          color="primary"
          class="text-xs font-bold"
          @click="store.markAllAsRead"
        >
          Limpar todas
        </v-btn>
      </v-card-title>
      
      <v-list v-if="store.notifications.length > 0" lines="two" bg-color="transparent" class="pa-0">
        <v-list-item
          v-for="item in store.notifications"
          :key="item.id"
          :value="item"
          @click="handleNotificationClick(item)"
          class="border-b px-4 py-3"
          :class="{'bg-primary-opacity-5': !item.isRead}"
        >
          <template v-slot:prepend>
            <v-avatar size="32" :color="item.isRead ? 'background' : 'primary'" variant="tonal" class="mr-3">
              <v-icon size="18" :color="item.isRead ? 'text-secondary' : 'primary'">
                {{ item.notificationType === 'NEW_ANSWER' ? 'mdi-message-reply-text' : 
                   item.notificationType === 'STATUS_CHANGED' ? 'mdi-list-status' : 'mdi-delete' }}
              </v-icon>
            </v-avatar>
          </template>
          <v-list-item-title :class="{'font-bold text-text-primary': !item.isRead, 'text-text-secondary': item.isRead}" class="text-sm">
            {{ item.content }}
          </v-list-item-title>
          <v-list-item-subtitle class="text-xs text-text-secondary mt-1">
            {{ new Date(item.createdAt).toLocaleString() }}
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>
      
      <div v-else class="pa-12 text-center text-text-secondary">
        <v-icon size="48" class="mb-4 opacity-20">mdi-bell-off-outline</v-icon>
        <p class="text-sm">Nenhuma notificação recente.</p>
      </div>
    </v-card>
  </v-menu>
</template>

<style scoped>
.bg-primary-opacity-5 {
  background-color: rgba(var(--v-theme-primary), 0.05) !important;
}
.border-b {
  border-bottom: 1px solid rgba(var(--v-border-color), 0.1) !important;
}
</style>