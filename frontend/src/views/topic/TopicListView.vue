<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTopicStore } from '@/stores/topic'
import api from '@/services/api'

const topicStore = useTopicStore()

const valid = ref(false)
const saving = ref(false)
const courses = ref<any[]>([])
const form = ref({
  title: '',
  message: '',
  courseId: null as number | null
})

const fetchCourses = async () => {
  try {
    const response = await api.get('/courses')
    courses.value = response?.data?.content || response?.data || []
  } catch (error) {
    console.error('Error fetching courses', error)
    courses.value = []
  }
}

const handleSave = async () => {
  if (!valid.value) return
  saving.value = true
  try {
    await api.post('/topics', form.value)
    topicStore.closeDialog()
    form.value = { title: '', message: '', courseId: null }
    topicStore.fetchTopics()
  } catch (error) {
    alert('Erro ao criar tópico. Verifique se o título e mensagem já não existem.')
  } finally {
    saving.value = false
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

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

onMounted(() => {
  topicStore.fetchTopics()
  fetchCourses()
})
</script>

<template>
  <v-container class="py-10">
    <v-row>
      <v-col cols="12" class="d-flex align-center justify-space-between mb-6">
        <h2 class="text-h4 font-weight-bold text-white">Explorar Tópicos</h2>
        <v-chip color="primary" variant="outlined" label>
          {{ topicStore.pagination.totalElements }} Tópicos Encontrados
        </v-chip>
      </v-col>

      <v-col v-if="topicStore.loading" cols="12" class="text-center py-10">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
      </v-col>

      <v-col v-else v-for="topic in topicStore.topics" :key="topic.id" cols="12">
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
            <v-chip :color="getStatusColor(topic.status)" variant="tonal" size="small">
              {{ topic.status === 'RESOLVED' ? 'RESOLVIDO' : 'ABERTO' }}
            </v-chip>
          </div>
          
          <v-card-text class="px-0 pb-0 pt-4 text-truncate text-grey-lighten-2">
            {{ topic.message }}
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" class="d-flex justify-center mt-6">
        <v-pagination
          v-if="topicStore.pagination.totalPages > 1"
          v-model="topicStore.pagination.page"
          :length="topicStore.pagination.totalPages"
          @update:model-value="(p) => topicStore.fetchTopics(p - 1)"
          color="primary"
        ></v-pagination>
      </v-col>
    </v-row>

    <!-- Dialog de Novo Tópico -->
    <v-dialog v-model="topicStore.showDialog" max-width="600px" persistent>
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-h5 text-white">Novo Tópico</v-card-title>
        <v-card-text>
          <v-form v-model="valid">
            <v-text-field v-model="form.title" label="Título" required variant="outlined" color="primary"></v-text-field>
            <v-select v-model="form.courseId" :items="courses" item-title="name" item-value="id" label="Selecione o Curso" required variant="outlined" color="primary"></v-select>
            <v-textarea v-model="form.message" label="Mensagem" required variant="outlined" color="primary" rows="5"></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="topicStore.closeDialog()">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" :loading="saving" :disabled="!valid" @click="handleSave">Postar Tópico</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.1) !important; 
}
</style>