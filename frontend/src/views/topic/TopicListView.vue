<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTopicStore } from '@/stores/topic'
import { useFavoriteStore } from '@/stores/favoriteStore'
import api from '@/services/api'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import BaseChip from '@/components/base/BaseChip.vue'
import { useDisplay } from 'vuetify'

const { mobile } = useDisplay()
const router = useRouter()
const topicStore = useTopicStore()
const favoriteStore = useFavoriteStore()

const valid = ref(false)
const saving = ref(false)
const courses = ref<any[]>([])
const form = ref({ title: '', message: '', courseId: null as number | null })
const snackbar = ref({ show: false, text: '', color: '' })

const showMessage = (text: string, color: string = 'success') => {
  snackbar.value = { show: true, text, color }
}

const fetchCourses = async () => {
  try {
    const response = await api.get('/courses')
    courses.value = response?.data?.content || response?.data || []
  } catch (error) {
    console.error('Error fetching courses', error)
  }
}

const handleSave = async () => {
  if (!valid.value) return
  saving.value = true
  try {
    await api.post('/topics', form.value)
    topicStore.closeDialog()
    form.value = { title: '', message: '', courseId: null }
    showMessage('Tópico criado com sucesso!')
    topicStore.fetchTopics()
  } catch (error) {
    showMessage('Erro ao criar tópico. Verifique os campos.', 'error')
  } finally {
    saving.value = false
  }
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'OPEN': return 'primary'
    case 'CLOSED': return 'text-secondary'
    case 'RESOLVED': return 'secondary'
    default: return 'primary'
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: '2-digit' })
}

onMounted(() => {
  topicStore.fetchTopics()
  favoriteStore.loadUserFavorites()
  fetchCourses()
})
</script>

<template>
  <v-container class="py-8 py-md-12 px-4 px-md-10" fluid style="max-width: 1560px; margin: 0 auto;">
    <!-- Header -->
    <header class="mb-10 mb-md-14 px-2">
      <h1 class="text-responsive-h1 text-text-primary font-weight-bold mb-2">Explorar Tópicos</h1>
      <p class="text-base text-text-secondary opacity-80">A plataforma oficial de aprendizado e suporte da comunidade</p>
    </header>

    <!-- Filters Section -->
    <div class="mb-12 mb-md-16 px-2">
      <v-row>
        <v-col cols="12" md="6" lg="5">
          <v-text-field
            v-model="topicStore.filters.search"
            placeholder="🔍 Buscar por título..."
            @update:model-value="topicStore.fetchTopics(0)"
            variant="outlined"
            hide-details
            bg-color="surface"
            height="52"
            class="rounded-lg"
          ></v-text-field>
        </v-col>
        <v-col cols="6" md="3" lg="2">
          <v-select
            v-model="topicStore.filters.status"
            :items="['OPEN', 'CLOSED', 'RESOLVED']"
            placeholder="Status"
            clearable
            variant="outlined"
            hide-details
            bg-color="surface"
            @update:model-value="topicStore.fetchTopics(0)"
          ></v-select>
        </v-col>
        <v-col cols="6" md="3" lg="2">
          <v-select
            v-model="topicStore.filters.courseId"
            :items="courses"
            item-title="name"
            item-value="id"
            placeholder="Curso"
            clearable
            variant="outlined"
            hide-details
            bg-color="surface"
            @update:model-value="topicStore.fetchTopics(0)"
          ></v-select>
        </v-col>
        <v-col cols="12" lg="3" class="d-flex gap-2">
          <v-select
            v-model="topicStore.filters.sort"
            :items="[{title:'Recentes', value:'createdAt,desc'}, {title:'Antigos', value:'createdAt,asc'}]"
            placeholder="Ordem"
            variant="outlined"
            hide-details
            bg-color="surface"
            class="flex-grow-1"
          ></v-select>
          <v-btn 
            variant="outlined" 
            color="secondary" 
            icon="mdi-filter-off-outline" 
            height="52"
            width="52"
            @click="topicStore.resetFilters()"
            class="border"
          ></v-btn>
        </v-col>
      </v-row>
    </div>

    <!-- Topics Grid: 3 columns on LG+ -->
    <v-row v-if="topicStore.loading" justify="center">
      <v-col cols="12" class="text-center py-16">
        <v-progress-circular indeterminate color="primary" size="64" width="6"></v-progress-circular>
      </v-col>
    </v-row>

    <v-row v-else class="mb-12">
      <v-col 
        v-for="topic in topicStore.topics" 
        :key="topic.id" 
        cols="12" 
        md="6" 
        lg="4"
        class="pa-3 pa-md-4"
      >
        <v-card 
          variant="outlined"
          class="topic-card pa-6 pa-md-8 bg-surface border-all transition-fast h-100 d-flex flex-column rounded-xl elevation-2"
          @click="router.push('/topics/' + topic.id)"
        >
          <div class="d-flex justify-space-between align-start mb-6">
            <BaseChip color="secondary" variant="tonal" class="text-[10px] uppercase font-bold px-3">
              {{ topic.course }}
            </BaseChip>
            <BaseChip :color="getStatusColor(topic.status)" class="text-[10px] font-bold px-3">
              {{ topic.status }}
            </BaseChip>
          </div>

          <h3 class="text-xl font-weight-bold text-text-primary line-clamp-2 mb-4 leading-tight">
            {{ topic.title }}
          </h3>
          
          <p class="text-base text-text-secondary line-clamp-3 mb-8 leading-relaxed opacity-80">
            {{ topic.message }}
          </p>

          <v-spacer></v-spacer>

          <div class="d-flex align-center justify-space-between mt-auto pt-6 border-t">
            <div class="d-flex align-center">
              <v-avatar size="28" color="primary" class="mr-3 text-white font-bold elevation-2">
                {{ topic.author.charAt(0).toUpperCase() }}
              </v-avatar>
              <div class="d-flex flex-column overflow-hidden">
                <span class="text-xs font-weight-bold text-text-primary text-truncate" style="max-width: 120px;">{{ topic.author }}</span>
                <span class="text-[10px] text-secondary">{{ formatDate(topic.createdAt) }}</span>
              </div>
            </div>
            <FavoriteButton :topicId="topic.id" size="24" @click.stop />
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Pagination -->
    <div class="d-flex justify-center py-12">
      <v-pagination
        v-model="topicStore.pagination.page"
        :length="topicStore.pagination.totalPages"
        :total-visible="mobile ? 3 : 7"
        active-color="primary"
        variant="flat"
        class="elevation-4 rounded-pill bg-surface pa-1"
        @update:model-value="(p) => topicStore.fetchTopics(p - 1)"
      ></v-pagination>
    </div>

    <!-- New Topic Dialog -->
    <v-dialog v-model="topicStore.showDialog" :fullscreen="mobile" :max-width="mobile ? '100%' : '700px'" transition="dialog-bottom-transition" persistent>
      <v-card color="surface" class="border elevation-24 rounded-lg overflow-hidden">
        <v-toolbar flat color="surface" class="border-b px-4" height="72">
          <v-btn icon color="text-primary" @click="topicStore.closeDialog()"><v-icon>mdi-close</v-icon></v-btn>
          <v-toolbar-title class="text-xl font-bold text-text-primary">Novo Tópico</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="flat" :disabled="!valid" :loading="saving" class="px-8 font-weight-bold text-black rounded-pill" height="44" @click="handleSave">POSTAR</v-btn>
        </v-toolbar>
        <div class="pa-8 pa-md-12 overflow-y-auto bg-surface">
          <v-form v-model="valid" @submit.prevent="handleSave">
            <div class="d-flex flex-column gap-8">
              <v-text-field v-model="form.title" label="Título do Tópico" placeholder="O que você deseja compartilhar ou perguntar?" variant="outlined" required :rules="[v => !!v || 'Campo obrigatório']" autofocus />
              <v-select v-model="form.courseId" :items="courses" item-title="name" item-value="id" label="Assunto / Curso" variant="outlined" required :rules="[v => !!v || 'Selecione um curso']" />
              <v-textarea v-model="form.message" label="Descrição" placeholder="Forneça detalhes para que a comunidade possa ajudar..." variant="outlined" rows="10" required :rules="[v => !!v || 'Campo obrigatório']" />
            </div>
          </v-form>
        </div>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">{{ snackbar.text }}</v-snackbar>
  </v-container>
</template>

<style scoped>
.topic-card {
  transition: all 300ms cubic-bezier(0.4, 0, 0.2, 1);
  border-color: rgba(var(--v-border-color), 0.15) !important;
}
.topic-card:hover {
  transform: translateY(-6px);
  border-color: rgba(var(--v-theme-primary), 0.5) !important;
  background-color: rgba(var(--v-theme-primary), 0.04) !important;
  box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.6) !important;
}
.border-t { border-top: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
</style>
