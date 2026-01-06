<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useCourseStore } from '@/stores/course'
import BaseChip from '@/components/base/BaseChip.vue'
import { useDisplay } from 'vuetify'

const { mdAndUp } = useDisplay()
const courseStore = useCourseStore()
const dialog = ref(false)
const confirmDeleteDialog = ref(false)
const selectedId = ref<string | null>(null)
const valid = ref(false)
const page = ref(1)
const editedId = ref<string | null>(null)

const editedItem = reactive({
  name: '',
  category: ''
})

const fetchPage = (p: number) => {
  courseStore.fetchCourses(p - 1)
}

const openDialog = (item?: any) => {
  if (item) {
    editedId.value = item.id
    editedItem.name = item.name
    editedItem.category = item.category
  } else {
    editedId.value = null
    editedItem.name = ''
    editedItem.category = ''
  }
  dialog.value = true
}

const save = async () => {
  if (!valid.value) return
  if (editedId.value) {
    await courseStore.updateCourse(editedId.value, { ...editedItem })
  } else {
    await courseStore.createCourse({ ...editedItem })
  }
  dialog.value = false
}

const openDeleteConfirm = (id: string) => {
  selectedId.value = id
  confirmDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (selectedId.value) {
    await courseStore.deleteCourse(selectedId.value)
  }
  confirmDeleteDialog.value = false
  selectedId.value = null
}

onMounted(() => {
  courseStore.fetchCourses(0)
})
</script>

<template>
  <v-container class="py-10 py-md-16 px-4 px-md-12 max-w-[1400px] mx-auto">
    <header class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center mb-12 mb-md-16 px-1 gap-6">
      <div>
        <h1 class="text-responsive-h1 text-text-primary font-weight-bold mb-2">Gestão de Cursos</h1>
        <p class="text-base text-text-secondary opacity-70">Gerencie as trilhas de conhecimento do fórum</p>
      </div>
      <v-btn color="primary" variant="flat" height="48" prepend-icon="mdi-plus" class="font-weight-bold text-black rounded-pill px-8" @click="openDialog()">
        NOVO CURSO
      </v-btn>
    </header>

    <div v-if="courseStore.loading" class="text-center py-16">
      <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
    </div>

    <template v-else>
      <!-- Desktop View: Table -->
      <v-card v-if="mdAndUp" variant="outlined" class="bg-surface border-all rounded-lg overflow-hidden elevation-2">
        <v-table hover class="bg-transparent">
          <thead>
            <tr class="bg-background-alt border-b">
              <th class="text-xs uppercase font-bold text-secondary py-6 pl-8">ID Reduzido</th>
              <th class="text-xs uppercase font-bold text-secondary py-6">Nome do Curso</th>
              <th class="text-xs uppercase font-bold text-secondary py-6">Categoria</th>
              <th class="text-right text-xs uppercase font-bold text-secondary py-6 pr-8">Ações</th>
            </tr>
          </thead>
          <tbody class="text-sm">
            <tr v-if="courseStore.courses.length === 0">
              <td colspan="4" class="text-center py-16 text-secondary text-lg">Nenhum curso cadastrado ainda.</td>
            </tr>
            <tr v-for="course in courseStore.courses" :key="course.id" class="border-b last:border-0 hover-bg transition-fast">
              <td class="py-6 pl-8 font-mono text-[11px] text-secondary opacity-60">{{ course.id.substring(0, 8) }}</td>
              <td class="py-6 font-bold text-text-primary text-base">{{ course.name }}</td>
              <td class="py-6">
                <BaseChip variant="tonal" color="secondary" class="font-bold px-3">
                  {{ course.category }}
                </BaseChip>
              </td>
              <td class="text-right py-6 pr-8">
                <v-btn icon="mdi-pencil-outline" variant="text" size="small" color="primary" class="mr-2" @click="openDialog(course)" />
                <v-btn icon="mdi-delete-outline" variant="text" size="small" color="error" @click="openDeleteConfirm(course.id)" />
              </td>
            </tr>
          </tbody>
        </v-table>
        <div class="pa-6 d-flex justify-center border-t bg-background-alt">
          <v-pagination v-model="page" :length="courseStore.totalPages" :total-visible="5" density="comfortable" active-color="primary" @update:model-value="fetchPage" />
        </div>
      </v-card>

      <!-- Mobile View -->
      <div v-else class="d-flex flex-column gap-4">
        <v-card v-for="course in courseStore.courses" :key="course.id" variant="outlined" class="pa-6 bg-surface border-all rounded-lg elevation-1">
          <div class="d-flex justify-space-between align-start mb-4">
            <span class="text-[10px] font-mono text-secondary opacity-50">ID: {{ course.id.substring(0, 8) }}</span>
            <BaseChip variant="tonal" color="secondary" size="x-small" class="font-bold">{{ course.category }}</BaseChip>
          </div>
          <h3 class="text-lg font-bold text-text-primary mb-6">{{ course.name }}</h3>
          <div class="d-flex justify-end gap-3 border-t pt-4">
            <v-btn variant="outlined" size="small" color="primary" prepend-icon="mdi-pencil" class="px-4" @click="openDialog(course)">EDITAR</v-btn>
            <v-btn variant="outlined" size="small" color="error" prepend-icon="mdi-delete" class="px-4" @click="openDeleteConfirm(course.id)">EXCLUIR</v-btn>
          </div>
        </v-card>
      </div>
    </template>

    <!-- Modais -->
    <v-dialog v-model="dialog" max-width="550">
      <v-card class="pa-8 pa-md-10 bg-surface border-all elevation-24 rounded-lg">
        <h2 class="text-h5 font-bold mb-8 text-text-primary">{{ editedId ? 'Editar Curso' : 'Novo Curso' }}</h2>
        <v-form v-model="valid" @submit.prevent="save">
          <div class="d-flex flex-column gap-6">
            <v-text-field v-model="editedItem.name" label="Nome do Curso" variant="outlined" :rules="[v => !!v || 'Campo obrigatório']" />
            <v-text-field v-model="editedItem.category" label="Categoria" variant="outlined" :rules="[v => !!v || 'Campo obrigatório']" />
          </div>
          <div class="d-flex justify-end gap-4 mt-12">
            <v-btn variant="text" @click="dialog = false" class="font-weight-bold px-6">CANCELAR</v-btn>
            <v-btn type="submit" color="primary" :disabled="!valid" class="font-weight-bold px-10 text-black">SALVAR CURSO</v-btn>
          </div>
        </v-form>
      </v-card>
    </v-dialog>

    <v-dialog v-model="confirmDeleteDialog" max-width="450">
      <v-card class="pa-8 border-all bg-surface elevation-24 rounded-lg">
        <h2 class="text-xl font-bold text-text-primary mb-4 text-center">Remover Curso?</h2>
        <p class="text-base text-secondary mb-10 text-center">Esta ação é definitiva e afetará os tópicos vinculados a este curso.</p>
        <div class="d-flex justify-center gap-4">
          <v-btn variant="text" @click="confirmDeleteDialog = false" class="font-weight-bold px-6">CANCELAR</v-btn>
          <v-btn color="error" @click="confirmDelete" class="font-weight-bold px-10">REMOVER</v-btn>
        </div>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.bg-background-alt { background: rgba(var(--v-theme-text-primary), 0.02); }
.border-all { border: 1px solid rgba(var(--v-border-color), 0.1) !important; }
.hover-bg:hover { background-color: rgba(var(--v-theme-text-primary), 0.015) !important; }
.transition-fast { transition: all 0.2s ease-in-out; }
</style>
