<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const error = ref('')
const showPassword = ref(false)

const handleLogin = async () => {
  try {
    error.value = ''
    await authStore.login({ email: email.value, password: password.value })
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
        <v-card class="pa-8 soft-border" color="surface" elevation="12">
          <div class="text-center mb-8">
            <h1 class="text-h3 font-weight-bold text-white mb-2">Fórum<span class="text-primary">Hub</span></h1>
            <p class="text-subtitle-1 text-grey-lighten-1">Onde o conhecimento se encontra</p>
          </div>

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="E-mail"
              prepend-inner-icon="mdi-email-outline"
              variant="outlined"
              color="primary"
              class="mb-4"
              required
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Senha"
              prepend-inner-icon="mdi-lock-outline"
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
              variant="flat"
              :loading="authStore.loading"
              class="font-weight-bold"
            >
              Entrar
            </v-btn>
          </v-form>

          <div class="text-center mt-8">
            <span class="text-grey">Não tem uma conta?</span>
            <v-btn variant="text" color="primary" class="ml-2" @click="router.push('/register')">Registre-se</v-btn>
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
  border-top: 2px solid #00f0ff !important; /* Neon usado como guia/destaque sutil */
}

.text-primary {
  color: #00f0ff !important;
}
</style>
