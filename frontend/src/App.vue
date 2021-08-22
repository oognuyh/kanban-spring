<template>
  <v-app>
    <v-main>
      <router-view />
    </v-main>
    <v-btn
        href="http://localhost:9999/oauth2/authorization/github"
      >
        <span class="mr-2">Login with Github</span>
        <v-icon>mdi-github</v-icon>
      </v-btn>
  </v-app>
</template>

<script>
import axios from 'axios'

export default {
  data: () => ({
    //
  }),
  mounted() {
    // let [authToken, refreshToken] = window.location.search.split("&")
    // 
    // authToken = authToken.replace("?authToken=")
    // refreshToken = refreshToken.replace("refreshToken=")

    console.log(this.$route.query.authToken)

  },
  methods: {
    login: () => {
      axios.get("http://localhost:9999/oauth2/authorization/github")
        .then(response => console.log(response))
    }
  }
};
</script>