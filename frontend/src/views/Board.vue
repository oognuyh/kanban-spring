<template>
  <v-container
    fluid
    fill-height
    align-start
  >
    <v-row>
      <v-col col="12">
        <v-card
          elevation="0"
        >
          <v-card-title>
            {{ board.title }} 
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
              title="Rename the board"
              @ok="editBoardTitle"
            >
              <template v-slot:content>
                <v-form
                  ref="titleEditForm"
                >
                  <v-text-field
                    v-model="newTitle"
                    :rules="[v => !!v || '보드명을 입력하세요.']"
                    :error-messages="message.title != null ? message.title : ''"
                    label="Title"
                  />
                </v-form>
              </template>
            </Dialog>

            <v-spacer />
        
            <v-btn
              text
              @click="isManageTagsDialogOpen = true"
            >
              <span class="text-caption">
                <v-icon
                  class="mr-1"
                  x-small
                >
                  mdi-label
                </v-icon>
                Manage tags
              </span>

              <Dialog
                v-model="isManageTagsDialogOpen"
                title="Manage this board tags"
                @ok="isManageTagsDialogOpen = false"
              >
                <template v-slot:content>
                  <v-chip-group
                    column
                  >
                    <v-chip
                      v-for="(tag, i) in board.tags"
                      :key="'tag' + i"
                      :color="tag.color"
                      :text-color="tag.textColor"
                      close
                      small
                      label
                      @click:close="removeTag(i)"
                    >
                      {{ tag.name }}
                    </v-chip>
                  </v-chip-group>

                  <v-card-text>
                    <v-form
                      ref="newTagForm"
                    >
                      <span class="text-overline">
                        Create a new tag
                      </span>
                      <v-text-field
                        v-model="newTag.name"
                        :rules="[(v) => !!v || '태그명을 입력하세요.']"
                        :error-messages="message.name != null ? message.name : ''"
                        label="Tag name"
                      />
                      <v-text-field
                        v-model="newTag.color"
                        :rules="[(v) => !!v || '배경 색상을 선택하세요.']"
                        label="Background color"
                      />
                    </v-form>
                    
                    <v-row>
                      <v-col
                        cols="12"
                        class="d-flex justify-center"
                      >
                        <v-chip
                          :color="newTag.color"
                          :text-color="newTagTextColor"
                          label
                          small
                        >
                          {{ newTag.name }}
                        </v-chip>
                      </v-col>
                      <v-col
                        cols="12"
                        class="d-flex justify-center"
                      >
                        <v-color-picker
                          v-model="newTag.color"
                          dot-size="25"
                          hide-inputs
                          mode="hexa"
                          show-swatches
                          swatches-max-height="100"
                        />
                      </v-col>
                      
                      <v-btn
                        class="mt-4"
                        color="blue-grey darken-4"
                        block
                        dark
                        @click="addNewTag"
                      >
                        ADD
                      </v-btn>
                    </v-row>
                  </v-card-text>
                </template>

                <template v-slot:footer>
                  <v-btn
                    color="blue-grey darken-4"
                    text
                    @click="isManageTagsDialogOpen = false"
                  >
                    Close
                  </v-btn>
                </template>
              </Dialog>
            </v-btn>
          </v-card-title>

          <v-card-text>
            <v-row>
              <v-col 
                v-for="(column, i) in board.columns"
                :key="i"
                cols="3"
              >
                <Column 
                  :column="column" 
                />
              </v-col>
            </v-row>      
          </v-card-text>
        </v-card>    
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Column from '@/components/Column'
import Dialog from '@/components/Dialog'

export default {
    components: {
        Column,
        Dialog
    },
    data() {
        return {
            isTitleEditDialogOpen: false,
            isManageTagsDialogOpen: false,
            newTitle: '',
            newTag: {
                name: "",
                color: "#FF00FFFF"
            }
        }
    },
    computed: {
        ...mapState({
            board: state => state.board.board,
            message: state => state.error.message
        }),
        newTagTextColor: function() {
            const c = this.newTag.color.substring(1)
            const rgba = parseInt(c, 16)
    
            const r = (rgba >> 24) & 0xff
            const g = (rgba >> 16) & 0xff
            const b = (rgba >> 8) & 0xff

            const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b

            return luma < 127.5 ? "white" : "black"
        }
    },
    watch: {
      isTitleEditDialogOpen: function() {
          this.newTitle = ""
          this.$refs.titleEditForm && this.$refs.titleEditForm.resetValidation()
      },
      isManageTagsDialogOpen: function() {
          this.$refs.newTagForm && this.$refs.newTagForm.resetValidation()
      }
    },
    mounted() {
        const id = this.$route.params.id

        this.getBoard(id)
    },
    methods: {
        ...mapActions({
            getBoard: 'board/getBoard',
            getBoards: 'board/getBoards',
            update: 'board/update'
        }),
        editBoardTitle: async function() {
            const originalTitle = this.board.title
            this.board.title = this.newTitle

            const isSuccessful = await this.update()
            if (!isSuccessful) {
                this.board.title = originalTitle
            } else {
                this.getBoards()
                this.isTitleEditDialogOpen = false
            }
        },
        addNewTag: async function() {
            if (this.$refs.newTagForm.validate()) {
                this.board.tags.push({
                    ...this.newTag,
                    textColor: this.newTagTextColor
                })

                this.newTag = {
                    name: "",
                    color: "#FF00FFFF"
                }
                
                const isSuccessful = await this.update()
                if (!isSuccessful) this.board.tags.pop()

                this.$refs.newTagForm.resetValidation()
            }
        },
        removeTag: function(i) {
            this.board.tags.splice(i, 1)

            this.update()
        }
    }
}
</script>

<style>

</style>