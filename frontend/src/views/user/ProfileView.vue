<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const user = ref<any>(null)
const loading = ref(true)
const showEditDialog = ref(false)
const valid = ref(false)
const saving = ref(false)
const snackbar = ref({ show: false, text: '', color: '' })

const form = ref({
  name: '',
  email: '',
  password: ''
})

const showMessage = (text: string, color: string = 'success') => {
  snackbar.value = { show: true, text, color }
}

const fetchProfile = async () => {
  try {
    const response = await api.get('/users/profile')
    user.value = response.data
    form.value.name = response.data.name
    form.value.email = response.data.email
  } catch (error) {
    console.error('Error loading profile', error)
  } finally {
    loading.value = false
  }
}

const handleUpdate = async () => {
  if (!valid.value) return
  saving.value = true
  try {
    const payload: any = { ...form.value }
    if (!payload.password) delete payload.password
    
    await api.put('/users/profile', payload)
    showEditDialog.value = false
    showMessage('Perfil atualizado com sucesso!')
    await fetchProfile()
  } catch (error) {
    showMessage('Erro ao atualizar perfil.', 'error')
  } finally {
    saving.value = false
  }
}

onMounted(fetchProfile)
</script>

<template>
  <v-container class="py-10 py-md-16 px-4 px-md-12 max-w-[1400px] mx-auto">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8" xl="6">
        <div v-if="loading" class="text-center py-16">
          <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
        </div>

        <v-card variant="outlined" class="bg-surface border-all pa-8 pa-md-16 rounded-lg elevation-2" v-else-if="user">
          <div class="text-center mb-12">
            <v-avatar size="140" color="primary" class="mb-8 elevation-8 border-4 border-surface">
              <span class="text-h2 font-weight-bold text-white">{{ user.name.charAt(0).toUpperCase() }}</span>
            </v-avatar>
            <h1 class="text-h3 font-weight-bold text-text-primary mb-2 leading-tight">{{ user.name }}</h1>
            <p class="text-lg text-secondary opacity-70">{{ user.email }}</p>
          </div>

          <v-divider class="mb-12 opacity-10"></v-divider>

          <div class="d-flex flex-column gap-6">
            <div class="d-flex align-center pa-6 rounded-lg bg-background-alt border-all transition-fast hover-lift">
              <v-avatar color="rgba(var(--v-theme-primary), 0.1)" size="56" class="mr-6">
                <v-icon color="primary" size="32">mdi-shield-account-outline</v-icon>
              </v-avatar>
              <div>
                <div class="text-[11px] text-secondary uppercase font-bold tracking-widest mb-1 opacity-60">Nível de Acesso</div>
                <div class="text-xl font-weight-bold text-text-primary">{{ user.profile }}</div>
              </div>
            </div>

            <div class="d-flex align-center pa-6 rounded-lg bg-background-alt border-all transition-fast hover-lift">
              <v-avatar color="rgba(var(--v-theme-primary), 0.1)" size="56" class="mr-6">
                <v-icon color="primary" size="32">mdi-calendar-clock-outline</v-icon>
              </v-avatar>
              <div>
                <div class="text-[11px] text-secondary uppercase font-bold tracking-widest mb-1 opacity-60">Data de Registro</div>
                <div class="text-xl font-weight-bold text-text-primary">{{ new Date(user.createdAt).toLocaleDateString('pt-BR', { dateStyle: 'long' }) }}</div>
              </div>
            </div>
          </div>

          <v-btn 
            color="primary" 
            variant="flat" 
            block 
            class="mt-16 font-weight-bold text-black rounded-pill" 
            height="56"
            prepend-icon="mdi-pencil-outline" 
            @click="showEditDialog = true"
          >
            EDITAR MEUS DADOS
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <!-- Edit Dialog -->
    <v-dialog v-model="showEditDialog" max-width="550">
      <v-card class="pa-8 pa-md-10 bg-surface border-all elevation-24 rounded-lg">
        <h2 class="text-h5 font-weight-bold mb-8 text-text-primary">Atualizar Informações</h2>
        
        <v-form v-model="valid" @submit.prevent="handleUpdate">
          <div class="d-flex flex-column gap-6">
            <v-text-field 
              v-model="form.name" 
              label="Nome Completo" 
              variant="outlined" 
              bg-color="rgba(var(--v-theme-text-primary), 0.02)"
              :rules="[v => !!v || 'Nome é obrigatório']" 
            />
            <v-text-field 
              v-model="form.email" 
              label="E-mail de Acesso" 
              variant="outlined" 
              bg-color="rgba(var(--v-theme-text-primary), 0.02)"
              :rules="[v => !!v || 'E-mail é obrigatório']" 
            />
            <v-text-field 
              v-model="form.password" 
              label="Alterar Senha" 
              type="password" 
              variant="outlined" 
              bg-color="rgba(var(--v-theme-text-primary), 0.02)"
              placeholder="Mantenha em branco para não alterar" 
            />
          </div>

          <div class="d-flex justify-end gap-4 mt-12">
            <v-btn variant="text" @click="showEditDialog = false" class="font-weight-bold px-6">CANCELAR</v-btn>
            <v-btn 
              type="submit" 
              color="primary" 
              :loading="saving" 
              :disabled="!valid" 
              class="font-weight-bold px-10 text-black rounded-sm"
            >
              SALVAR MUDANÇAS
            </v-btn>
          </div>
        </v-form>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.bg-background-alt {
  background: rgba(var(--v-theme-text-primary), 0.02);
}
.gap-6 { gap: 24px; }
.gap-4 { gap: 16px; }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.hover-lift:hover {
  transform: translateY(-4px);
  background-color: rgba(var(--v-theme-primary), 0.03) !important;
  border-color: rgba(var(--v-theme-primary), 0.3) !important;
}
.transition-fast { transition: all 0.2s ease-in-out; }
</style>
