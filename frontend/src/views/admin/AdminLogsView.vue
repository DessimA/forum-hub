<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import TheNavbar from '@/components/layout/TheNavbar.vue'

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

const deleteLog = async (id: number) => {
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
  <v-layout>
    <TheNavbar />
    <v-main>
      <v-container class="py-10">
        <h1 class="text-h4 neon-text mb-8">Auditoria do Sistema (Admin)</h1>
        
        <v-card color="surface" class="border-neon pa-4">
          <v-list bg-color="transparent" lines="three">
            <v-list-item v-for="log in logs" :key="log.id" class="mb-4 log-item">
              <template v-slot:prepend>
                <v-icon :color="log.actionType === 'DELETE' ? 'error' : 'primary'" size="large">
                  {{ log.actionType === 'DELETE' ? 'mdi-trash-can' : 'mdi-history' }}
                </v-icon>
              </template>
              
              <v-list-item-title class="font-weight-bold">
                {{ log.actionType }} - {{ log.entityType }} (ID: {{ log.entityId }})
              </v-list-item-title>
              
              <v-list-item-subtitle>
                <div class="text-grey-lighten-1 mt-1"><strong>Executor:</strong> {{ log.performedBy }}</div>
                <div class="mt-2 text-white"><strong>Conteúdo:</strong> {{ log.content }}</div>
                <div class="mt-1 text-warning" v-if="log.justification"><strong>Justificativa:</strong> {{ log.justification }}</div>
              </v-list-item-subtitle>

              <template v-slot:append>
                <div class="d-flex flex-column align-end">
                  <span class="text-caption text-grey mb-2">{{ new Date(log.createdAt).toLocaleString() }}</span>
                  <v-btn icon="mdi-delete-forever" color="error" variant="text" @click="deleteLog(log.id)"></v-btn>
                </div>
              </template>
            </v-list-item>
          </v-list>
          <div v-if="logs.length === 0" class="text-center py-10 text-grey">Nenhum log encontrado.</div>
        </v-card>
      </v-container>
    </v-main>
  </v-layout>
</template>

<style scoped>
.border-neon { border: 1px solid #00f0ff !important; }
.neon-text { color: #00f0ff; text-shadow: 0 0 10px rgba(0, 240, 255, 0.3); }
.log-item { border-bottom: 1px solid rgba(255,255,255,0.1); }
</style>
