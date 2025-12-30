<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const user = ref<any>(null)
const loading = ref(true)
const showEditDialog = ref(false)
const valid = ref(false)
const saving = ref(false)

const form = ref({
  name: '',
  email: '',
  password: ''
})

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
    await fetchProfile()
    alert('Perfil atualizado com sucesso!')
  } catch (error) {
    alert('Erro ao atualizar perfil.')
  } finally {
    saving.value = false
  }
}

onMounted(fetchProfile)
</script>

<template>
  <v-container class="py-10">
    <v-row justify="center">
      <v-col cols="12" md="8">
        <v-card color="surface" class="pa-8 border-subtle" variant="flat">
          <div class="text-center mb-8" v-if="!loading && user">
            <v-avatar size="120" color="primary" class="mb-4" variant="tonal">
              <v-icon size="64">mdi-account-outline</v-icon>
            </v-avatar>
            <h1 class="text-h4 font-weight-bold text-white">{{ user.name }}</h1>
            <p class="text-grey-lighten-1">{{ user.email }}</p>
          </div>

          <v-divider class="mb-8" style="opacity: 0.05"></v-divider>

          <v-list bg-color="transparent" v-if="user" theme="dark">
            <v-list-item prepend-icon="mdi-shield-account-outline" title="Cargo" :subtitle="user.profile"></v-list-item>
            <v-list-item prepend-icon="mdi-calendar-outline" title="Membro desde" :subtitle="new Date(user.createdAt).toLocaleDateString()"></v-list-item>
          </v-list>

          <v-btn color="primary" variant="outlined" class="mt-8" block prepend-icon="mdi-pencil-outline" @click="showEditDialog = true">
            Editar Perfil
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="showEditDialog" max-width="500px">
      <v-card color="surface" class="pa-4 border-subtle">
        <v-card-title class="text-white">Editar Meus Dados</v-card-title>
        <v-card-text>
          <v-form v-model="valid">
            <v-text-field v-model="form.name" label="Nome" variant="outlined" color="primary" :rules="[v => !!v || 'Obrigatório']"></v-text-field>
            <v-text-field v-model="form.email" label="E-mail" variant="outlined" color="primary" :rules="[v => !!v || 'Obrigatório']"></v-text-field>
            <v-text-field v-model="form.password" label="Nova Senha (deixe em branco para manter)" type="password" variant="outlined" color="primary"></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey" @click="showEditDialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" :loading="saving" :disabled="!valid" @click="handleUpdate">Salvar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
</style>