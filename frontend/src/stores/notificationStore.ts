import { defineStore } from 'pinia'
import api from '@/services/api'
import { useFavoriteStore } from './favoriteStore'
import { useTopicStore } from './topic'

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [] as any[],
    unreadCount: 0,
    loading: false,
    pollingInterval: null as ReturnType<typeof setInterval> | null
  }),

  actions: {
    async fetchNotifications() {
      this.loading = true
      try {
        const response = await api.get('/notifications')
        this.notifications = response.data
      } catch (error) {
        console.error('Error loading notifications', error)
      } finally {
        this.loading = false
      }
    },

    async fetchUnreadCount() {
      try {
        const response = await api.get('/notifications/unread-count')
        const newCount = response.data.count
        
        if (newCount > this.unreadCount) {
          await this.fetchNotifications()
          this.checkSystemEvents()
        }
        
        this.unreadCount = newCount
      } catch (error) {
        console.error('Error loading unread count', error)
      }
    },

    checkSystemEvents() {
      const hasDeletedTopic = this.notifications.some(n => 
        n.notificationType === 'TOPIC_DELETED' && !n.isRead
      )
      
      if (hasDeletedTopic) {
        const favoriteStore = useFavoriteStore()
        const topicStore = useTopicStore()
        favoriteStore.loadUserFavorites()
        topicStore.fetchTopics()
      }
    },

    async markAsRead(id: string) {
      try {
        await api.patch(`/notifications/${id}/read`)
        this.notifications = this.notifications.map(n => 
          n.id === id ? { ...n, isRead: true } : n
        )
        // Optimistically update count
        if (this.unreadCount > 0) this.unreadCount--
      } catch (error) {
        console.error('Error marking notification as read', error)
        throw error
      }
    },

    async markAllAsRead() {
      try {
        await api.patch('/notifications/read-all')
        this.notifications = this.notifications.map(n => ({ ...n, isRead: true }))
        this.unreadCount = 0
      } catch (error) {
        console.error('Error marking all notifications as read', error)
        throw error
      }
    },

    startPolling() {
      this.fetchUnreadCount()
      if (!this.pollingInterval) {
        this.pollingInterval = setInterval(() => {
          this.fetchUnreadCount()
        }, 30000)
      }
    },

    stopPolling() {
      if (this.pollingInterval) {
        clearInterval(this.pollingInterval)
        this.pollingInterval = null
      }
    }
  }
})
