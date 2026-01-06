<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import BaseChip from '@/components/base/BaseChip.vue'
import { useDisplay } from 'vuetify'

const { mdAndUp } = useDisplay()
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
  mode: 'temporary' as 'temporary' | 'permanent',
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
    showMessage('Erro ao aplicar punição.', 'error')
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
  <v-container class="py-10 py-md-16 px-4 px-md-12 max-w-[1400px] mx-auto">
    <header class="mb-12 mb-md-16 px-1">
      <h1 class="text-responsive-h1 text-text-primary font-weight-bold mb-2">Gestão de Usuários</h1>
      <p class="text-base text-text-secondary opacity-70">Controle de permissões e moderação ativa da comunidade</p>
    </header>

    <div v-if="loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
    </div>

    <template v-else>
      <!-- Desktop View: Table -->
      <v-card v-if="mdAndUp" variant="outlined" class="bg-surface border-all rounded-lg overflow-hidden elevation-2">
        <v-table hover class="bg-transparent">
          <thead>
            <tr class="bg-background-alt border-b">
              <th class="text-xs uppercase font-bold text-secondary py-6 pl-8">Usuário</th>
              <th class="text-xs uppercase font-bold text-secondary py-6">Cargo / Nível</th>
              <th class="text-xs uppercase font-bold text-secondary py-6">Status da Conta</th>
              <th class="text-right text-xs uppercase font-bold text-secondary py-6 pr-8">Ações</th>
            </tr>
          </thead>
          <tbody class="text-sm">
            <tr v-for="u in users" :key="u.id" class="border-b last:border-0 hover-bg transition-fast">
              <td class="py-6 pl-8">
                <div class="d-flex align-center">
                  <v-avatar size="40" color="primary" class="mr-4 text-xs text-white font-bold elevation-2">
                    {{ u.name.charAt(0).toUpperCase() }}
                  </v-avatar>
                  <div>
                    <div class="font-bold text-text-primary text-base leading-tight">{{ u.name }}</div>
                    <div class="text-xs text-secondary opacity-70">{{ u.email }}</div>
                  </div>
                </div>
              </td>
              <td class="py-6">
                <v-select
                  :model-value="u.profile"
                  :items="profiles"
                  item-title="name"
                  item-value="id"
                  density="compact"
                  variant="outlined"
                  hide-details
                  bg-color="rgba(var(--v-theme-text-primary), 0.02)"
                  class="role-select max-w-[220px]"
                  :disabled="u.email === authStore.userEmail"
                  @update:model-value="(val) => openRoleChange(u, val as string)"
                ></v-select>
              </td>
              <td class="py-6">
                <BaseChip v-if="u.isPermanentBlocked" color="error" variant="flat" size="small" class="font-bold px-3">BANIDO</BaseChip>
                <BaseChip v-else-if="u.blockedUntil" color="warning" variant="flat" size="small" class="font-bold px-3">SUSPENSO</BaseChip>
                <BaseChip v-else color="success" variant="tonal" size="small" class="font-bold px-3">CONTA ATIVA</BaseChip>
              </td>
              <td class="text-right py-6 pr-8">
                <v-menu location="bottom end">
                  <template v-slot:activator="{ props }">
                    <v-btn icon="mdi-dots-vertical" variant="text" v-bind="props" color="secondary" :disabled="u.email === authStore.userEmail"></v-btn>
                  </template>
                  <v-list density="compact" bg-color="surface" class="border border-all rounded-lg pa-2 elevation-12">
                    <v-list-item prepend-icon="mdi-account-cancel-outline" title="Bloqueio Permanente" class="rounded-sm mb-1" @click="openPunish(u, true)" />
                    <v-list-item prepend-icon="mdi-clock-alert-outline" title="Suspensão Temporária" class="rounded-sm" @click="openPunish(u, false)" />
                    <v-divider v-if="u.isPermanentBlocked || u.blockedUntil" class="my-2" />
                    <v-list-item v-if="u.isPermanentBlocked || u.blockedUntil" prepend-icon="mdi-account-check-outline" title="Reativar Conta" color="success" class="rounded-sm font-bold" @click="unblockUser(u.id)" />
                  </v-list>
                </v-menu>
              </td>
            </tr>
          </tbody>
        </v-table>
      </v-card>

      <!-- Mobile View: Cards -->
      <div v-else class="d-flex flex-column gap-4">
        <v-card v-for="u in users" :key="u.id" variant="outlined" class="pa-6 bg-surface border-all rounded-lg elevation-1">
          <div class="d-flex justify-space-between align-start mb-6">
            <div class="d-flex align-center">
              <v-avatar size="44" color="primary" class="mr-4 text-white font-bold elevation-2">
                {{ u.name.charAt(0).toUpperCase() }}
              </v-avatar>
              <div>
                <div class="font-bold text-text-primary text-lg">{{ u.name }}</div>
                <div class="text-xs text-secondary opacity-70">{{ u.email }}</div>
              </div>
            </div>
            <BaseChip v-if="u.isPermanentBlocked" color="error" size="x-small" variant="flat" class="font-bold">BANIDO</BaseChip>
            <BaseChip v-else-if="u.blockedUntil" color="warning" size="x-small" variant="flat" class="font-bold">SUSPENSO</BaseChip>
            <BaseChip v-else color="success" size="x-small" variant="tonal" class="font-bold">ATIVO</BaseChip>
          </div>

          <div class="d-flex align-center gap-3">
            <v-select
              :model-value="u.profile"
              :items="profiles"
              item-title="name"
              item-value="id"
              label="Alterar Cargo"
              density="compact"
              variant="outlined"
              class="flex-grow-1"
              hide-details
              bg-color="rgba(var(--v-theme-text-primary), 0.02)"
              :disabled="u.email === authStore.userEmail"
              @update:model-value="(val) => openRoleChange(u, val as string)"
            ></v-select>
            
            <v-menu location="top end">
              <template v-slot:activator="{ props }">
                <v-btn icon="mdi-shield-alert-outline" variant="outlined" v-bind="props" color="secondary" :disabled="u.email === authStore.userEmail" height="44" class="rounded-lg border"></v-btn>
              </template>
              <v-list density="compact" bg-color="surface" class="border border-all rounded-lg pa-2">
                <v-list-item prepend-icon="mdi-account-cancel-outline" title="Bloquear Permanente" @click="openPunish(u, true)" />
                <v-list-item prepend-icon="mdi-clock-alert-outline" title="Bloquear Temporário" @click="openPunish(u, false)" />
                <v-list-item v-if="u.isPermanentBlocked || u.blockedUntil" prepend-icon="mdi-account-check-outline" title="Cancelar Punição" @click="unblockUser(u.id)" />
              </v-list>
            </v-menu>
          </div>
        </v-card>
      </div>
    </template>

    <!-- Modais -->
    <v-dialog v-model="showPunishDialog" max-width="500">
      <v-card class="pa-8 pa-md-10 bg-surface border-all elevation-24 rounded-lg">
        <h2 class="text-h5 font-bold mb-8 text-text-primary">Aplicar Punição</h2>
        <div class="text-sm text-secondary mb-6 border pa-4 rounded-lg bg-background-alt">
          Usuário: <span class="font-weight-bold text-text-primary">{{ selectedUser?.name }}</span>
        </div>
        <v-radio-group v-model="punishForm.mode" inline color="primary" class="mb-4"><v-radio label="Temporário" value="temporary"></v-radio><v-radio label="Permanente" value="permanent" v-if="authStore.isAdmin"></v-radio></v-radio-group>
        <v-text-field v-if="punishForm.mode === 'temporary'" v-model="punishForm.days" type="number" label="Dias de Suspensão" variant="outlined" class="mb-6" />
        <v-textarea v-model="punishForm.justification" label="Justificativa" variant="outlined" placeholder="Descreva o motivo..." rows="3" class="mb-8" />
        <div class="d-flex justify-end gap-4"><v-btn variant="text" @click="showPunishDialog = false" class="font-weight-bold px-6">CANCELAR</v-btn><v-btn color="error" @click="confirmPunish" class="font-weight-bold px-8">CONFIRMAR</v-btn></div>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showAdminConfirmDialog" max-width="450">
      <v-card class="pa-8 pa-md-10 bg-surface border-all elevation-24 rounded-lg">
        <div class="text-center mb-6"><v-icon color="warning" size="64">mdi-alert-octagon-outline</v-icon></div>
        <h2 class="text-xl font-bold text-center text-text-primary mb-4">Confirmação de Nível</h2>
        <p class="text-base text-center text-secondary mb-10">Você tem certeza que deseja promover <strong>{{ selectedUser?.name }}</strong> ao cargo de <strong>ADMINISTRADOR</strong>?</p>
        <div class="d-flex justify-center gap-4"><v-btn variant="text" @click="showAdminConfirmDialog = false" class="font-weight-bold px-6">CANCELAR</v-btn><v-btn color="warning" @click="changeRole" class="font-weight-bold px-8 text-black">SIM, PROMOVER</v-btn></div>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">{{ snackbar.text }}</v-snackbar>
  </v-container>
</template>

<style scoped>
.bg-background-alt { background: rgba(var(--v-theme-text-primary), 0.02); }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.hover-bg:hover { background-color: rgba(var(--v-theme-text-primary), 0.015) !important; }
.transition-fast { transition: all 0.2s ease-in-out; }
</style>