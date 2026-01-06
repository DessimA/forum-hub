<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import AnimatedLogo from '@/components/common/AnimatedLogo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'

const authStore = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  if (!email.value || !password.value) return
  
  loading.value = true
  error.value = ''
  try {
    await authStore.login({ email: email.value, password: password.value })
    router.push('/topics')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Falha na autenticação. Verifique suas credenciais.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="fill-height auth-container px-4">
    <div class="w-100 mx-auto auth-card-wrapper" style="max-width: 440px;">
      <!-- Top Logo -->
      <div class="text-center mb-12">
        <div class="d-flex justify-center mb-6">
          <AnimatedLogo :size="48" @click="router.push('/')" class="cursor-pointer" />
        </div>
        <h1 class="text-h4 font-weight-bold text-text-primary mb-2">Bem-vindo de volta</h1>
        <p class="text-text-secondary text-base opacity-70">Acesse sua conta para participar da comunidade</p>
      </div>

      <v-card class="auth-card border-all rounded-lg pa-8 pa-md-12 position-relative elevation-12 overflow-hidden">
        <!-- Decoration -->
        <div class="auth-decoration"></div>

        <v-form @submit.prevent="handleLogin" class="d-flex flex-column gap-6 mt-4">
          <v-text-field
            v-model="email"
            label="E-mail"
            placeholder="seu@email.com"
            prepend-inner-icon="mdi-email-outline"
            type="email"
            required
            variant="outlined"
            density="default"
            class="auth-input"
            data-cy="login-email"
          />

          <div class="d-flex flex-column">
            <v-text-field
              v-model="password"
              label="Senha"
              placeholder="••••••••"
              prepend-inner-icon="mdi-lock-outline"
              :type="showPassword ? 'text' : 'password'"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              required
              variant="outlined"
              density="default"
              class="auth-input"
              data-cy="login-password"
            />
            <div class="text-right mt-2">
              <a href="#" class="text-primary text-xs font-weight-bold opacity-80 hover:opacity-100">Esqueceu a senha?</a>
            </div>
          </div>

          <v-alert v-if="error" type="error" variant="tonal" class="text-sm mb-2 shake-anim" density="compact">
            {{ error }}
          </v-alert>

          <v-btn
            type="submit"
            color="primary"
            block
            height="56"
            :loading="loading"
            class="font-weight-bold auth-submit-btn mt-4 text-black text-lg"
            data-cy="login-submit"
          >
            ENTRAR NO SISTEMA
          </v-btn>
        </v-form>
      </v-card>

      <div class="text-center mt-12">
        <p class="text-text-secondary text-base">
          Ainda não faz parte? 
          <router-link to="/register" class="text-primary font-weight-bold ml-1">Crie sua conta agora</router-link>
        </p>
      </div>
    </div>

    <!-- Floating Theme Toggle for Auth Pages -->
    <div class="theme-toggle-floating">
      <ThemeToggle />
    </div>
  </v-container>
</template>

<style scoped>
.auth-container {
  background: radial-gradient(circle at top right, rgba(var(--v-theme-primary), 0.08) 0%, transparent 60%);
}

.auth-card-wrapper {
  animation: fadeIn 0.6s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.auth-card {
  background: rgba(var(--v-theme-surface), 0.9) !important;
  backdrop-filter: blur(16px);
  border: 1px solid rgba(var(--v-border-color), 0.1) !important;
}

.auth-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, rgb(var(--v-theme-primary)), transparent);
}

.theme-toggle-floating {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 100;
}

.gap-6 { gap: 24px; }

.auth-input :deep(.v-field) {
  transition: all 0.2s ease;
}

.auth-submit-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  border-radius: 4px !important;
}

.auth-submit-btn:hover {
  filter: brightness(1.1);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px -4px rgba(var(--v-theme-primary), 0.4);
}

.shake-anim {
  animation: shake 0.4s cubic-bezier(.36,.07,.19,.97) both;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}

.cursor-pointer { cursor: pointer; }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
</style>
