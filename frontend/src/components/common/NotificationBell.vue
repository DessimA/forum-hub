<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notificationStore'

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
      <v-btn icon v-bind="props" class="mr-2">
        <v-badge
          :content="store.unreadCount"
          :model-value="store.unreadCount > 0"
          color="error"
          overlap
        >
          <v-icon>mdi-bell-outline</v-icon>
        </v-badge>
      </v-btn>
    </template>

    <v-card min-width="300" max-width="400" max-height="500" class="overflow-y-auto" color="surface">
      <v-card-title class="d-flex justify-space-between align-center text-subtitle-1 pa-4 text-white">
        <span>Notificações</span>
        <v-btn 
          v-if="store.unreadCount > 0" 
          variant="text" 
          size="small" 
          color="primary"
          @click="store.markAllAsRead"
        >
          Marcar todas
        </v-btn>
      </v-card-title>
      <v-divider></v-divider>
      
      <v-list v-if="store.notifications.length > 0" lines="two" bg-color="surface">
        <v-list-item
          v-for="item in store.notifications"
          :key="item.id"
          :value="item"
          @click="handleNotificationClick(item)"
          :class="{'bg-grey-darken-3': !item.isRead}"
        >
          <template v-slot:prepend>
            <v-icon :color="item.isRead ? 'grey' : 'primary'">
              {{ item.notificationType === 'NEW_ANSWER' ? 'mdi-message-reply-text' : 
                 item.notificationType === 'STATUS_CHANGED' ? 'mdi-list-status' : 'mdi-delete' }}
            </v-icon>
          </template>
          <v-list-item-title :class="{'font-weight-bold text-white': !item.isRead, 'text-grey-lighten-1': item.isRead}">
            {{ item.content }}
          </v-list-item-title>
          <v-list-item-subtitle class="text-caption text-grey">
            {{ new Date(item.createdAt).toLocaleString() }}
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>
      
      <div v-else class="pa-4 text-center text-grey">
        Nenhuma notificação recente.
      </div>
    </v-card>
  </v-menu>
</template>
