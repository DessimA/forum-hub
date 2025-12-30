<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const name = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const valid = ref(false)

const handleRegister = async () => {
  if (!valid.value) return
  loading.value = true
  error.value = ''
  try {
    await api.post('/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value
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
        <v-card class="pa-8 soft-border" color="surface" elevation="12">
          <div class="text-center mb-8">
            <h1 class="text-h3 font-weight-bold text-white mb-2">Fórum<span class="text-primary">Hub</span></h1>
            <p class="text-subtitle-1 text-grey-lighten-1">Crie sua conta e participe</p>
          </div>

          <v-form v-model="valid" @submit.prevent="handleRegister">
            <v-text-field
              v-model="name"
              label="Nome Completo"
              prepend-inner-icon="mdi-account-outline"
              variant="outlined"
              color="primary"
              class="mb-4"
              :rules="[v => !!v || 'Nome é obrigatório']"
              required
            ></v-text-field>

            <v-text-field
              v-model="email"
              label="E-mail"
              prepend-inner-icon="mdi-email-outline"
              variant="outlined"
              color="primary"
              class="mb-4"
              :rules="[v => !!v || 'E-mail é obrigatório', v => /.+@.+\..+/.test(v) || 'E-mail inválido']"
              required
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Senha (mín. 6 caracteres)"
              prepend-inner-icon="mdi-lock-outline"
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
              variant="flat"
              :loading="loading"
              :disabled="!valid"
              class="font-weight-bold"
            >
              Cadastrar
            </v-btn>
          </v-form>

          <div class="text-center mt-8">
            <span class="text-grey">Já tem uma conta?</span>
            <v-btn variant="text" color="primary" class="ml-2" @click="router.push('/login')">Entrar</v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.login-bg {
  background: transparent !important;
}

.soft-border {
  border: 1px solid rgba(255, 255, 255, 0.05) !important;
  border-top: 2px solid #00f0ff !important;
}

.text-primary {
  color: #00f0ff !important;
}
</style>
