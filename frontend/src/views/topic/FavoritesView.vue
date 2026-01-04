<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import { useFavoriteStore } from '@/stores/favoriteStore'

const favoriteStore = useFavoriteStore()
const topics = ref<any[]>([])
const loading = ref(false)
const pagination = ref({
  page: 1,
  totalPages: 0,
  totalElements: 0
})

const fetchFavorites = async (page = 0) => {
  loading.value = true
  try {
    const response = await api.get(`/favorites?page=${page}&size=10&sort=createdAt,desc`)
    topics.value = response.data.content
    pagination.value = {
      page: response.data.number + 1,
      totalPages: response.data.totalPages,
      totalElements: response.data.totalElements
    }
  } catch (error) {
    console.error('Error loading favorites', error)
  } finally {
    loading.value = false
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'OPEN': return 'primary'
    case 'CLOSED': return 'grey'
    case 'RESOLVED': return 'success'
    default: return 'primary'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'OPEN': return 'ABERTO'
    case 'CLOSED': return 'FECHADO'
    case 'RESOLVED': return 'RESOLVIDO'
    default: return status
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

onMounted(() => {
  fetchFavorites()
  favoriteStore.loadUserFavorites()
})
</script>

<template>
  <v-container class="py-10">
    <v-row>
      <v-col cols="12" class="d-flex align-center justify-space-between mb-6">
        <h2 class="text-h4 font-weight-bold text-white">Meus Favoritos</h2>
        <v-chip color="yellow-darken-2" variant="outlined" label icon="mdi-star">
          {{ pagination.totalElements }} Favoritos
        </v-chip>
      </v-col>

      <v-col v-if="loading" cols="12" class="text-center py-10">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      </v-col>

      <v-col v-else-if="topics.length === 0" cols="12" class="text-center py-10">
        <v-icon size="64" color="grey" class="mb-4">mdi-star-off</v-icon>
        <h3 class="text-h5 text-grey">Você ainda não tem favoritos.</h3>
        <v-btn to="/" color="primary" variant="text" class="mt-2">Explorar Tópicos</v-btn>
      </v-col>

      <v-col v-else v-for="topic in topics" :key="topic.id" cols="12">
        <v-card 
          class="topico-card mb-4 pa-4" 
          color="surface" 
          hover 
          variant="flat"
          :to="'/topics/' + topic.id"
        >
          <div class="d-flex justify-space-between align-start">
            <div>
              <h3 class="text-h5 font-weight-bold mb-2 text-white">{{ topic.title }}</h3>
              <div class="d-flex align-center gap-4 text-body-2 text-grey-lighten-1">
                <span class="d-flex align-center">
                  <v-icon size="small" class="mr-1">mdi-account-circle-outline</v-icon>
                  {{ topic.author }}
                </span>
                <span class="d-flex align-center ml-4">
                  <v-icon size="small" class="mr-1">mdi-calendar-range</v-icon>
                  {{ formatDate(topic.createdAt) }}
                </span>
                <v-chip size="x-small" color="secondary" variant="tonal" class="ml-4">
                  {{ topic.course }}
                </v-chip>
              </div>
            </div>
            <div class="d-flex align-center">
                <v-chip :color="getStatusColor(topic.status)" variant="tonal" size="small" class="font-weight-bold status-chip mr-2">
                  {{ getStatusText(topic.status) }}
                </v-chip>
                <FavoriteButton :topicId="topic.id" />
            </div>
          </div>
          
          <v-card-text class="px-0 pb-0 pt-4 text-truncate text-grey-lighten-2">
            {{ topic.message }}
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" class="d-flex justify-center mt-6">
        <v-pagination
          v-if="pagination.totalPages > 1"
          v-model="pagination.page"
          :length="pagination.totalPages"
          @update:model-value="(p) => fetchFavorites(p - 1)"
          color="primary"
        ></v-pagination>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.topico-card { 
  transition: all 0.2s ease; 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
.topico-card:hover { 
  border: 1px solid rgba(0, 240, 255, 0.2) !important; 
  background-color: rgba(255, 255, 255, 0.02) !important;
}
</style>