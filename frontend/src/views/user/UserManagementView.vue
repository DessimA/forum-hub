<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const users = ref<any[]>([])
const loading = ref(true)

const profiles = [
  { id: '550e8400-e29b-41d4-a716-446655440000', name: 'ROLE_ADMIN' },
  { id: '550e8400-e29b-41d4-a716-446655440001', name: 'ROLE_MODERATOR' },
  { id: '550e8400-e29b-41d4-a716-446655440002', name: 'ROLE_USER' }
]

const fetchUsers = async () => {
  try {
    const response = await api.get('/users')
    users.value = response.data
  } catch (error) {
    console.error('Error fetching users', error)
  } finally {
    loading.value = false
  }
}

const changeRole = async (userId: string, profileId: string) => {
  try {
    await api.patch(`/users/${userId}/role`, profileId)
    await fetchUsers()
  } catch (error) {
    alert('Erro ao alterar cargo.')
  }
}

const blockUser = async (userId: string, permanent: boolean) => {
  const days = permanent ? 0 : prompt('Bloquear por quantos dias?', '7')
  if (!permanent && !days) return
  
  const justification = prompt('Justificativa para o bloqueio (obrigatória):')
  if (!justification) {
    alert('Justificativa é obrigatória!')
    return
  }

  try {
    await api.post(`/users/${userId}/block`, { permanent, days: Number(days), justification })
    await fetchUsers()
  } catch (error) {
    alert('Erro ao bloquear usuário.')
  }
}

const unblockUser = async (userId: string) => {
  try {
    await api.post(`/users/${userId}/unblock`)
    await fetchUsers()
  } catch (error) {
    alert('Erro ao desbloquear usuário.')
  }
}

onMounted(fetchUsers)
</script>

<template>
  <v-container class="py-10">
    <h1 class="text-h4 font-weight-bold text-white mb-8">Gestão de Usuários</h1>
    
    <v-card color="surface" class="border-subtle" variant="flat">
      <v-table bg-color="transparent" theme="dark">
        <thead>
          <tr>
            <th class="text-grey-lighten-1">Nome / E-mail</th>
            <th class="text-grey-lighten-1">Cargo Atual</th>
            <th class="text-grey-lighten-1">Status</th>
            <th class="text-center text-grey-lighten-1">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td class="py-4">
              <div class="font-weight-bold text-white">{{ u.name }}</div>
              <div class="text-caption text-grey">{{ u.email }}</div>
            </td>
            <td>
              <v-select
                :model-value="u.profile"
                :items="profiles"
                item-title="name"
                item-value="id"
                density="compact"
                variant="plain"
                color="primary"
                class="role-select"
                :disabled="u.email === authStore.userEmail"
                @update:model-value="(val) => changeRole(u.id, val as string)"
              ></v-select>
            </td>
                            <td>
                              <v-chip v-if="u.isPermanentBlocked" color="error" size="small" variant="tonal">Banido</v-chip>
                              <v-chip v-else-if="u.blockedUntil" color="warning" size="small" variant="tonal">Suspenso</v-chip>
                              <v-chip v-else color="success" size="small" variant="tonal">Ativo</v-chip>
                            </td>
                            <td class="text-center">
                              <v-menu>
                                <template v-slot:activator="{ props }">
                                  <v-btn icon="mdi-dots-vertical" variant="text" v-bind="props" color="grey" :disabled="u.email === authStore.userEmail"></v-btn>
                                </template>
                                <v-list bg-color="surface" theme="dark">
                                  <v-list-item v-if="!u.isPermanentBlocked" prepend-icon="mdi-account-cancel-outline" title="Bloquear Permanente" @click="blockUser(u.id, true)"></v-list-item>
                                  <v-list-item v-if="!u.blockedUntil" prepend-icon="mdi-clock-alert-outline" title="Bloquear Temporário" @click="blockUser(u.id, false)"></v-list-item>
                                  <v-list-item v-if="u.isPermanentBlocked || u.blockedUntil" prepend-icon="mdi-account-check-outline" title="Cancelar Punição" @click="unblockUser(u.id)"></v-list-item>
                                </v-list>
                              </v-menu>
                            </td>
            
          </tr>
        </tbody>
      </v-table>
    </v-card>
  </v-container>
</template>

<style scoped>
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
.role-select { max-width: 180px; }
</style>
