<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import AnimatedLogo from '@/components/common/AnimatedLogo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'

const authStore = useAuthStore()
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

const passwordStrength = computed(() => {
  if (!password.value) return 0
  let strength = 0
  if (password.value.length >= 8) strength++
  if (/[A-Z]/.test(password.value)) strength++
  if (/[0-9]/.test(password.value)) strength++
  if (/[^A-Za-z0-9]/.test(password.value)) strength++
  return strength
})

const strengthColor = computed(() => {
  const colors = ['error', 'error', 'warning', 'success', 'success']
  return colors[passwordStrength.value]
})

const passwordsMatch = computed(() => {
  return confirmPassword.value === password.value && confirmPassword.value !== ''
})

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error.value = 'As senhas não coincidem'
    return
  }
  
  loading.value = true
  error.value = ''
  try {
    await authStore.register(name.value, email.value, password.value)
    router.push('/topics')
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Erro ao criar conta. Tente novamente.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <v-container class="fill-height auth-container px-4">
    <div class="w-100 mx-auto auth-card-wrapper" style="max-width: 480px;">
      <!-- Top Logo -->
      <div class="text-center mb-12">
        <div class="d-flex justify-center mb-6">
          <AnimatedLogo :size="48" @click="router.push('/')" class="cursor-pointer" />
        </div>
        <h1 class="text-h4 font-weight-bold text-text-primary mb-2">Crie sua conta</h1>
        <p class="text-text-secondary text-base opacity-70">Junte-se à comunidade oficial do FórumHub</p>
      </div>

      <v-card class="auth-card border-all rounded-lg pa-8 pa-md-12 position-relative elevation-12 overflow-hidden">
        <!-- Decoration -->
        <div class="auth-decoration"></div>

        <v-form @submit.prevent="handleRegister" class="d-flex flex-column gap-6 mt-4">
          <v-text-field
            v-model="name"
            label="Nome Completo"
            placeholder="Ex: José Silva"
            prepend-inner-icon="mdi-account-outline"
            required
            variant="outlined"
            density="default"
            class="auth-input"
            data-cy="register-name"
          />

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
            data-cy="register-email"
          />

          <div class="d-flex flex-column">
            <v-text-field
              v-model="password"
              label="Senha"
              placeholder="••••••••"
              prepend-inner-icon="mdi-lock-outline"
              type="password"
              required
              variant="outlined"
              density="default"
              class="auth-input mb-2"
              data-cy="register-password"
            />
            <!-- Strength Meter -->
            <div class="d-flex gap-1 mb-1 px-1">
              <div 
                v-for="i in 4" 
                :key="i" 
                class="strength-segment flex-grow-1"
                :class="{ 'filled': i <= passwordStrength, [strengthColor]: i <= passwordStrength }"
              ></div>
            </div>
            <p class="text-[10px] text-secondary text-right uppercase font-bold tracking-tighter">Segurança da Senha</p>
          </div>

          <v-text-field
            v-model="confirmPassword"
            label="Confirmar Senha"
            placeholder="••••••••"
            prepend-inner-icon="mdi-lock-check-outline"
            type="password"
            required
            variant="outlined"
            density="default"
            class="auth-input"
            :class="{ 'match-success': passwordsMatch, 'match-error': confirmPassword && !passwordsMatch }"
            data-cy="register-confirm-password"
          >
            <template v-slot:append-inner>
              <transition name="scale">
                <v-icon v-if="passwordsMatch" color="success" size="20">mdi-check-circle</v-icon>
                <v-icon v-else-if="confirmPassword && !passwordsMatch" color="error" size="20">mdi-close-circle</v-icon>
              </transition>
            </template>
          </v-text-field>

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
            data-cy="register-submit"
          >
            CRIAR MINHA CONTA
          </v-btn>
        </v-form>
      </v-card>

      <div class="text-center mt-12">
        <p class="text-text-secondary text-base">
          Já tem conta? 
          <router-link to="/login" class="text-primary font-weight-bold ml-1">Acesse agora</router-link>
        </p>
      </div>
    </div>

    <!-- Floating Theme Toggle -->
    <div class="theme-toggle-floating">
      <ThemeToggle />
    </div>
  </v-container>
</template>

<style scoped>
.auth-container {
  background: radial-gradient(circle at top left, rgba(var(--v-theme-primary), 0.08) 0%, transparent 60%);
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
.gap-1 { gap: 4px; }

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

.match-success :deep(.v-field__outline) {
  --v-field-border-color: rgb(var(--v-theme-success)) !important;
}

.match-error :deep(.v-field__outline) {
  --v-field-border-color: rgb(var(--v-theme-error)) !important;
}

.strength-segment {
  height: 4px;
  background: rgba(var(--v-border-color), 0.1);
  border-radius: 2px;
  transition: all 0.3s ease;
}

.strength-segment.filled.error { background: rgb(var(--v-theme-error)) !important; }
.strength-segment.filled.warning { background: rgb(var(--v-theme-warning)) !important; }
.strength-segment.filled.success { background: rgb(var(--v-theme-success)) !important; }

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

.scale-enter-active, .scale-leave-active { transition: all 0.2s ease; }
.scale-enter-from, .scale-leave-to { transform: scale(0); opacity: 0; }

.cursor-pointer { cursor: pointer; }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
</style>
