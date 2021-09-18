<template>
  <v-container
    fluid
    fill-height
  >
    <v-toolbar
      flat
    >
      <v-spacer />
      
      <v-btn
        fab
        text
        small
        color="grey darken-2"
        @click="$refs.calendar.prev()"
      >
        <v-icon small>
          mdi-chevron-left
        </v-icon>
      </v-btn>
      <v-btn
        icon
        @click="focus=''"
      >
        <v-icon>
          mdi-calendar-today
        </v-icon>
      </v-btn>
      <v-toolbar-title v-if="$refs.calendar">
        {{ $refs.calendar.title }}
      </v-toolbar-title>
      <v-btn
        fab
        text
        small
        color="grey darken-2"
        @click="$refs.calendar.next()"
      >
        <v-icon small>
          mdi-chevron-right
        </v-icon>
      </v-btn>
      
      <v-spacer />
    </v-toolbar>

    <v-calendar
      ref="calendar"
      v-model="focus"
      :events="events"
      :event-color="getEventColor"
      type="month"
      @click:event="showEvent"
    />
    <v-menu
      v-model="isEventShowOpen"
      :close-on-content-click="false"
      :activator="selectedElement"
      offset-x
    >
      <v-card>
        <v-card
          flat
          width="300px"
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
                :tags="selectedEvent.tags"
              />
            </v-tooltip>
            <v-spacer />

            <v-btn
              icon
              color="red"
              @click="isEventShowOpen = false"
            >
              <v-icon>
                mdi-close-circle-outline
              </v-icon>
            </v-btn>
          </v-card-actions>
          
          <v-card-text
            class="pb-0"
          >
            <div v-html="selectedEvent.content" />
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
                    <v-img :src="selectedEvent.userImageUrl" />
                  </v-avatar>
                </template>
                {{ selectedEvent.userName }}
              </v-tooltip>
            </span>
          </v-card-actions>
        </v-card>
      </v-card>
    </v-menu>
  </v-container>
</template>

<script>
import { mapActions, mapState } from 'vuex'
import Tags from '@/components/Tags'
import moment from 'moment'

export default {
    components: {
        Tags
    },
    data() {
        return {
            focus: '',
            isEventShowOpen: false,
            selectedElement: {},
            selectedEvent: {}
        }
    },
    computed: {
        ...mapState({
            tasks: state => state.board.tasks
        }),
        events: function() {
            return this.tasks.map(task => {
                return {
                    name: `${task.content.substring(0, 15)}${task.content.length > 15 ? ' ...' : ''}`,
                    start: `${task.deadline}`,
                    end: `${task.deadline}`,
                    color:  ['blue', 'indigo', 'deep-purple', 'green', 'orange', 'red'][Math.floor(Math.random() * 6)],
                    ...task
                }
            })
        },
        fromNow: function() {
            return moment(this.selectedEvent.createdAt, 'yyyy-MM-DD HH:mm').fromNow()
        }
    },
    mounted() {
        console.log("수행")
        this.getTasks()
    },
    methods: {
        ...mapActions('board', ['getTasks']),
        getEventColor(event) {
            return event.color
        },
        showEvent({ nativeEvent, event }) {
            const open = () => {
                this.selectedEvent = event
                this.selectedElement = nativeEvent.target
                requestAnimationFrame(() => requestAnimationFrame(() => this.isEventShowOpen = true))
            }

            if (this.isEventShowOpen) {
                this.isEventShowOpen = false
                requestAnimationFrame(() => requestAnimationFrame(() => open()))
            } else {
                open()
            }

            nativeEvent.stopPropagation()
        }
    }
}
</script>

<style>

</style>