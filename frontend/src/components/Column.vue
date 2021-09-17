<template>
  <v-card>
    <v-card-title class="text-overline">
      {{ column.title }}
      <v-btn
        icon
        @click="isTitleEditDialogOpen = true"
      >
        <v-icon
          small
        >
          mdi-wrench
        </v-icon>
      </v-btn>

      <Dialog
        v-model="isTitleEditDialogOpen"
        title="Edit the title of the column"
        @ok="editColumnTitle"
      >
        <template v-slot:content>
          <v-form ref="form">
            <v-text-field
              v-model="newColumnTitle"
              :rules="[(v) => !!v || '이름을 입력하세요.']"
              :error-messages="!!message.title ? message.title : ''"
              label="Title"
            />
          </v-form>
        </template>
      </Dialog>
    </v-card-title>

    <v-card-text>
      <draggable
        v-model="column.tasks"
        :group="group"
        tag="div"
        @change="update"
      >
        <Task
          v-for="task in column.tasks"
          :key="task.id"
          :task="task" 
          @update="updateTask"
          @remove="removeTask"  
        />
      </draggable>
    </v-card-text>

    <v-card-actions
      class="justify-center"
    >
      <v-btn
        icon
        @click="isNewTaskDialogOpen = true"
      >
        <v-icon>
          mdi-plus
        </v-icon>
      </v-btn>
    </v-card-actions>
    
    <Dialog 
      v-model="isNewTaskDialogOpen"
      title="Create a new task"
      @ok="addNewTask"
    >
      <template v-slot:content>
        <v-form
          ref="newTaskForm"
        >
          <v-combobox
            v-model="newTask.tags"
            :items="tags"
            item-text="name"
            chips
            clearable
            label="Tags"
            multiple
            
          >
            <template v-slot:selection="{ attrs, item, select, selected }">
              <v-chip
                v-bind="attrs"
                :input-value="selected"
                :color="item.color"
                :text-color="item.textColor"
                label
                small
                @click="select"
              >
                {{ item.name }}
              </v-chip>
            </template>

            <template v-slot:item="{ item }">
              <v-chip
                :color="item.color"
                :text-color="item.textColor"
                label
                small
              >
                {{ item.name }}
              </v-chip>
            </template>
          </v-combobox>

          <v-textarea
            v-model="newTask.content"
            :rules="[v => !!v || '내용을 입력하세요']"
            :error-messages="!!message.content ? message.content : ''"
            rows="10"
            no-resize
            clearable
            counter
            label="Content"
          />

          <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                v-model="newTask.deadline"
                v-bind="attrs"
                label="Select a deadline"
                prepend-icon="mdi-calendar"
                readonly
                v-on="on"
              />
            </template>
            <v-date-picker
              v-model="newTask.deadline"
              :min="today"
              no-title
              @input="menu = false"
            />
          </v-menu>
        </v-form>
      </template>
    </Dialog>
  </v-card>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Dialog from '@/components/Dialog'
import Task from "@/components/Task"
import draggable from 'vuedraggable'
import marked from 'marked'
import moment from 'moment'

export default {
    components: {
        Task,
        Dialog,
        draggable
    },
    props: {
        column: {
            type: Object,
            required: true
        },
        group: {
            type: String,
            default: "group"
        }
    },
    data() {
        return {
            isNewTaskDialogOpen: false,
            isTitleEditDialogOpen: false,
            newColumnTitle: "",
            today: moment().format('yyyy-MM-DD'),
            newTask: {
                content: "",
                deadline: this.today,
                tags: []
            },
            menu: false
        }
    },
    computed: {
        ...mapState({
            user: state => state.auth.user,
            tags: state => state.board.board.tags,
            message: state => state.error.message
        })
    },
    watch: {
        isNewTaskDialogOpen: function() {
            this.newTask = {
                ...this.newTask,
                content: "",
                deadline: this.today,
                tags: []
            }
            this.$refs.newTaskForm && this.$refs.newTaskForm.resetValidation()
        },
        isTitleEditDialogOpen: function() {
            this.newColumnTitle = ""
            this.$refs.form && this.$refs.form.resetValidation()
        }
    },
    methods: {
        ...mapActions('board', ['update']),
        async addNewTask() {
            if (this.$refs.newTaskForm.validate()) {
                this.column.tasks.push({
                    ...this.newTask,
                    content: marked(this.newTask.content),
                    userName: this.user.name,
                    userImageUrl: this.user.imageUrl,
                    createdAt: moment().format('yyyy-MM-DD HH:mm'),
                })

                console.log(this.column.tasks)

                const isSuccessful = await this.update()
                if (!isSuccessful) {
                    this.column.tasks.pop()   
                } else {
                    this.isNewTaskDialogOpen = false
                }
            }
        },
        async editColumnTitle() {
            if (this.$refs.form.validate()) {
                const originalTitle = this.column.title
                this.column.title = this.newColumnTitle

                const isSuccessful = await this.update()
                if (!isSuccessful) {
                    this.column.title = originalTitle
                } else {
                    this.isTitleEditDialogOpen = false
                }
            }
        },
        removeTask(task) {
            this.column.tasks.splice(this.column.tasks.indexOf(task), 1)

            this.update()
        },
        async updateTask(originalTask, newTask) {
            const index = this.column.tasks.indexOf(originalTask)
            this.column.tasks[index] = newTask

            const isSuccessful = await this.update()
            if (!isSuccessful) {
                this.column.tasks[index] = originalTask
                return false
            } else {
                return true
            }
        }
    }
}
</script>

<style>

</style>