import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { public: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/user/ProfileView.vue')
    },
    {
      path: '/admin/users',
      name: 'user-management',
      component: () => import('../views/user/UserManagementView.vue'),
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/courses',
      name: 'course-management',
      component: () => import('../views/admin/CourseManagementView.vue'),
      meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] }
    },
    {
      path: '/admin/logs',
      name: 'admin-logs',
      component: () => import('../views/admin/AdminLogsView.vue'),
      meta: { requiresAdmin: true }
    },
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/topic/TopicListView.vue')
    },
    {
      path: '/topics/:id',
      name: 'TopicDetail',
      component: () => import('@/views/topic/TopicDetailView.vue'),
      props: true
    }
  ]
})

router.beforeEach((to, _, next) => {
  const authStore = useAuthStore()
  
  if (!to.meta.public && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.name === 'Login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
