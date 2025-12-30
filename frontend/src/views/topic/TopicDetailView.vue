<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()
const topic = ref<any>(null)
const newAnswer = ref('')
const loading = ref(true)
const sending = ref(false)

const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editForm = ref({ title: '', message: '' })

const fetchTopic = async () => {
  try {
    const response = await api.get(`/topics/${route.params.id}`)
    topic.value = response.data
    editForm.value = { title: response.data.title, message: response.data.message }
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
    await fetchTopic()
  } catch (error) {
    alert('Erro ao atualizar tópico')
  }
}

const handleDelete = async () => {
  let justification = ''
  if (topic.value.authorEmail !== authStore.userEmail) {
    justification = prompt('Justificativa para a exclusão (obrigatório para moderadores):') || ''
    if (!justification) return
  }
  
  try {
    await api.delete(`/topics/${topic.value.id}?justification=${encodeURIComponent(justification)}`)
    router.push('/')
  } catch (error) {
    alert('Erro ao excluir tópico')
  }
}

const sendAnswer = async () => {
  if (!newAnswer.value.trim()) return
  sending.value = true
  try {
    await api.post(`/topics/${route.params.id}/answers`, { message: newAnswer.value })
    newAnswer.value = ''
    await fetchTopic()
  } catch (error) {
    alert('Erro ao enviar resposta')
  } finally {
    sending.value = false
  }
}

const toggleSolution = async (answerId: number) => {
  try {
    await api.patch(`/answers/${answerId}/solution`)
    await fetchTopic()
  } catch (error) {
    alert('Apenas o autor do tópico pode gerenciar soluções')
  }
}

const deleteAnswer = async (id: number, authorEmail: string) => {
  let justification = ''
  if (authorEmail !== authStore.userEmail) {
    justification = prompt('Justificativa para a exclusão (obrigatório para moderadores):') || ''
    if (!justification) return
  }

  if(!confirm('Deseja excluir esta resposta?')) return
  try {
    await api.delete(`/answers/${id}?justification=${encodeURIComponent(justification)}`)
    await fetchTopic()
  } catch (error) {
    alert('Erro ao excluir resposta')
  }
}

const blockUser = async (userId: number) => {
  const isOnlyModerator = authStore.isModerator && !authStore.isAdmin
  let permanent = false
  let days = 1
  
  if (!isOnlyModerator) {
    const mode = prompt('Bloqueio: (1) Permanente, (2) Temporário (dias):', '1')
    if (!mode) return
    if (mode === '1') {
      permanent = true
    } else {
      days = parseInt(prompt('Quantidade de dias:', '7') || '7')
    }
  } else {
    if (!confirm('Suspender usuário por 24h?')) return
    permanent = false
    days = 1
  }

  const justification = prompt('Justificativa para a punição (obrigatória):')
  if (!justification) {
    alert('Justificativa é obrigatória!')
    return
  }

  try {
    await api.post(`/users/${userId}/block`, { permanent, days, justification })
    alert('Usuário punido com sucesso!')
  } catch (error) {
    alert('Erro ao processar punição. Verifique se você tem permissão.')
  }
}

onMounted(fetchTopic)
</script>

<template>
  <v-container v-if="!loading && topic" class="py-10">
    <v-card color="surface" class="pa-6 mb-8 border-subtle">
      <div class="d-flex justify-space-between mb-4">
        <v-chip color="secondary" size="small" variant="tonal">{{ topic.course }}</v-chip>
        <v-chip :color="topic.status === 'RESOLVED' ? 'success' : 'primary'" variant="tonal" size="small" class="font-weight-bold">
          {{ topic.status === 'RESOLVED' ? '✓ RESOLVIDO' : 'ABERTO' }}
        </v-chip>
      </div>
      <h1 class="text-h3 font-weight-bold mb-4 text-white">{{ topic.title }}</h1>
      <div class="d-flex align-center justify-space-between mb-6">
        <div class="d-flex align-center text-grey">
          <v-icon class="mr-2">mdi-account-outline</v-icon> {{ topic.author }}
          <v-icon class="ml-4 mr-2">mdi-clock-outline</v-icon> {{ new Date(topic.createdAt).toLocaleString() }}
        </div>
        <div class="d-flex gap-2" v-if="authStore.isAdmin || authStore.isModerator || topic.authorEmail === authStore.userEmail">
          <v-btn v-if="authStore.isAdmin || topic.authorEmail === authStore.userEmail" icon="mdi-pencil" variant="text" color="primary" @click="showEditDialog = true"></v-btn>
          <v-btn icon="mdi-delete" variant="text" color="error" @click="showDeleteDialog = true"></v-btn>
          <v-btn v-if="(authStore.isAdmin || authStore.isModerator) && topic.authorEmail !== authStore.userEmail" icon="mdi-account-cancel" variant="text" color="warning" @click="blockUser(topic.authorId)"></v-btn>
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
                          @click="blockUser(resp.authorId)"
                        ></v-btn>
          
        </div>
      </div>
    </v-card>

    <v-card color="surface" class="pa-6 mt-10 border-subtle" variant="flat">
      <h3 class="text-h6 mb-4 text-white">Sua Resposta</h3>
      <v-textarea v-model="newAnswer" placeholder="Escreva sua contribuição..." variant="outlined" color="primary" rows="4"></v-textarea>
      <v-btn color="primary" variant="flat" class="mt-2 font-weight-bold" :loading="sending" @click="sendAnswer">Responder</v-btn>
    </v-card>

    <v-dialog v-model="showEditDialog" max-width="600px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-white">Editar Tópico</v-card-title>
        <v-card-text>
          <v-text-field v-model="editForm.title" label="Título" variant="outlined" color="primary"></v-text-field>
          <v-textarea v-model="editForm.message" label="Mensagem" variant="outlined" color="primary" rows="5"></v-textarea>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showEditDialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" @click="handleUpdate">Salvar Alterações</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showDeleteDialog" max-width="400px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-error font-weight-bold">Excluir Tópico?</v-card-title>
        <v-card-text class="text-grey-lighten-1">Esta ação não pode ser desfeita.</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showDeleteDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="flat" @click="handleDelete">Sim, Excluir</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
