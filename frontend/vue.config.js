module.exports = {
  transpileDependencies: [
    'vuetify'
  ],
  devServer: {
    port: 9998,
    proxy: {
      "/api": {
        target: "http://localhost:9999/"
      }
    }
  }
}
