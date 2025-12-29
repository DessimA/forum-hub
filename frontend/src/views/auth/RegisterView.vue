<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const nome = ref('')
const email = ref('')
const senha = ref('')
const error = ref('')
const loading = ref(false)
const valid = ref(false)

const handleRegister = async () => {
  if (!valid.value) return
  loading.value = true
  error.value = ''
  try {
    await api.post('/auth/register', {
      nome: nome.value,
      email: email.value,
      senha: senha.value
    })
    router.push('/login')
  } catch (e: any) {
    error.value = e.response?.status === 409 
      ? 'Este e-mail já está cadastrado.' 
      : 'Erro ao realizar cadastro. Tente novamente.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-8 neon-border" color="surface" elevation="24">
          <div class="text-center mb-8">
            <h1 class="text-h3 font-weight-bold neon-text">FórumHub</h1>
            <p class="text-subtitle-1 text-grey-lighten-1">Crie sua conta e participe</p>
          </div>

          <v-form v-model="valid" @submit.prevent="handleRegister">
            <v-text-field
              v-model="nome"
              label="Nome Completo"
              prepend-inner-icon="mdi-account"
              variant="outlined"
              color="primary"
              class="mb-4"
              :rules="[v => !!v || 'Nome é obrigatório']"
              required
            ></v-text-field>

            <v-text-field
              v-model="email"
              label="E-mail"
              prepend-inner-icon="mdi-email"
              variant="outlined"
              color="primary"
              class="mb-4"
              :rules="[v => !!v || 'E-mail é obrigatório', v => /.+@.+\..+/.test(v) || 'E-mail inválido']"
              required
            ></v-text-field>

            <v-text-field
              v-model="senha"
              label="Senha (mín. 6 caracteres)"
              prepend-inner-icon="mdi-lock"
              type="password"
              variant="outlined"
              color="primary"
              class="mb-6"
              :rules="[v => !!v || 'Senha é obrigatória', v => v.length >= 6 || 'Mínimo 6 caracteres']"
              required
            ></v-text-field>

            <v-alert v-if="error" type="error" variant="tonal" class="mb-6">
              {{ error }}
            </v-alert>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              :loading="loading"
              :disabled="!valid"
              class="neon-button"
            >
              Cadastrar
            </v-btn>
          </v-form>

          <div class="text-center mt-8">
            <span class="text-grey">Já tem uma conta?</span>
            <v-btn variant="text" color="secondary" class="ml-2" @click="router.push('/login')">Entrar</v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.login-bg { background: transparent; }
.neon-border { border: 1px solid #00f0ff !important; box-shadow: 0 0 20px rgba(0, 240, 255, 0.2) !important; }
.neon-text { color: #00f0ff; text-shadow: 0 0 10px rgba(0, 240, 255, 0.5); }
.neon-button { background-color: transparent !important; border: 1px solid #00f0ff !important; color: #00f0ff !important; transition: all 0.3s ease; }
.neon-button:hover { background-color: #00f0ff !important; color: #0a0e27 !important; box-shadow: 0 0 15px #00f0ff; }
</style>
