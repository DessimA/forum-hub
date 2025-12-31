import { defineStore } from 'pinia'
import api from '@/services/api'

export const useTopicStore = defineStore('topic', {
  state: () => ({
    topics: [] as any[],
    loading: false,
    showDialog: false,
    filters: {
      status: null as string | null,
      courseId: null as string | null,
      search: '' as string,
      sort: 'createdAt,desc' as string
    },
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
        let url = `/topics?page=${page}&size=${size}&sort=${this.filters.sort}`
        if (this.filters.status) url += `&status=${this.filters.status}`
        if (this.filters.courseId) url += `&courseId=${this.filters.courseId}`
        if (this.filters.search) url += `&title=${encodeURIComponent(this.filters.search)}`
        
        const response = await api.get(url)
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

    async closeTopic(id: string) {
      try {
        await api.patch(`/topics/${id}/close`)
      } catch (error) {
        console.error('Error closing topic', error)
        throw error
      }
    },

    async reopenTopic(id: string) {
      try {
        await api.patch(`/topics/${id}/reopen`)
      } catch (error) {
        console.error('Error reopening topic', error)
        throw error
      }
    },

    setFilters(newFilters: { status?: string | null, courseId?: string | null }) {
      this.filters = { ...this.filters, ...newFilters }
      this.fetchTopics(0) // Reset to page 0 when filtering
    },

    resetFilters() {
      this.filters = { 
        status: null, 
        courseId: null, 
        search: '', 
        sort: 'createdAt,desc' 
      }
      this.fetchTopics(0)
    },

    openDialog() {
      this.showDialog = true
    },

    closeDialog() {
      this.showDialog = false
    }
  }
})