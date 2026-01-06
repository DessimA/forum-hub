import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Landing',
      component: () => import('@/views/LandingPage.vue'),
      meta: { public: true }
    },
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
      path: '/favorites',
      name: 'Favorites',
      component: () => import('@/views/topic/FavoritesView.vue')
    },
    {
      path: '/topics',
      name: 'Topics',
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
  
  // Public routes: Landing, Login, Register
  const isPublicRoute = to.meta.public === true

  if (authStore.isAuthenticated) {
    // Authenticated user trying to access Landing, Login or Register
    if (isPublicRoute) {
      next('/topics')
    } else {
      next()
    }
  } else {
    // Unauthenticated user trying to access private route
    if (!isPublicRoute) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router