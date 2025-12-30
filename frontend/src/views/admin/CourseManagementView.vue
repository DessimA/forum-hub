<template>
  <v-container>
    <v-row>
      <v-col cols="12" class="d-flex justify-space-between align-center">
        <h1 class="text-h4 font-weight-bold text-white">Gerenciar Cursos</h1>
        <v-btn color="primary" variant="flat" prepend-icon="mdi-plus" @click="openDialog()">
          Novo Curso
        </v-btn>
      </v-col>
    </v-row>

    <v-card class="mt-4 border-subtle" color="surface" variant="flat">
      <v-table bg-color="transparent" theme="dark">
        <thead>
          <tr>
            <th class="text-grey-lighten-1">ID</th>
            <th class="text-grey-lighten-1">Nome</th>
            <th class="text-grey-lighten-1">Categoria</th>
            <th class="text-right text-grey-lighten-1">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="courseStore.loading">
            <td colspan="4" class="text-center py-4">
              <v-progress-circular indeterminate color="primary" />
            </td>
          </tr>
          <tr v-else-if="courseStore.courses.length === 0">
            <td colspan="4" class="text-center py-4 text-grey">
              Nenhum curso encontrado.
            </td>
          </tr>
          <tr v-for="course in courseStore.courses" :key="course.id">
            <td class="text-caption text-grey">{{ course.id.substring(0, 8) }}...</td>
            <td class="font-weight-bold text-white">{{ course.name }}</td>
            <td>
              <v-chip size="small" variant="tonal" color="secondary">
                {{ course.category }}
              </v-chip>
            </td>
            <td class="text-right">
              <v-btn icon="mdi-pencil-outline" size="small" variant="text" color="primary" @click="openDialog(course)" />
              <v-btn icon="mdi-delete-outline" size="small" variant="text" color="error" @click="deleteCourse(course.id)" />
            </td>
          </tr>
        </tbody>
      </v-table>
      
      <v-divider style="opacity: 0.05" />
      
      <div class="pa-4 d-flex justify-center">
        <v-pagination
          v-model="page"
          :length="courseStore.totalPages"
          :total-visible="5"
          color="primary"
          @update:model-value="fetchPage"
        />
      </div>
    </v-card>

    <!-- Dialog de Cadastro/Edição -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card color="surface" class="border-subtle">
        <v-card-title class="text-h5 text-white pa-4">
          {{ editedId ? 'Editar Curso' : 'Novo Curso' }}
        </v-card-title>
        <v-card-text class="pt-0">
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="editedItem.name"
              label="Nome do Curso"
              :rules="[v => !!v || 'Campo obrigatório']"
              required
              variant="outlined"
              color="primary"
            />
            <v-text-field
              v-model="editedItem.category"
              label="Categoria"
              :rules="[v => !!v || 'Campo obrigatório']"
              required
              variant="outlined"
              color="primary"
              class="mt-2"
            />
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4 pt-0">
          <v-spacer />
          <v-btn variant="text" color="grey" @click="dialog = false">Cancelar</v-btn>
          <v-btn color="primary" variant="flat" :disabled="!valid" @click="save">
            Salvar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.border-subtle { 
  border: 1px solid rgba(255, 255, 255, 0.05) !important; 
}
</style>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useCourseStore } from '@/stores/course'

const courseStore = useCourseStore()
const dialog = ref(false)
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

const deleteCourse = async (id: string) => {
  if (confirm('Tem certeza que deseja remover este curso?')) {
    await courseStore.deleteCourse(id)
  }
}

onMounted(() => {
  courseStore.fetchCourses(0)
})
</script>