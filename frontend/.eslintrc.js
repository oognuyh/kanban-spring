module.exports = {
    extends: [
        'plugin:vue/recommended'
    ],
    plugins: [
        'vuetify'
    ],
    rules: {
        'vuetify/no-deprecated-classes': 'error',
        "vue/valid-v-model": "off",
    },
    parserOptions: {
        ecmaVersion: 2017
    }
}
