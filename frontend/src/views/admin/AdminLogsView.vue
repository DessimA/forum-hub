<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const logs = ref<any[]>([])
const loading = ref(true)

const fetchLogs = async () => {
  try {
    const response = await api.get('/admin/logs')
    logs.value = response.data
  } catch (error) {
    console.error('Error fetching logs')
  } finally {
    loading.value = false
  }
}

const restoreEntity = async (id: string) => {
  if(!confirm('Deseja restaurar este item para o sistema?')) return
  try {
    await api.post(`/admin/logs/${id}/restore`)
    alert('Item restaurado com sucesso!')
    await fetchLogs()
  } catch (error: any) {
    const msg = error.response?.data?.message || ''
    if (msg.includes('Restore the topic first')) {
      alert('Falha na restauração: Este item pertence a um tópico que também foi deletado. Por favor, restaure o Tópico primeiro.')
    } else {
      alert('Erro ao restaurar item. Verifique se os dados originais ainda são válidos.')
    }
  }
}

const deleteLog = async (id: string) => {
  if(!confirm('Excluir este log permanentemente? O conteúdo arquivado será perdido.')) return
  try {
    await api.delete(`/admin/logs/${id}`)
    await fetchLogs()
  } catch (error) {
    alert('Erro ao excluir log.')
  }
}

onMounted(fetchLogs)
</script>

<template>
  <v-container class="py-10">
    <h1 class="text-h4 font-weight-bold text-white mb-8">Auditoria e Recuperação</h1>
    
    <v-card color="surface" class="border-subtle pa-4" variant="flat">
      <v-list bg-color="transparent" lines="three" theme="dark">
        <v-list-item v-for="log in logs" :key="log.id" class="mb-4 log-item">
          <template v-slot:prepend>
            <v-avatar color="surface-light" size="48" class="mr-4">
              <v-icon :color="log.actionType === 'DELETE' ? 'error' : 'primary'">
                {{ log.actionType === 'DELETE' ? 'mdi-trash-can-outline' : 'mdi-history' }}
              </v-icon>
            </v-avatar>
          </template>
          
          <v-list-item-title class="font-weight-bold text-white">
            {{ log.actionType }} - {{ log.entityType }}
          </v-list-item-title>
          
          <v-list-item-subtitle>
            <div class="text-grey-lighten-1 mt-1 text-caption">Executor: {{ log.performedBy }}</div>
            <div class="mt-2 text-grey-lighten-2">Conteúdo: {{ log.content }}</div>
            <div class="mt-1 text-warning" v-if="log.justification">Justificativa: {{ log.justification }}</div>
          </v-list-item-subtitle>

          <template v-slot:append>
            <div class="d-flex flex-column align-end">
              <span class="text-caption text-grey mb-2">{{ new Date(log.createdAt).toLocaleString() }}</span>
              <div class="d-flex gap-2">
                <v-btn v-if="log.actionType === 'DELETE'" icon="mdi-backup-restore" color="success" variant="text" size="small" title="Restaurar" @click="restoreEntity(log.id)"></v-btn>
                <v-btn icon="mdi-delete-forever-outline" color="error" variant="text" size="small" title="Remover Permanente" @click="deleteLog(log.id)"></v-btn>
              </div>
            </div>
          </template>
        </v-list-item>
      </v-list>
      <div v-if="logs.length === 0" class="text-center py-10 text-grey">Nenhum log encontrado.</div>
    </v-card>
  </v-container>
</template>

<style scoped>
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
.log-item { border-bottom: 1px solid rgba(255,255,255,0.03); }
</style>
