<template>
  <v-card
    tile
    class="ma-2"
  >
    <v-card-actions>
      <v-tooltip bottom>
        <template v-slot:activator="{ on, attrs }">
          <v-icon
            v-bind="attrs"
            class="mx-1"
            color="blue-grey darken-4"
            small
            v-on="on"
          >
            mdi-label
          </v-icon>
        </template>
        <Tags
          :tags="task.tags"
        />
      </v-tooltip>
      <v-spacer />

      <v-icon
        class="mx-1"
        small
        @click="isTaskEditDialogOpen = true"
      >
        mdi-wrench
      </v-icon>
      <Dialog
        v-model="isTaskEditDialogOpen"
        title="Edit the task"
        @ok="edit"
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
      
      <v-icon
        class="mx-1"
        color="red"
        small
        @click="remove"
      >
        mdi-delete
      </v-icon>
    </v-card-actions>

    <v-card-text
      class="pb-0"
    >
      <div 
        v-html="task.content"
      />
    </v-card-text>
    
    <v-card-actions>
      <v-spacer />
      <span class="text-caption">{{ fromNow }} by 
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-avatar
              v-bind="attrs"
              class="mx-1"
              size="24px"
              v-on="on"
            >
              <v-img :src="task.userImageUrl" />
            </v-avatar>
          </template>
          {{ task.userName }}
        </v-tooltip>
        {{ until }}
      </span>
    </v-card-actions>
  </v-card>
</template>

<script>
import Tags from '@/components/Tags'
import Dialog from '@/components/Dialog'
import { mapState, mapActions } from 'vuex'
import moment from 'moment'

export default {
    components: {
        Tags,
        Dialog
    },
    props: {
        task: {
            type: Object,
            required: true
        }
    },
    data() {
        return {
            menu: false,
            today: moment().format('yyyy-MM-DD'),
            isTaskEditDialogOpen: false,
            newTask: {
                ...this.task
            }
        }
    },
    computed: {
        ...mapState({
            user: state => state.auth.user,
            tags: state => state.board.board.tags,
            message: state => state.error.message
        }),
        fromNow: function() {
            return moment(this.task.createdAt, 'yyyy-MM-DD HH:mm').fromNow()
        },
        until: function() {
            return moment(this.task.deadline, 'yyyy-MM-DD').endOf('day').fromNow()
        }
    },
    watch: {
        isTaskEditDialogOpen: function() {
            this.newTask = {
                ...this.task
            }
            this.$refs.newTaskForm && this.$refs.newTaskForm.resetValidation()
        }
    },
    methods: {
        ...mapActions('board', ['update']),
        remove() {
            this.$emit('remove', this.task)
        },
        async edit() {
            if (this.$refs.newTaskForm.validate()) {
                const originalTask = this.task

                const isSuccessful = await this.$emit('update', originalTask, this.newTask)
                if (isSuccessful) {
                    this.isTaskEditDialogOpen = false
                }
            }
        }
    }
}
</script>

<style>

</style>