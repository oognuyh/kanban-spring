<template>
    <v-container>
        
    </v-container>
</template>

<script>
export default {
    mounted() {
        const { authToken, refreshToken } = this.$route.query
       
        localStorage.setItem('authToken', authToken)
        localStorage.setItem('refreshToken', refreshToken)

        this.$router.replace("/")
    }
}
</script>