import { defineStore } from 'pinia'
import { courseService } from '@/services/api'

interface Course {
  id: string
  name: string
  category: string
}

export const useCourseStore = defineStore('course', {
  state: () => ({
    courses: [] as Course[],
    loading: false,
    totalPages: 0,
    currentPage: 0
  }),

  actions: {
    async fetchCourses(page = 0) {
      this.loading = true
      try {
        const { data } = await courseService.list(page)
        this.courses = data.content
        this.totalPages = data.totalPages
        this.currentPage = data.number
      } finally {
        this.loading = false
      }
    },

    async createCourse(payload: { name: string; category: string }) {
      await courseService.create(payload)
      await this.fetchCourses(0)
    },

    async updateCourse(id: string, payload: { name: string; category: string }) {
      await courseService.update(id, payload)
      await this.fetchCourses(this.currentPage)
    },

    async deleteCourse(id: string) {
      await courseService.remove(id)
      await this.fetchCourses(this.currentPage)
    }
  }
})
