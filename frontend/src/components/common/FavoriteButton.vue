<script setup lang="ts">
import { computed } from 'vue'
import { useFavoriteStore } from '@/stores/favoriteStore'

const props = defineProps<{
  topicId: string
}>()

const favoriteStore = useFavoriteStore()
const isFavorited = computed(() => favoriteStore.isFavorited(props.topicId))

const toggleFavorite = async () => {
  if (isFavorited.value) {
    await favoriteStore.removeFavorite(props.topicId)
  } else {
    await favoriteStore.addFavorite(props.topicId)
  }
}
</script>

<template>
  <v-btn
    :icon="isFavorited ? 'mdi-star' : 'mdi-star-outline'"
    :color="isFavorited ? 'yellow-darken-2' : 'grey'"
    variant="text"
    @click.stop="toggleFavorite"
    title="Favoritar"
  ></v-btn>
</template>
