module.exports = {
    transpileDependencies: [
        'vuetify'
    ],
    devServer: {
        port: 9998,
        proxy: {
            "/api": {
                target: process.env.VUE_APP_API_URL
            }
        }
    }
}
