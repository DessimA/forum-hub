<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const senha = ref('')
const error = ref('')
const showPassword = ref(false)

const handleLogin = async () => {
  try {
    error.value = ''
    await authStore.login({ email: email.value, senha: senha.value })
    router.push('/')
  } catch (e: any) {
    error.value = 'E-mail ou senha inválidos'
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
            <p class="text-subtitle-1 text-grey-lighten-1">Conectando mentes na velocidade da luz</p>
          </div>

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="E-mail"
              prepend-inner-icon="mdi-email"
              variant="outlined"
              color="primary"
              class="mb-4"
              required
            ></v-text-field>

            <v-text-field
              v-model="senha"
              label="Senha"
              prepend-inner-icon="mdi-lock"
              :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
              :type="showPassword ? 'text' : 'password'"
              variant="outlined"
              color="primary"
              @click:append-inner="showPassword = !showPassword"
              class="mb-6"
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
              :loading="authStore.loading"
              class="neon-button"
            >
              Entrar
            </v-btn>
          </v-form>

          <div class="text-center mt-8">
            <span class="text-grey">Não tem uma conta?</span>
            <v-btn variant="text" color="secondary" class="ml-2" @click="router.push('/register')">Registre-se</v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.login-bg {
  background: transparent;
}

.neon-border {
  border: 1px solid #00f0ff !important;
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.2) !important;
}

.neon-text {
  color: #00f0ff;
  text-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
}

.neon-button {
  background-color: transparent !important;
  border: 1px solid #00f0ff !important;
  color: #00f0ff !important;
  transition: all 0.3s ease;
}

.neon-button:hover {
  background-color: #00f0ff !important;
  color: #0a0e27 !important;
  box-shadow: 0 0 15px #00f0ff;
}
</style>
