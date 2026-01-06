<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import { useFavoriteStore } from '@/stores/favoriteStore'
import BaseChip from '@/components/base/BaseChip.vue'

const router = useRouter()
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
    const response = await api.get(`/favorites?page=${page}&size=12&sort=createdAt,desc`)
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
    case 'CLOSED': return 'warning'
    case 'RESOLVED': return 'success'
    default: return 'primary'
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: '2-digit' })
}

onMounted(() => {
  fetchFavorites()
  favoriteStore.loadUserFavorites()
})
</script>

<template>
  <v-container class="py-8 py-md-12 px-4 px-md-12 max-w-[1400px] mx-auto">
    <!-- Header -->
    <header class="mb-10 mb-md-16 px-1">
      <div class="d-flex align-center justify-space-between">
        <div>
          <h1 class="text-responsive-h1 text-text-primary font-weight-bold mb-2">Meus Favoritos</h1>
          <p class="text-text-secondary text-sm">Tópicos que você marcou para acompanhar</p>
        </div>
        <div class="text-right hidden-sm-and-down">
          <div class="text-h3 font-weight-bold text-primary leading-none mb-1">{{ pagination.totalElements }}</div>
          <div class="text-[10px] uppercase font-bold text-secondary tracking-widest opacity-70">Tópicos Salvos</div>
        </div>
      </div>
    </header>

    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
    </div>

    <div v-else-if="topics.length === 0" class="text-center py-20 bg-surface border border-dashed rounded-lg opacity-60">
      <v-icon size="80" color="secondary" class="mb-6 opacity-30">mdi-star-off-outline</v-icon>
      <h3 class="text-xl font-weight-bold text-text-primary mb-3">Nenhum favorito ainda</h3>
      <p class="text-base text-text-secondary mb-10">Explore os tópicos e clique na estrela para salvar aqui.</p>
      <v-btn to="/topics" color="primary" variant="flat" height="48" class="font-weight-bold px-10 text-black rounded-pill">
        EXPLORAR COMUNIDADE
      </v-btn>
    </div>

    <!-- Grid -->
    <v-row v-else class="ga-4 ga-md-6">
      <v-col v-for="topic in topics" :key="topic.id" cols="12" sm="6" lg="4" class="pa-0">
        <v-card 
          variant="outlined"
          class="topic-card pa-6 pa-md-8 bg-surface border-all transition-fast h-100 d-flex flex-column rounded-lg elevation-1" 
          @click="router.push('/topics/' + topic.id)"
        >
          <div class="d-flex justify-space-between align-start mb-5">
            <BaseChip color="secondary" variant="tonal" class="text-[10px] uppercase font-bold px-2">
              {{ topic.course }}
            </BaseChip>
            <BaseChip :color="getStatusColor(topic.status)" class="text-[10px] font-bold px-2">
              {{ topic.status }}
            </BaseChip>
          </div>
          
          <h3 class="text-lg font-bold mb-4 text-text-primary line-clamp-2 leading-tight">{{ topic.title }}</h3>
          
          <v-spacer></v-spacer>

          <div class="d-flex align-center justify-space-between mt-auto pt-5 border-t">
            <div class="d-flex align-center text-[11px] text-text-secondary">
              <v-avatar size="20" color="primary" class="mr-2 text-white font-bold text-[8px]">
                {{ topic.author.charAt(0).toUpperCase() }}
              </v-avatar>
              <span class="text-truncate font-weight-medium" style="max-width: 100px;">{{ topic.author }}</span>
              <span class="mx-2 opacity-30">•</span>
              <span>{{ formatDate(topic.createdAt) }}</span>
            </div>
            <FavoriteButton :topicId="topic.id" density="compact" size="20" @click.stop />
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Pagination -->
    <div class="d-flex justify-center mt-12 pb-16">
      <v-pagination
        v-if="pagination.totalPages > 1"
        v-model="pagination.page"
        :length="pagination.totalPages"
        :total-visible="5"
        density="comfortable"
        active-color="primary"
        @update:model-value="(p) => fetchFavorites(p - 1)"
      ></v-pagination>
    </div>
  </v-container>
</template>

<style scoped>
.topic-card {
  transition: all 250ms cubic-bezier(0.4, 0, 0.2, 1);
  border-color: rgba(var(--v-border-color), 0.1) !important;
}
.topic-card:hover {
  transform: translateY(-4px);
  border-color: rgba(var(--v-theme-primary), 0.4) !important;
  background-color: rgba(var(--v-theme-primary), 0.03) !important;
  box-shadow: 0 12px 24px -8px rgba(0, 0, 0, 0.5) !important;
}
.border-t {
  border-top: 1px solid rgba(var(--v-border-color), 0.1) !important;
}
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.ga-4 { gap: 16px; }
.ga-6 { gap: 24px; }
</style>
