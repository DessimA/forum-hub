<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useFavoriteStore } from '@/stores/favoriteStore'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import BaseChip from '@/components/base/BaseChip.vue'
import { useDisplay } from 'vuetify'

const { mobile, name: displayBreakpoint } = useDisplay()
const authStore = useAuthStore()
const favoriteStore = useFavoriteStore()
const route = useRoute()
const router = useRouter()

const topic = ref<any>(null)
const newAnswer = ref('')
const loading = ref(true)
const sending = ref(false)
const snackbar = ref({ show: false, text: '', color: '' })

// UI Controllers
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const showPunishDialog = ref(false)
const selectedUser = ref<any>(null)
const courses = ref<any[]>([])

const editForm = ref({ title: '', message: '', courseId: null as number | null })
const punishForm = ref({ mode: 'temporary' as 'temporary' | 'permanent', days: 1, justification: '' })

const isAuthorOrAdmin = computed(() => {
  if (!topic.value) return false
  return authStore.isAdmin || topic.value.authorEmail === authStore.userEmail
})

// Only use Kebab menu on very small mobile screens
const useKebabMenu = computed(() => displayBreakpoint.value === 'xs')

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

const fetchTopic = async () => {
  try {
    const response = await api.get(`/topics/${route.params.id}`)
    topic.value = response.data
    editForm.value = { title: topic.value.title, message: topic.value.message, courseId: topic.value.courseId }
  } catch (error) {
    router.push('/topics')
  } finally {
    loading.value = false
  }
}

const openEdit = async () => {
  await fetchCourses()
  showEditDialog.value = true
}

const handleUpdate = async () => {
  try {
    await api.put(`/topics/${topic.value.id}`, editForm.value)
    showEditDialog.value = false
    showMessage('Tópico atualizado!')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao atualizar tópico', 'error')
  }
}

const sendAnswer = async () => {
  if (!newAnswer.value.trim()) return
  sending.value = true
  try {
    await api.post(`/topics/${route.params.id}/answers`, { message: newAnswer.value })
    newAnswer.value = ''
    showMessage('Resposta enviada!')
    await fetchTopic()
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })
  } catch (error) {
    showMessage('Erro ao responder', 'error')
  } finally {
    sending.value = false
  }
}

const toggleSolution = async (answerId: number) => {
  await api.patch(`/answers/${answerId}/solution`)
  await fetchTopic()
}

const deleteTopic = async () => {
  try {
    await api.delete(`/topics/${topic.value.id}`)
    router.push('/topics')
  } catch (error) {
    showMessage('Erro ao excluir tópico', 'error')
  }
}

const openPunish = (userId: string | number, name: string, context: { topicId?: string, answerId?: string } = {}) => {
  selectedUser.value = { id: String(userId), name, ...context }
  showPunishDialog.value = true
}

const confirmPunish = async () => {
  const user = selectedUser.value
  if (!user || !punishForm.value.justification) return
  await api.post(`/users/${user.id}/block`, { 
    permanent: punishForm.value.mode === 'permanent', 
    days: punishForm.value.mode === 'permanent' ? 0 : Number(punishForm.value.days), 
    justification: punishForm.value.justification,
    topicId: user.topicId || null,
    answerId: user.answerId || null
  })
  showPunishDialog.value = false
  if (user.topicId) router.push('/topics')
  else await fetchTopic()
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'OPEN': return 'primary'
    case 'CLOSED': return 'warning'
    case 'RESOLVED': return 'success'
    default: return 'primary'
  }
}

onMounted(() => {
  fetchTopic()
  favoriteStore.loadUserFavorites()
})
</script>

<template>
  <v-container class="container-adaptive py-8 px-4 px-md-12 max-w-[1200px] mx-auto">
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>

    <div v-else-if="topic" class="d-flex flex-column">
      <!-- Header Section -->
      <v-card variant="outlined" class="pa-adaptive-card bg-surface border-all elevation-1 mb-6 rounded-lg">
        <div class="d-flex align-center mb-8">
          <v-btn variant="text" size="small" prepend-icon="mdi-arrow-left" @click="router.push('/topics')" class="text-none px-0 text-secondary font-weight-bold btn-back-text">
            <span class="adaptive-label">VOLTAR</span>
          </v-btn>
          
          <v-spacer></v-spacer>
          
          <!-- ADAPTIVE ACTIONS BLOCK -->
          <div class="d-flex align-center gap-2">
            <template v-if="isAuthorOrAdmin || authStore.isModerator">
              <!-- Desktop & Tablet: Dynamic Buttons -->
              <div v-if="!useKebabMenu" class="d-flex gap-2 mr-2">
                <template v-if="isAuthorOrAdmin">
                  <v-btn variant="outlined" color="primary" size="small" @click="openEdit" class="border action-btn">
                    <v-icon :left="!mobile">mdi-pencil</v-icon>
                    <span class="adaptive-label ml-1">EDITAR</span>
                  </v-btn>
                  
                  <v-btn variant="outlined" color="error" size="small" @click="showDeleteDialog = true" class="border action-btn">
                    <v-icon :left="!mobile">mdi-trash-can</v-icon>
                    <span class="adaptive-label ml-1">EXCLUIR</span>
                  </v-btn>
                </template>

                <v-btn 
                  v-if="authStore.isModerator && topic.authorEmail !== authStore.userEmail" 
                  variant="tonal" color="error" size="small" @click="openPunish(topic.authorId, topic.author, { topicId: topic.id })"
                  class="action-btn"
                >
                  <v-icon :left="!mobile">mdi-account-cancel</v-icon>
                  <span class="adaptive-label ml-1">BANIR</span>
                </v-btn>
              </div>

              <!-- Extra Small Mobile: Kebab Menu -->
              <v-menu v-else location="bottom end">
                <template v-slot:activator="{ props }">
                  <v-btn icon="mdi-dots-vertical" variant="text" color="secondary" v-bind="props" class="mr-1"></v-btn>
                </template>
                <v-list density="compact" class="pa-2 border rounded-lg elevation-12">
                  <template v-if="isAuthorOrAdmin">
                    <v-list-item prepend-icon="mdi-pencil" title="Editar" @click="openEdit" class="rounded-sm mb-1" />
                    <v-list-item prepend-icon="mdi-trash-can" title="Excluir" color="error" @click="showDeleteDialog = true" class="rounded-sm" />
                  </template>
                  <v-list-item v-if="authStore.isModerator && topic.authorEmail !== authStore.userEmail" prepend-icon="mdi-account-cancel" title="Banir Autor" color="error" @click="openPunish(topic.authorId, topic.author, { topicId: topic.id })" class="rounded-sm" />
                </v-list>
              </v-menu>
            </template>

            <v-divider vertical class="mx-2 hidden-xs-only" v-if="isAuthorOrAdmin || authStore.isModerator" />
            <FavoriteButton :topicId="topic.id" size="28" />
          </div>
        </div>

        <h1 class="text-adaptive-h1 font-weight-bold text-text-primary mb-6 leading-tight">
          {{ topic.title }}
        </h1>

        <div class="d-flex flex-wrap align-center gap-2 gap-md-3 mb-8">
          <BaseChip :color="getStatusColor(topic.status)" variant="flat" class="text-white px-3 font-weight-bold text-adaptive-chip">
            {{ topic.status }}
          </BaseChip>
          <BaseChip color="surface" variant="outlined" class="text-secondary border px-3 text-adaptive-chip">
            <v-icon size="14" class="mr-1">mdi-book-education-outline</v-icon>
            {{ topic.course }}
          </BaseChip>
          <BaseChip color="surface" variant="outlined" class="text-secondary border px-3 text-adaptive-chip">
            <v-icon size="14" class="mr-1">mdi-comment-text-multiple-outline</v-icon>
            {{ topic.answers.length }}
          </BaseChip>
        </div>

        <div class="d-flex align-center text-sm text-secondary pt-6 border-t">
          <v-avatar size="44" color="primary" class="mr-4 text-white font-bold elevation-2">
            {{ topic.author.charAt(0).toUpperCase() }}
          </v-avatar>
          <div class="d-flex flex-column">
            <span class="font-weight-bold text-text-primary text-base">{{ topic.author }}</span>
            <span class="text-xs opacity-70">{{ new Date(topic.createdAt).toLocaleString('pt-BR', { dateStyle: 'medium', timeStyle: 'short' }) }}</span>
          </div>
        </div>
      </v-card>

      <!-- Message Card -->
      <v-card variant="outlined" class="pa-adaptive-message bg-surface border-all mb-12 rounded-lg elevation-1">
        <div class="message-content text-text-primary leading-relaxed white-space-pre text-adaptive-body">
          {{ topic.message }}
        </div>
      </v-card>

      <!-- Answers Section -->
      <section class="mt-4">
        <h2 class="text-adaptive-h2 font-weight-bold text-text-primary mb-8">Respostas</h2>

        <div v-if="topic.answers.length > 0" class="d-flex flex-column gap-6">
          <v-card 
            v-for="resp in topic.answers" 
            :key="resp.id" 
            variant="outlined" 
            class="pa-adaptive-card bg-surface border-all transition-fast rounded-lg" 
            :class="{'solution-card': resp.solution}"
          >
            <div class="d-flex align-center mb-6">
              <v-avatar size="40" color="primary" variant="tonal" class="mr-4">{{ resp.author.charAt(0).toUpperCase() }}</v-avatar>
              <div class="d-flex flex-column">
                <div class="d-flex align-center gap-2">
                  <span class="font-weight-bold text-text-primary text-base">{{ resp.author }}</span>
                  <BaseChip v-if="resp.solution" color="success" size="x-small" class="font-weight-bold">SOLUÇÃO</BaseChip>
                </div>
                <span class="text-xs text-secondary">{{ new Date(resp.createdAt).toLocaleDateString() }}</span>
              </div>
              <v-spacer></v-spacer>
              <div class="d-flex gap-1">
                <v-btn v-if="isAuthorOrAdmin" icon="mdi-check-decagram" :color="resp.solution ? 'success' : 'secondary'" variant="text" size="small" @click="toggleSolution(resp.id)"></v-btn>
                <v-btn v-if="authStore.isAdmin || authStore.isModerator" icon="mdi-account-cancel-outline" color="error" variant="text" size="small" @click="openPunish(resp.authorId, resp.author, { answerId: resp.id })"></v-btn>
              </div>
            </div>
            <div class="text-adaptive-body text-text-primary leading-relaxed pl-md-14">{{ resp.message }}</div>
          </v-card>
        </div>

        <div v-else class="text-center py-16 px-4 border border-dashed rounded-lg bg-surface opacity-60">
          <v-icon size="64" color="secondary" class="mb-4">mdi-message-off-outline</v-icon>
          <p class="text-lg text-secondary">Nenhuma resposta ainda.</p>
        </div>

        <!-- Reply Form -->
        <v-card v-if="topic.status === 'OPEN'" variant="outlined" class="pa-adaptive-card bg-surface border-all mt-12 elevation-6 rounded-lg">
          <h3 class="text-adaptive-h3 font-weight-bold text-text-primary mb-8"><v-icon color="primary" class="mr-3">mdi-reply</v-icon> Sua Resposta</h3>
          <v-textarea v-model="newAnswer" placeholder="Sua contribuição..." variant="outlined" rows="6" bg-color="rgba(var(--v-theme-text-primary), 0.02)" class="mb-8" hide-details />
          <div class="d-flex justify-end">
            <v-btn color="primary" height="52" class="px-10 font-weight-bold text-black rounded-pill" :loading="sending" @click="sendAnswer" :block="mobile">POSTAR RESPOSTA</v-btn>
          </div>
        </v-card>
      </section>
    </div>

    <!-- Modais -->
    <v-dialog v-model="showEditDialog" max-width="600"><v-card class="pa-8 bg-surface border-all elevation-24 rounded-lg"><h2 class="text-h5 font-weight-bold mb-8 text-text-primary">Editar Tópico</h2><v-text-field v-model="editForm.title" label="Título" variant="outlined" class="mb-6" /><v-select v-model="editForm.courseId" :items="courses" item-title="name" item-value="id" label="Curso" variant="outlined" class="mb-6" /><v-textarea v-model="editForm.message" label="Mensagem" variant="outlined" rows="8" /><div class="d-flex justify-end gap-4 mt-8"><v-btn variant="text" @click="showEditDialog = false" class="font-weight-bold">CANCELAR</v-btn><v-btn color="primary" @click="handleUpdate" class="font-weight-bold px-10 text-black">SALVAR</v-btn></div></v-card></v-dialog>
    <v-dialog v-model="showDeleteDialog" max-width="450"><v-card class="pa-8 border-all bg-surface elevation-24 rounded-lg"><h2 class="text-h5 font-bold text-text-primary mb-4 text-center">Excluir tópico?</h2><p class="text-base text-secondary mb-10 text-center">Esta ação é irreversível.</p><div class="d-flex justify-center gap-4"><v-btn variant="text" @click="showDeleteDialog = false" class="font-weight-bold">CANCELAR</v-btn><v-btn color="error" @click="deleteTopic" class="font-weight-bold px-8">CONFIRMAR</v-btn></div></v-card></v-dialog>
    <v-dialog v-model="showPunishDialog" max-width="450"><v-card class="pa-8 bg-surface border-all elevation-24 rounded-lg"><h2 class="text-h5 font-weight-bold mb-8 text-text-primary">Aplicar Punição</h2><v-textarea v-model="punishForm.justification" label="Justificativa" placeholder="Motivo..." variant="outlined" rows="4" class="mb-2" /><div class="d-flex justify-end gap-4 mt-6"><v-btn variant="text" @click="showPunishDialog = false" class="font-weight-bold">CANCELAR</v-btn><v-btn color="error" @click="confirmPunish" class="font-weight-bold px-8">CONFIRMAR</v-btn></div></v-card></v-dialog>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">{{ snackbar.text }}</v-snackbar>
  </v-container>
</template>

<style scoped>
/* FLUID TYPOGRAPHY */
.text-adaptive-h1 { font-size: clamp(1.5rem, 4vw, 2.5rem) !important; }
.text-adaptive-h2 { font-size: clamp(1.25rem, 3vw, 1.75rem) !important; }
.text-adaptive-h3 { font-size: clamp(1.1rem, 2.5vw, 1.5rem) !important; }
.text-adaptive-body { font-size: clamp(0.9rem, 1.5vw, 1.15rem) !important; }
.text-adaptive-chip { font-size: clamp(0.65rem, 1vw, 0.75rem) !important; }

/* ADAPTIVE SPACING */
.container-adaptive { padding-left: clamp(1rem, 5vw, 3rem) !important; padding-right: clamp(1rem, 5vw, 3rem) !important; }
.pa-adaptive-card { padding: clamp(1rem, 3vw, 2.5rem) !important; }
.pa-adaptive-message { padding: clamp(1.5rem, 4vw, 3rem) !important; }

/* SMART BUTTONS */
.action-btn { transition: all 0.2s ease; min-width: 0 !important; }
.adaptive-label { font-size: clamp(0.7rem, 1.1vw, 0.85rem); white-space: nowrap; }

@media (max-width: 1050px) {
  .adaptive-label { display: none !important; }
  .action-btn { width: 36px !important; padding: 0 !important; border-radius: 50% !important; }
  .action-btn :deep(.v-btn__content) { margin: 0 !important; }
  .action-btn :deep(.v-icon) { margin: 0 !important; }
}

.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.message-content { word-break: break-word; }
.white-space-pre { white-space: pre-wrap; }
.solution-card { border-left: 6px solid rgb(var(--v-theme-success)) !important; background-color: rgba(var(--v-theme-success), 0.03) !important; }
.transition-fast { transition: all 0.2s ease-in-out !important; }
.gap-2 { gap: 8px; }
.gap-3 { gap: 12px; }
.gap-6 { gap: 24px; }
</style>