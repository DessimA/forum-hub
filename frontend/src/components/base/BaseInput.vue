<template>
  <v-text-field
    class="rounded-sm"
    variant="outlined"
    density="compact"
    hide-details="auto"
    v-bind="$attrs"
  >
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}" />
    </template>
  </v-text-field>
</template>

<script setup lang="ts">
// Vuetify v-text-field height is roughly 40px with density="compact"
</script>
