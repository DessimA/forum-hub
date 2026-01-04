import { defineStore } from 'pinia'
import api from '@/services/api'

export const useFavoriteStore = defineStore('favorite', {
  state: () => ({
    favoriteTopicIds: new Set<string>(),
    loading: false
  }),

  actions: {
    async loadUserFavorites() {
      this.loading = true
      try {
        const response = await api.get('/favorites?size=1000')
        // Handle Page response
        const content = response.data.content || response.data
        this.favoriteTopicIds = new Set(content.map((t: any) => t.id))
      } catch (error) {
        console.error('Error loading favorites', error)
      } finally {
        this.loading = false
      }
    },

    async addFavorite(topicId: string) {
      try {
        await api.post(`/favorites/topics/${topicId}`)
        this.favoriteTopicIds.add(topicId)
      } catch (error) {
        console.error('Error adding favorite', error)
        throw error
      }
    },

    async removeFavorite(topicId: string) {
      try {
        await api.delete(`/favorites/topics/${topicId}`)
        this.favoriteTopicIds.delete(topicId)
      } catch (error) {
        console.error('Error removing favorite', error)
        throw error
      }
    },

    isFavorited(topicId: string): boolean {
      return this.favoriteTopicIds.has(topicId)
    }
  }
})
