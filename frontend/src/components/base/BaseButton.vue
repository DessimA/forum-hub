<template>
  <v-btn
    class="rounded-sm transition-all hover-scale"
    :height="compact ? 36 : 40"
    v-bind="$attrs"
  >
    <slot></slot>
  </v-btn>
</template>

<script setup lang="ts">
defineProps<{
  compact?: boolean
}>()
</script>
