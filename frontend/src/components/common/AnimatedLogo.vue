<template>
  <div class="animated-logo d-flex align-center" :style="{ fontSize: size + 'px' }">
    <!-- Animated Hexagon Icon -->
    <svg 
      class="logo-icon mr-2" 
      viewBox="0 0 100 100" 
      :style="{ width: (size * 1.2) + 'px', height: (size * 1.2) + 'px' }"
    >
      <path 
        class="hexagon-path" 
        d="M50 5 L90 27.5 L90 72.5 L50 95 L10 72.5 L10 27.5 Z" 
        fill="none" 
        stroke="currentColor" 
        stroke-width="6"
      />
      <circle cx="50" cy="50" r="10" fill="currentColor" class="center-dot" />
    </svg>

    <!-- Animated Text -->
    <div class="logo-text d-flex align-center">
      <span class="text-forum">Fórum</span>
      <span class="text-hub ml-1">Hub</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  size: number
}>()
</script>

<style scoped>
.animated-logo {
  user-select: none;
  font-family: 'Inter', sans-serif;
  color: rgb(var(--v-theme-text-primary));
}

.text-forum {
  font-weight: 400;
}

.text-hub {
  font-weight: 800;
  background: linear-gradient(90deg, #38BDF8 0%, #10B981 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: gradientSlide 3s linear infinite;
  will-change: background-position;
}

.logo-icon {
  color: rgb(var(--v-theme-primary));
}

.hexagon-path {
  stroke-dasharray: 300;
  stroke-dashoffset: 300;
  animation: strokeDraw 3s ease-in-out infinite;
}

.center-dot {
  opacity: 0;
  animation: fadeInOut 3s ease-in-out infinite;
}

@keyframes gradientSlide {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes strokeDraw {
  0%, 100% { stroke-dashoffset: 300; }
  50% { stroke-dashoffset: 0; }
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  50% { opacity: 1; transform: scale(1.2); }
}
</style>
