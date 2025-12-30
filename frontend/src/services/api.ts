import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !window.location.pathname.includes('/login')) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const courseService = {
  list: (page = 0) => api.get(`/courses?page=${page}&size=10`),
  create: (data: { name: string; category: string }) => api.post('/courses', data),
  update: (id: string, data: { name: string; category: string }) => api.put(`/courses/${id}`, data),
  remove: (id: string) => api.delete(`/courses/${id}`)
}

export default api