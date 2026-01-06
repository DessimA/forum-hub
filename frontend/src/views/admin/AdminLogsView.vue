<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const logs = ref<any[]>([])
const loading = ref(true)
const snackbar = ref({ show: false, text: '', color: '' })

// Dialog State
const confirmDialog = ref({
  show: false,
  title: '',
  message: '',
  action: null as (() => Promise<void>) | null,
  btnText: '',
  btnColor: '',
  isTopicRestore: false,
  includeAnswers: true
})

const showMessage = (text: string, color: string = 'success') => {
  snackbar.value = { show: true, text, color }
}

const fetchLogs = async () => {
  loading.value = true
  try {
    const response = await api.get('/admin/logs')
    logs.value = response.data
  } catch (error) {
    showMessage('Erro ao carregar logs', 'error')
  } finally {
    loading.value = false
  }
}

const openConfirm = (title: string, message: string, btnText: string, color: string, isTopic: boolean, action: () => Promise<void>) => {
  confirmDialog.value = {
    show: true,
    title,
    message,
    btnText,
    btnColor: color,
    action,
    isTopicRestore: isTopic,
    includeAnswers: true
  }
}

const handleConfirm = async () => {
  if (confirmDialog.value.action) {
    await confirmDialog.value.action()
  }
  confirmDialog.value.show = false
}

const restoreEntity = async (log: any) => {
  const isTopic = log.entityType === 'TOPIC'
  
  openConfirm(
    'Restaurar Item',
    `Deseja restaurar este ${isTopic ? 'tópico' : 'comentário'} para o sistema?`,
    'RESTAURAR',
    'success',
    isTopic,
    async () => {
      try {
        await api.post(`/admin/logs/${log.id}/restore?includeAnswers=${confirmDialog.value.includeAnswers}`)
        showMessage('Item restaurado com sucesso!')
        await fetchLogs()
      } catch (error: any) {
        const msg = error.response?.data?.message || ''
        if (msg.includes('Restore the topic first')) {
          showMessage('Falha: Restaure o Tópico pai primeiro.', 'warning')
        } else {
          showMessage('Erro ao restaurar item.', 'error')
        }
      }
    }
  )
}

const deleteLog = async (id: string) => {
  openConfirm(
    'Excluir Log',
    'Excluir este log permanentemente? O conteúdo arquivado será perdido.',
    'EXCLUIR',
    'error',
    false,
    async () => {
      try {
        await api.delete(`/admin/logs/${id}`)
        showMessage('Log excluído permanentemente.')
        await fetchLogs()
      } catch (error) {
        showMessage('Erro ao excluir log.', 'error')
      }
    }
  )
}

onMounted(fetchLogs)
</script>

<template>
  <div class="container-responsive py-6">
    <div class="mb-8 px-1">
      <h1 class="text-responsive-h1 text-text-primary">Auditoria</h1>
      <p class="text-sm text-text-secondary">Histórico de ações e recuperação de dados arquivados</p>
    </div>
    
    <div v-if="loading" class="text-center py-12">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <v-card v-else-if="logs.length > 0" variant="outlined" class="bg-surface border-all rounded-sm">
      <v-list bg-color="transparent" class="pa-0">
        <v-list-item 
          v-for="log in logs" 
          :key="log.id" 
          class="pa-4 pa-md-6 border-b last:border-0 hover-bg transition-fast"
        >
          <template v-slot:prepend>
            <v-avatar color="rgba(var(--v-theme-primary), 0.05)" size="40" class="mr-4 border">
              <v-icon :color="log.actionType === 'DELETE' ? 'error' : 'primary'" size="20">
                {{ log.actionType === 'DELETE' ? 'mdi-trash-can-outline' : 'mdi-history' }}
              </v-icon>
            </v-avatar>
          </template>
          
          <v-list-item-title class="font-bold text-text-primary d-flex align-center flex-wrap gap-2 mb-1">
            <span :class="log.actionType === 'DELETE' ? 'text-error' : 'text-primary'">{{ log.actionType }}</span>
            <span class="text-secondary opacity-50">•</span>
            <span>{{ log.entityType }}</span>
          </v-list-item-title>
          
          <v-list-item-subtitle class="mt-2">
            <div class="text-[11px] text-secondary mb-2">
              <v-icon size="12" class="mr-1">mdi-account-circle-outline</v-icon>
              Executor: <span class="text-text-primary font-bold">{{ log.performedBy }}</span>
              <span class="mx-2 opacity-30">|</span>
              <v-icon size="12" class="mr-1">mdi-clock-outline</v-icon>
              {{ new Date(log.createdAt).toLocaleString() }}
            </div>
            <div class="text-sm text-text-primary line-clamp-2 leading-relaxed mb-2">
              {{ log.content }}
            </div>
            <div 
              v-if="log.justification" 
              class="text-xs pa-2 rounded-sm bg-background-alt border border-dashed text-primary font-medium"
            >
              Justificativa: {{ log.justification }}
            </div>
          </v-list-item-subtitle>

          <template v-slot:append>
            <div class="d-flex gap-2 ml-4">
              <v-btn 
                v-if="log.actionType === 'DELETE'" 
                icon="mdi-backup-restore" 
                color="success" 
                variant="tonal" 
                size="small" 
                title="Restaurar" 
                @click="restoreEntity(log)"
              ></v-btn>
              <v-btn 
                icon="mdi-delete-forever-outline" 
                color="error" 
                variant="tonal" 
                size="small" 
                title="Remover Permanente" 
                @click="deleteLog(log.id)"
              ></v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card>
    
    <div v-else class="text-center py-16 bg-surface border border-dashed rounded-sm opacity-60">
      <v-icon size="64" color="secondary" class="mb-4">mdi-clipboard-text-off-outline</v-icon>
      <h3 class="text-lg font-weight-bold text-text-primary">Nenhum log encontrado</h3>
      <p class="text-sm text-secondary">O histórico de auditoria aparecerá aqui conforme as ações forem realizadas.</p>
    </div>

    <!-- Confirmation Dialog -->
    <v-dialog v-model="confirmDialog.show" max-width="450">
      <v-card class="pa-6 bg-surface border-all elevation-24">
        <h2 class="text-lg font-bold text-text-primary mb-2">{{ confirmDialog.title }}</h2>
        <p class="text-sm text-secondary mb-6">{{ confirmDialog.message }}</p>
        
        <!-- Recursive Restore Option -->
        <div v-if="confirmDialog.isTopicRestore" class="mb-6 pa-3 rounded-sm bg-background-alt border">
          <v-checkbox
            v-model="confirmDialog.includeAnswers"
            label="Restaurar todas as respostas associadas"
            color="primary"
            hide-details
            density="compact"
          ></v-checkbox>
          <p class="text-[10px] text-secondary mt-1 ml-10">
            Busca automaticamente todos os logs de comentários deletados vinculados a este tópico.
          </p>
        </div>

        <div class="d-flex justify-end gap-3 mt-4">
          <v-btn variant="text" @click="confirmDialog.show = false" class="font-weight-bold px-4">
            CANCELAR
          </v-btn>
          <v-btn :color="confirmDialog.btnColor" @click="handleConfirm" class="font-weight-bold px-6 text-white">
            {{ confirmDialog.btnText }}
          </v-btn>
        </div>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" class="mb-4">
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<style scoped>
.hover-bg:hover {
  background-color: rgba(var(--v-theme-text-primary), 0.02) !important;
}
.border-b {
  border-bottom: 1px solid rgba(var(--v-border-color), 0.1) !important;
}
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.bg-background-alt {
  background: rgba(var(--v-theme-text-primary), 0.02);
}
.last\:border-0:last-child { border-bottom: 0 !important; }
.gap-2 { gap: 8px; }
.gap-3 { gap: 12px; }
</style>