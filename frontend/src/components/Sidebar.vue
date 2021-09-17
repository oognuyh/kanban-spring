<template>
  <v-navigation-drawer
    v-model="isOpen"
    class="blue-grey darken-4"
    dark
    app
  >
    <v-list-item>
      <v-list-item-content>
        <v-list-item-title
          class="pb-3"
        >
          <v-list-item-icon>
            <a href="/">
              <v-img
                src="@/assets/logo.png"
                alt="logo"
                height="24"
                width="24"
                icon
              />
            </a>
          </v-list-item-icon>
          <span class="text-h6">Kanban Board</span>
        </v-list-item-title>
      
        <v-list-item-subtitle>
          <v-avatar 
            size="36"
            color="white"
          >
            <v-img 
              v-if="user.imageUrl != null"
              :src="user.imageUrl"
            />
            <span
              v-else
              class="black--text text-h5"
            >
              {{ user.name && user.name[0] }}
            </span>
          </v-avatar>
          <span class="ml-3 white--text">
            {{ user.name }}
          </span>
        </v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>

    <v-divider />
    <v-list
      dense
      nav
    >
      <v-list-item
        :to="'/board'"
        class="py-1"
        link
        exact
      >
        <v-list-item-icon>
          <v-icon>mdi-calendar</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>
            Calendar
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item
        v-for="board in boards"
        :key="board.id"
        :to="'/board/' + board.id"
        link
        class="py-1"
      >
        <v-list-item-icon>
          <v-icon>mdi-label</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>
            {{ board.title }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        class="justify-center"
        @click="createNew"
      >
        <v-icon>mdi-plus</v-icon>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { mapActions, mapState } from 'vuex'


export default {
    props: {
        value: {
            type: Boolean,
            required: true
        }
    },
    computed: {
        ...mapState({
            user: state => state.auth.user,
            boards: state => state.board.boards
        }),
        isOpen: {
            get() {
                return this.value
            },
            set(v) {
                this.$emit('input', v)
            }
        }
    },
    mounted() {
        this.getBoards()
    },
    methods: {
        ...mapActions({
            getBoards: 'board/getBoards',
            createNew: 'board/createNew',
        })
    }
}
</script>

<style>

</style>