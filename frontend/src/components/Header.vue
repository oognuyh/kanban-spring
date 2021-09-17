<template>
  <v-app-bar
    class="blue-grey darken-4"
    dark
    app
  >
    <v-app-bar-nav-icon @click="isOpen = !isOpen" />  
    <v-spacer />
    
    <v-btn
      icon
      @click="logout"
    >
      <v-icon>mdi-logout</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script>
import { mapActions } from 'vuex'
export default {
    props: {
        value: {
            type: Boolean,
            required: true
        } 
    },
    computed: {
        isOpen: {
            get() {
                return this.value
            },
            set(v) {
                this.$emit('input', v)
            }
        }
    },
    methods: {
        ...mapActions('auth', ['logout'])
    }
}
</script>

<style>

</style>