<template>
  <v-chip
    :color="color"
    :text-color="textColor"
    label
    small
  >
    <v-icon 
      v-if="!!icon"
      left 
    >
      mdi-{{ icon }}
    </v-icon>
    {{ name }}
  </v-chip>
</template>

<script>
export default {
    props: {
        color: {
            type: String,
            required: true
        },
        name: {
            type: String,
            required: true
        },
        icon: {
            type: String,
            required: false,
            default: undefined
        }
    },
    computed: {
        textColor: function() {
            const c = this.color.substring(1)

            const rgb = parseInt(c, 16)
            const r = (rgb >> 16) & 0xff
            const g = (rgb >> 8) & 0xff
            const b = (rgb >> 0) & 0xff

            const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b

            return luma < 127.5 ? "white" : "black"
        }
    }
}
</script>

<style>

</style>