<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import TheNavbar from '@/components/layout/TheNavbar.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const users = ref<any[]>([])
const loading = ref(true)

const profiles = [
  { id: 1, name: 'ROLE_ADMIN' },
  { id: 2, name: 'ROLE_MODERATOR' },
  { id: 3, name: 'ROLE_USER' }
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

const changeRole = async (userId: number, profileId: number) => {
  try {
    await api.patch(`/users/${userId}/role`, profileId)
    await fetchUsers()
  } catch (error) {
    alert('Erro ao alterar cargo.')
  }
}

const blockUser = async (userId: number, permanent: boolean) => {
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

const unblockUser = async (userId: number) => {
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
  <v-layout>
    <TheNavbar />
    <v-main>
      <v-container class="py-10">
        <h1 class="text-h4 neon-text mb-8">Gestão Avançada de Usuários</h1>
        
        <v-card color="surface" class="border-neon">
          <v-table bg-color="transparent" theme="dark">
            <thead>
              <tr>
                <th>Nome / E-mail</th>
                <th>Cargo Atual</th>
                <th>Status</th>
                <th class="text-center">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in users" :key="u.id">
                <td>
                  <div class="font-weight-bold">{{ u.name }}</div>
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
                    class="role-select"
                    :disabled="u.email === authStore.userEmail"
                    @update:model-value="(val) => changeRole(u.id, val as number)"
                  ></v-select>
                </td>
                <td>
                  <v-chip v-if="u.isPermanentBlocked" color="error" size="small">Banido</v-chip>
                  <v-chip v-else-if="u.blockedUntil" color="warning" size="small">Suspenso</v-chip>
                  <v-chip v-else color="success" size="small">Ativo</v-chip>
                </td>
                <td class="text-center">
                  <v-menu>
                    <template v-slot:activator="{ props }">
                      <v-btn icon="mdi-dots-vertical" variant="text" v-bind="props" :disabled="u.email === authStore.userEmail"></v-btn>
                    </template>
                    <v-list bg-color="surface">
                      <v-list-item v-if="!u.isPermanentBlocked" prepend-icon="mdi-account-cancel" title="Bloquear Permanente" @click="blockUser(u.id, true)"></v-list-item>
                      <v-list-item v-if="!u.blockedUntil" prepend-icon="mdi-clock-alert" title="Bloquear Temporário" @click="blockUser(u.id, false)"></v-list-item>
                      <v-list-item v-if="u.isPermanentBlocked || u.blockedUntil" prepend-icon="mdi-account-check" title="Desbloquear" @click="unblockUser(u.id)"></v-list-item>
                    </v-list>
                  </v-menu>
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-container>
    </v-main>
  </v-layout>
</template>

<style scoped>
.border-neon { border: 1px solid #00f0ff !important; }
.neon-text { color: #00f0ff; text-shadow: 0 0 10px rgba(0, 240, 255, 0.3); }
.role-select { max-width: 180px; }
</style>