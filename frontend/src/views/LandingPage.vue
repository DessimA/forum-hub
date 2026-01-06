<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'
import { useDisplay } from 'vuetify'
import AnimatedLogo from '@/components/common/AnimatedLogo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'

const { mobile } = useDisplay()
const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/topics')
  }
})

const features = [
  {
    icon: 'mdi-forum-outline',
    title: 'Discussões Organizadas',
    description: 'Categorize suas dúvidas por curso e status, facilitando a busca por soluções.'
  },
  {
    icon: 'mdi-shield-check-outline',
    title: 'Moderação Proativa',
    description: 'Equipe dedicada para manter a qualidade e o respeito na comunidade.'
  },
  {
    icon: 'mdi-history',
    title: 'Auditoria Completa',
    description: 'Transparência em todas as ações administrativas para sua segurança.'
  }
]
</script>

<template>
  <div class="landing-page">
    <!-- Fixed Theme Toggle -->
    <div class="theme-toggle-wrapper">
      <ThemeToggle />
    </div>

    <!-- Hero Section -->
    <section class="hero-section d-flex flex-column align-center justify-center text-center pa-6">
      <div class="hero-content">
        <div class="d-flex justify-center mb-8">
          <AnimatedLogo :size="mobile ? 40 : 64" />
        </div>
        
        <p class="tagline text-uppercase tracking-widest mb-4">
          Conectando conhecimento, construindo comunidade
        </p>
        
        <h1 class="text-h4 text-md-h2 font-weight-bold text-text-primary mb-6">
          A plataforma oficial de<br/>aprendizado colaborativo
        </h1>
        
        <p class="hero-subtitle mx-auto mb-10">
          O FórumHub permite que alunos e instrutores compartilhem conhecimento de forma organizada e segura, acelerando o aprendizado de todos.
        </p>

        <div class="d-flex flex-column flex-sm-row justify-center gap-6">
          <v-btn
            variant="flat"
            color="primary"
            height="48"
            class="px-10 font-weight-bold landing-btn"
            @click="router.push('/register')"
          >
            Criar Conta
          </v-btn>
          <v-btn
            variant="outlined"
            color="secondary"
            height="48"
            class="px-10 font-weight-bold landing-btn"
            @click="router.push('/login')"
          >
            Entrar
          </v-btn>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section py-16 px-6 bg-background-alt">
      <div class="container-responsive">
        <v-row>
          <v-col v-for="feat in features" :key="feat.title" cols="12" md="4">
            <v-card variant="outlined" class="feature-card h-100 pa-8 border transition-fast">
              <v-icon :icon="feat.icon" size="40" color="primary" class="mb-6" />
              <h3 class="text-h6 font-weight-bold mb-3">{{ feat.title }}</h3>
              <p class="text-body-2 text-text-secondary leading-relaxed">{{ feat.description }}</p>
            </v-card>
          </v-col>
        </v-row>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section py-16 text-center bg-surface border-t border-b">
      <div class="pa-6">
        <h2 class="text-h5 text-md-h4 font-weight-bold mb-2">Pronto para começar?</h2>
        <p class="text-text-secondary mb-8">Junte-se a milhares de alunos agora mesmo.</p>
        <v-btn
          color="primary"
          size="large"
          height="52"
          class="px-12 font-weight-bold rounded-pill shadow-primary"
          @click="router.push('/register')"
        >
          Cadastre-se Gratuitamente
        </v-btn>
      </div>
    </section>

    <!-- Footer -->
    <footer class="py-12 text-center text-text-secondary text-caption">
      <p>&copy; 2025 FórumHub &bull; Desenvolvido para Alura</p>
    </footer>
  </div>
</template>

<style scoped>
.landing-page {
  position: relative;
  min-height: 100vh;
}

.gap-6 { gap: 24px; }

.theme-toggle-wrapper {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 100;
}

.hero-section {
  min-height: 100vh;
  background: radial-gradient(circle at center, rgba(var(--v-theme-primary), 0.08) 0%, transparent 70%);
}

.hero-content {
  animation: slideUp 0.8s cubic-bezier(0.2, 0.8, 0.2, 1);
  max-width: 900px;
}

.tagline {
  font-size: 14px;
  font-weight: 600;
  color: #94A3B8;
  letter-spacing: 0.2em !important;
}

@media (min-width: 768px) {
  .tagline {
    font-size: 18px;
  }
}

.hero-subtitle {
  font-size: 14px;
  line-height: 1.6;
  color: #64748B;
  max-width: 600px;
}

@media (min-width: 768px) {
  .hero-subtitle {
    font-size: 16px;
  }
}

.bg-background-alt {
  background: rgba(var(--v-theme-surface), 0.3);
}

.feature-card {
  background: rgba(var(--v-theme-surface), 0.5) !important;
  backdrop-filter: blur(8px);
  border-radius: 16px !important;
}

.feature-card:hover {
  transform: translateY(-8px);
  border-color: rgb(var(--v-theme-primary)) !important;
  box-shadow: 0 20px 40px -20px rgba(var(--v-theme-primary), 0.3);
}

.landing-btn {
  transition: all 0.3s ease !important;
  border-radius: 8px !important;
}

.landing-btn:hover {
  transform: scale(1.05);
  filter: brightness(1.1);
}

.shadow-primary {
  box-shadow: 0 8px 24px rgba(var(--v-theme-primary), 0.3);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .hero-section {
    min-height: auto;
    padding-top: 120px !important;
    padding-bottom: 80px !important;
  }
}
</style>