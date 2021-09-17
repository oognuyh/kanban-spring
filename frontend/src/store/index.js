import Vue from 'vue'
import Vuex from 'vuex'
import auth from './auth'
import board from './board'
import error from './error'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {

    },
    mutations: {

    },
    actions: {

    },
    modules: {
        auth,
        board,
        error
    }
})
