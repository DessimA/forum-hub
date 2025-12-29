<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script setup lang="ts">
</script>

<style>
body {
  background-color: #0a0e27;
  background-image: linear-gradient(rgba(10, 14, 39, 0.5), rgba(10, 14, 39, 0.5)), url('@/assets/background.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  min-height: 100vh;
}

.v-application, .v-main {
  background: transparent !important;
}

.v-card {
  background-color: rgba(21, 27, 61, 0.8) !important;
  backdrop-filter: blur(10px);
}

.v-app-bar {
  background-color: rgba(10, 14, 39, 0.8) !important;
  backdrop-filter: blur(5px);
}

::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #0a0e27;
}

::-webkit-scrollbar-thumb {
  background: #00f0ff;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #00d9ff;
}
</style>
