<template>
  <v-card
    class="flat-card transition-all"
    variant="flat"
    :color="color"
    v-bind="$attrs"
  >
    <slot></slot>
  </v-card>
</template>

<script setup lang="ts">
defineProps<{
  color?: string
}>()
</script>
