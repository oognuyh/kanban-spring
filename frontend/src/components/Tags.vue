<template>
  <v-chip-group
    column
  >
    <template
      v-for="(tag, i) in tags"
    >
      <Tag 
        :name="tag.name" 
        :color="tag.color" 
        :key="i" 
      />
    </template>
  </v-chip-group>
</template>

<script>
import Tag from '@/components/Tag'

export default {
    components: {
        Tag
    },
    props: {
        tags: {
            type: Array,
            required: false,
            default: Array
        }
    }
} 
</script>

<style>

</style>