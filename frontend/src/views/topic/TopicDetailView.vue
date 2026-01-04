<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useFavoriteStore } from '@/stores/favoriteStore'
import FavoriteButton from '@/components/common/FavoriteButton.vue'

const authStore = useAuthStore()
const favoriteStore = useFavoriteStore()
const route = useRoute()
const router = useRouter()
const topic = ref<any>(null)
const newAnswer = ref('')
const loading = ref(true)
const sending = ref(false)
const snackbar = ref({ show: false, text: '', color: '' })

// Diálogos
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const showPunishDialog = ref(false)
const showEditAnswerDialog = ref(false)
const selectedUser = ref<any>(null)
const selectedAnswer = ref<any>(null)
const courses = ref<any[]>([])

const editForm = ref({ title: '', message: '', courseId: null as string | null })
const editAnswerForm = ref({ message: '' })
const punishForm = ref({
  mode: 'temporary',
  days: 1,
  justification: ''
})

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

const openEdit = async () => {
  await fetchCourses()
  showEditDialog.value = true
}

const openEditAnswer = (answer: any) => {
  selectedAnswer.value = answer
  editAnswerForm.value = { message: answer.message }
  showEditAnswerDialog.value = true
}

const handleUpdateAnswer = async () => {
  try {
    await api.put(`/answers/${selectedAnswer.value.id}`, editAnswerForm.value)
    showEditAnswerDialog.value = false
    showMessage('Resposta atualizada!')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao atualizar resposta', 'error')
  }
}

const fetchTopic = async () => {
  try {
    const response = await api.get(`/topics/${route.params.id}`)
    topic.value = response.data
    // Update form with current data
    editForm.value = { 
      title: response.data.title, 
      message: response.data.message,
      courseId: response.data.courseId
    }
  } catch (error) {
    console.error('Error loading details', error)
    router.push('/')
  } finally {
    loading.value = false
  }
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

const handleClose = async () => {
  try {
    await api.patch(`/topics/${topic.value.id}/close`)
    showMessage('Tópico fechado.')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao fechar tópico', 'error')
  }
}

const handleReopen = async () => {
  try {
    await api.patch(`/topics/${topic.value.id}/reopen`)
    showMessage('Tópico reaberto!')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao reabrir tópico', 'error')
  }
}

const handleDelete = async () => {
  let justification = ''
  if (topic.value.authorEmail !== authStore.userEmail) {
    justification = punishForm.value.justification
    if (!justification) {
      showMessage('Justificativa é obrigatória para exclusão por moderação.', 'warning')
      return
    }
  }
  
  try {
    await api.delete(`/topics/${topic.value.id}?justification=${encodeURIComponent(justification)}`)
    showMessage('Tópico excluído.')
    router.push('/')
  } catch (error) {
    showMessage('Erro ao excluir tópico', 'error')
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
  } catch (error) {
    showMessage('Erro ao enviar resposta', 'error')
  } finally {
    sending.value = false
  }
}

const toggleSolution = async (answerId: number) => {
  try {
    await api.patch(`/answers/${answerId}/solution`)
    showMessage('Status da solução atualizado.')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao gerenciar solução', 'error')
  }
}

const deleteAnswer = async (id: number, authorEmail: string) => {
  if (authorEmail === authStore.userEmail) {
    if(!confirm('Excluir sua resposta?')) return
    try {
      await api.delete(`/answers/${id}`)
      showMessage('Resposta removida.')
      await fetchTopic()
    } catch (error) {
      showMessage('Erro ao excluir', 'error')
    }
  } else {
    const reason = prompt('Justificativa para remover resposta alheia:')
    if (!reason) return
    try {
      await api.delete(`/answers/${id}?justification=${encodeURIComponent(reason)}`)
      showMessage('Resposta removida por moderação.')
      await fetchTopic()
    } catch (error) {
      showMessage('Erro ao excluir', 'error')
    }
  }
}

const openPunish = (userId: number, name: string, context: { topicId?: string, answerId?: string } = {}) => {
  selectedUser.value = { id: userId, name, ...context }
  punishForm.value = {
    mode: 'temporary',
    days: 1,
    justification: ''
  }
  showPunishDialog.value = true
}

const confirmPunish = async () => {
  if (!punishForm.value.justification) {
    showMessage('A justificativa é obrigatória!', 'warning')
    return
  }

  try {
    await api.post(`/users/${selectedUser.value.id}/block`, { 
      permanent: punishForm.value.mode === 'permanent', 
      days: punishForm.value.mode === 'permanent' ? 0 : Number(punishForm.value.days), 
      justification: punishForm.value.justification,
      topicId: selectedUser.value.topicId || null,
      answerId: selectedUser.value.answerId || null
    })
    
    showMessage('Usuário punido e conteúdo removido!', 'warning')
    showPunishDialog.value = false
    
    // Se o tópico foi deletado (punimos o autor do tópico), voltamos para a home
    if (selectedUser.value.topicId) {
      router.push('/')
    } else {
      await fetchTopic()
    }
  } catch (error) {
    showMessage('Erro ao processar punição.', 'error')
  }
}

const unblockUser = async (userId: number) => {
  try {
    await api.post(`/users/${userId}/unblock`)
    showMessage('Punição removida com sucesso!')
    await fetchTopic()
  } catch (error) {
    showMessage('Erro ao remover punição.', 'error')
  }
}

onMounted(() => {
  fetchTopic()
  favoriteStore.loadUserFavorites()
})
</script>

<template>
  <v-container v-if="!loading && topic" class="py-10">
    <v-card color="surface" class="pa-6 mb-8 border-subtle">
      <div class="d-flex justify-space-between mb-4">
        <v-chip color="secondary" size="small" variant="tonal">{{ topic.course }}</v-chip>
        <div class="d-flex gap-2 align-center">
          <FavoriteButton :topicId="topic.id" />
          <v-chip v-if="topic.authorIsBlocked || topic.authorBlockedUntil" color="error" size="x-small" variant="flat" prepend-icon="mdi-account-alert">AUTOR PUNIDO</v-chip>
          <v-chip v-if="topic.status === 'CLOSED'" color="grey" variant="tonal" size="small" class="font-weight-bold">
            FECHADO
          </v-chip>
          <v-chip v-else :color="topic.status === 'RESOLVED' ? 'success' : 'primary'" variant="tonal" size="small" class="font-weight-bold">
            {{ topic.status === 'RESOLVED' ? '✓ RESOLVIDO' : 'ABERTO' }}
          </v-chip>
        </div>
      </div>
      <h1 class="text-h3 font-weight-bold mb-4 text-white">{{ topic.title }}</h1>
      <div class="d-flex align-center justify-space-between mb-6">
        <div class="d-flex align-center text-grey">
          <v-icon class="mr-2">mdi-account-outline</v-icon> {{ topic.author }}
          <v-icon class="ml-4 mr-2">mdi-clock-outline</v-icon> {{ new Date(topic.createdAt).toLocaleString() }}
        </div>
        <div class="d-flex gap-2" v-if="authStore.isAdmin || authStore.isModerator || topic.authorEmail === authStore.userEmail">
          <v-btn v-if="(authStore.isAdmin || topic.authorEmail === authStore.userEmail) && topic.status === 'CLOSED'" icon="mdi-lock-open-outline" variant="text" color="success" title="Reabrir Tópico" @click="handleReopen"></v-btn>
          <v-btn v-if="(authStore.isAdmin || topic.authorEmail === authStore.userEmail) && topic.status === 'OPEN'" icon="mdi-lock-outline" variant="text" color="grey" title="Fechar Tópico" @click="handleClose"></v-btn>
          <v-btn v-if="(authStore.isAdmin || topic.authorEmail === authStore.userEmail) && topic.status === 'OPEN'" icon="mdi-pencil" variant="text" color="primary" title="Editar Tópico" @click="openEdit"></v-btn>
          <v-btn icon="mdi-delete" variant="text" color="error" title="Excluir Tópico" @click="showDeleteDialog = true"></v-btn>
          
          <!-- Botões de Punição do Autor -->
          <template v-if="(authStore.isAdmin || authStore.isModerator) && topic.authorEmail !== authStore.userEmail">
            <v-btn v-if="!topic.authorIsBlocked && !topic.authorBlockedUntil" icon="mdi-account-cancel" variant="text" color="warning" title="Punir Autor" @click="openPunish(topic.authorId, topic.author, { topicId: topic.id })"></v-btn>
            <v-btn v-else icon="mdi-account-check" variant="text" color="success" title="Remover Punição" @click="unblockUser(topic.authorId)"></v-btn>
          </template>
        </div>
      </div>
      <v-divider class="mb-6" style="opacity: 0.05"></v-divider>
      <div class="text-body-1 white-space-pre text-grey-lighten-3">{{ topic.message }}</div>
    </v-card>

    <h2 class="text-h5 text-white mb-6">Respostas ({{ topic.answers.length }})</h2>
    
    <v-card v-for="resp in topic.answers" :key="resp.id" class="mb-4 pa-4 card-soft" :class="{'solucao-border': resp.solution}" color="surface" variant="flat">
      <div class="d-flex justify-space-between align-center mb-2">
        <div class="font-weight-bold text-primary">{{ resp.author }}</div>
        <v-chip v-if="resp.solution" color="success" size="x-small" variant="flat" prepend-icon="mdi-check-decagram">SOLUÇÃO</v-chip>
      </div>
      <p class="mb-4 text-grey-lighten-2">{{ resp.message }}</p>
      <div class="d-flex justify-space-between align-center">
        <span class="text-caption text-grey">{{ new Date(resp.createdAt).toLocaleString() }}</span>
        <div class="d-flex gap-2">
          <v-btn v-if="authStore.isAdmin || topic.authorEmail === authStore.userEmail" variant="text" size="small" :color="resp.solution ? 'warning' : 'success'" :prepend-icon="resp.solution ? 'mdi-close-circle' : 'mdi-check'" @click="toggleSolution(resp.id)">
            {{ resp.solution ? 'Desmarcar Solução' : 'Marcar como Solução' }}
          </v-btn>
          
          <!-- Botão Editar Resposta: Só o autor -->
          <v-btn 
            v-if="resp.authorEmail === authStore.userEmail"
            icon="mdi-pencil-outline" 
            variant="text" 
            size="small" 
            color="primary" 
            title="Editar Resposta"
            @click="openEditAnswer(resp)"
          ></v-btn>

          <v-btn 
            v-if="authStore.isAdmin || authStore.isModerator || resp.authorEmail === authStore.userEmail"
            icon="mdi-delete-outline" 
            variant="text" 
            size="small" 
            color="grey" 
            @click="deleteAnswer(resp.id, resp.authorEmail)"
          ></v-btn>
          <v-btn 
            v-if="(authStore.isAdmin || authStore.isModerator) && resp.authorEmail !== authStore.userEmail"
            icon="mdi-account-cancel-outline" 
            variant="text" 
            size="small" 
            color="warning" 
            title="Punir Autor da Resposta"
            @click="openPunish(resp.authorId, resp.author, { answerId: resp.id })"
          ></v-btn>
        </div>
      </div>
    </v-card>

    <v-card v-if="topic.status === 'OPEN'" color="surface" class="pa-6 mt-10 border-subtle" variant="flat">
      <h3 class="text-h6 mb-4 text-white">Sua Resposta</h3>
      <v-textarea v-model="newAnswer" placeholder="Escreva sua contribuição..." variant="outlined" color="primary" rows="4"></v-textarea>
      <v-btn color="primary" variant="flat" class="mt-2 font-weight-bold" :loading="sending" @click="sendAnswer">Responder</v-btn>
    </v-card>
    <v-alert v-else type="info" variant="tonal" class="mt-10" icon="mdi-information-outline">
      Este tópico está {{ topic.status === 'CLOSED' ? 'fechado' : 'resolvido' }} e não aceita mais novas respostas.
    </v-alert>

    <!-- Diálogo de Edição -->
    <v-dialog v-model="showEditDialog" max-width="600px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-white">Editar Tópico</v-card-title>
        <v-card-text>
          <v-text-field v-model="editForm.title" label="Título" variant="outlined" color="primary"></v-text-field>
          <v-select 
            v-model="editForm.courseId" 
            :items="courses" 
            item-title="name" 
            item-value="id" 
            label="Mudar de Curso" 
            variant="outlined" 
            color="primary"
          ></v-select>
          <v-textarea v-model="editForm.message" label="Mensagem" variant="outlined" color="primary" rows="5"></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showEditDialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" @click="handleUpdate">Salvar Alterações</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de Exclusão -->
    <v-dialog v-model="showDeleteDialog" max-width="450px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-error font-weight-bold">Excluir Tópico?</v-card-title>
        <v-card-text>
          <div class="text-grey-lighten-1 mb-4">Esta ação não pode ser desfeita.</div>
          <v-textarea
            v-if="topic.authorEmail !== authStore.userEmail"
            v-model="punishForm.justification"
            label="Justificativa da Exclusão"
            variant="outlined"
            placeholder="Obrigatório para moderadores..."
            rows="3"
          ></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showDeleteDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="flat" @click="handleDelete">Sim, Excluir</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de Punição -->
    <v-dialog v-model="showPunishDialog" max-width="500">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-h5 text-white">Aplicar Punição</v-card-title>
        <v-card-text>
          <div class="text-subtitle-1 text-grey mb-4">Usuário: {{ selectedUser?.name }}</div>
          <v-radio-group v-model="punishForm.mode" inline color="primary">
            <v-radio label="Temporário" value="temporary"></v-radio>
            <v-radio label="Permanente (Banir)" value="permanent" v-if="authStore.isAdmin"></v-radio>
          </v-radio-group>
          <v-text-field
            v-if="punishForm.mode === 'temporary'"
            v-model="punishForm.days"
            type="number"
            label="Quantidade de dias"
            variant="outlined"
            density="compact"
          ></v-text-field>
          <v-textarea
            v-model="punishForm.justification"
            label="Justificativa"
            variant="outlined"
            rows="3"
            required
          ></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showPunishDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="flat" @click="confirmPunish">Confirmar Punição</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de Edição de Resposta -->
    <v-dialog v-model="showEditAnswerDialog" max-width="600px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-white">Editar Resposta</v-card-title>
        <v-card-text>
          <v-textarea v-model="editAnswerForm.message" label="Sua Resposta" variant="outlined" color="primary" rows="5"></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showEditAnswerDialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" @click="handleUpdateAnswer">Salvar Alterações</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
.card-soft {
  border: 1px solid rgba(255, 255, 255, 0.03) !important;
}
.solucao-border { 
  border: 1px solid rgba(76, 175, 80, 0.3) !important; 
  background-color: rgba(76, 175, 80, 0.02) !important;
}
.white-space-pre { white-space: pre-wrap; }
</style>