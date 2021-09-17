<template>
  <v-dialog
    v-model="isOpen"
    persistent
    max-width="500"
  >
    <v-card>
      <v-card-title class="text-h5">
        {{ title }}
      </v-card-title>

      <v-card-text>
        <slot name="content" />
      </v-card-text>
        

      <v-card-actions>
        <v-spacer />
        <slot name="footer">
          <v-btn
            color="red darken-4"
            text
            @click.stop="isOpen = false"
          >
            Cancel
          </v-btn>

          <v-btn
            color="blue-grey darken-4"
            text
            @click="$emit('ok')"
          >
            OK
          </v-btn>
        </slot>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
    props: {
        value: {
            type: Boolean,
            required: true
        },
        title: {
            type: String,
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
    }
}
</script>

<style>

</style>