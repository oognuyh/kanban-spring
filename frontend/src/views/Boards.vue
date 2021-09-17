<template>
  <v-container
    fluid
    fill-height
  > 
    <Header 
      v-model="isDrawerOpen"
    />

    <Sidebar
      v-model="isDrawerOpen"
    />

    <router-view :key="$route.fullPath" />
    
    <Footer />
  </v-container>
</template>

<script>
import Footer from '@/components/Footer'
import Header from '@/components/Header'
import Sidebar from '@/components/Sidebar'

export default {
    components: {
        Footer,
        Header,
        Sidebar
    },
    data() {
        return {
            isDrawerOpen: false
        }
    }
}
</script>

<style>

</style>