<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const users = ref<any[]>([])
const loading = ref(true)
const snackbar = ref({ show: false, text: '', color: '' })

const profiles = [
  { id: '550e8400-e29b-41d4-a716-446655440000', name: 'ROLE_ADMIN' },
  { id: '550e8400-e29b-41d4-a716-446655440001', name: 'ROLE_MODERATOR' },
  { id: '550e8400-e29b-41d4-a716-446655440002', name: 'ROLE_USER' }
]

// Diálogos
const showPunishDialog = ref(false)
const showAdminConfirmDialog = ref(false)
const selectedUser = ref<any>(null)
const selectedProfileId = ref<string>('')

const punishForm = ref({
  mode: 'temporary',
  days: 7,
  justification: ''
})

const showMessage = (text: string, color: string = 'success') => {
  snackbar.value = { show: true, text, color }
}

const fetchUsers = async () => {
  try {
    const response = await api.get('/users')
    users.value = response.data
  } catch (error) {
    showMessage('Erro ao carregar usuários', 'error')
  } finally {
    loading.value = false
  }
}

const openRoleChange = (user: any, profileId: string) => {
  selectedUser.value = user
  selectedProfileId.value = profileId
  
  if (profileId === '550e8400-e29b-41d4-a716-446655440000') { // ROLE_ADMIN
    showAdminConfirmDialog.value = true
  } else {
    changeRole()
  }
}

const changeRole = async () => {
  try {
    await api.patch(`/users/${selectedUser.value.id}/role`, selectedProfileId.value)
    showMessage(`Cargo de ${selectedUser.value.name} atualizado!`)
    showAdminConfirmDialog.value = false
    await fetchUsers()
  } catch (error) {
    showMessage('Erro ao alterar cargo', 'error')
  }
}

const openPunish = (user: any, permanent: boolean) => {
  selectedUser.value = user
  punishForm.value = {
    mode: permanent ? 'permanent' : 'temporary',
    days: 7,
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
      justification: punishForm.value.justification 
    })
    showMessage('Usuário punido com sucesso!')
    showPunishDialog.value = false
    await fetchUsers()
  } catch (error) {
    showMessage('Erro ao aplicar punição. Verifique suas permissões.', 'error')
  }
}

const unblockUser = async (userId: string) => {
  try {
    await api.post(`/users/${userId}/unblock`)
    showMessage('Punição removida com sucesso!')
    await fetchUsers()
  } catch (error) {
    showMessage('Erro ao remover punição.', 'error')
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
                @update:model-value="(val) => openRoleChange(u, val as string)"
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
                  <v-list-item prepend-icon="mdi-account-cancel-outline" title="Bloquear Permanente" @click="openPunish(u, true)"></v-list-item>
                  <v-list-item prepend-icon="mdi-clock-alert-outline" title="Bloquear Temporário" @click="openPunish(u, false)"></v-list-item>
                  <v-list-item v-if="u.isPermanentBlocked || u.blockedUntil" prepend-icon="mdi-account-check-outline" title="Cancelar Punição" @click="unblockUser(u.id)"></v-list-item>
                </v-list>
              </v-menu>
            </td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

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
            :suffix="punishForm.days > 1 ? 'dias' : 'dia'"
          ></v-text-field>

          <v-textarea
            v-model="punishForm.justification"
            label="Justificativa"
            variant="outlined"
            placeholder="Descreva o motivo desta ação..."
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

    <!-- Diálogo de Confirmação Admin -->
    <v-dialog v-model="showAdminConfirmDialog" max-width="450">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-warning font-weight-bold">
          <v-icon start icon="mdi-alert-octagon"></v-icon>
          Confirmação Crítica
        </v-card-title>
        <v-card-text class="text-grey-lighten-1">
          Você tem certeza que deseja promover <strong>{{ selectedUser?.name }}</strong> ao cargo de <strong>ADMINISTRADOR</strong>? 
          <br><br>
          Esta ação concede controle total sobre o sistema para este usuário.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showAdminConfirmDialog = false">Cancelar</v-btn>
          <v-btn color="warning" variant="flat" @click="changeRole">Sim, Promover</v-btn>
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
.role-select { max-width: 180px; }
</style>