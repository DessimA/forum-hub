import { defineStore } from 'pinia'
import api from '@/services/api'

export const useTopicStore = defineStore('topic', {
  state: () => ({
    topics: [] as any[],
    loading: false,
    showDialog: false,
    pagination: {
      page: 0,
      totalPages: 0,
      totalElements: 0
    }
  }),

  actions: {
    async fetchTopics(page = 0, size = 10) {
      this.loading = true
      try {
        const response = await api.get(`/topics?page=${page}&size=${size}`)
        this.topics = response.data.content
        this.pagination = {
          page: response.data.number,
          totalPages: response.data.totalPages,
          totalElements: response.data.totalElements
        }
      } catch (error) {
        console.error('Error loading topics', error)
      } finally {
        this.loading = false
      }
    },

    openDialog() {
      this.showDialog = true
    },

    closeDialog() {
      this.showDialog = false
    }
  }
})