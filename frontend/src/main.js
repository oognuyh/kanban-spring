import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from './router'
import swal from './plugins/swal'
import VueCookies from 'vue-cookies'
import store from './store'

Vue.config.productionTip = false

Vue.use(VueCookies)

new Vue({
  router,
  vuetify,
  swal,
  store,
  render: h => h(App)
}).$mount('#app')
