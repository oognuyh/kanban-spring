<template>
  <v-container
    fill-height
  >
      <v-row
        align="center"
        justify="center"
      >
          <v-col
            cols="12"
            sm="8"
            md="4"
            lg="4"
          >
            <v-card
              elevation="0"
            >
              <a
                href="/"
              >
                <v-img 
                  src="@/assets/logo.png"
                  alt="logo"
                  contain
                  height="100"
                />
              </a>
              <v-card-title
                class="justify-center"
              >
                Welcome to Kanban Board
              </v-card-title>

              <v-card-text>
                <v-btn
                  href="http://localhost:9999/oauth2/authorization/github"
                  dark
                  tile
                  x-large
                  block
                >
                  <v-icon
                    large
                    left
                    class="mr-5"
                  >
                    mdi-github
                  </v-icon>
                  Login with Github
                </v-btn>

                <v-btn
                  href="http://localhost:9999/oauth2/authorization/google"
                  x-large
                  block
                  tile
                  class="mt-3"
                >
                  <v-icon
                    large
                    left
                    class="mr-5"
                  >
                    mdi-google
                  </v-icon>
                  Login with Google
                </v-btn>
              </v-card-text>
            </v-card>
          </v-col>
      </v-row>
  </v-container>
</template>

<script>
export default {
}
</script>

<style>

</style>