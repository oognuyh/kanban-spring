import Vue from 'vue'
import VueRouter from 'vue-router'

import store from '@/store'

import OAuth2Processor from '@/views/OAuth2Processor'
import NotFound from '@/views/NotFound'
import Login from '@/views/Login'
import Board from '@/views/Board'
import Boards from '@/views/Boards'
import Calendar from '@/views/Calendar'

Vue.use(VueRouter)

const requireAuth = async function(from, to, next) {
    if (localStorage.getItem('authToken') != null) {
        await store.dispatch('auth/getUser')

        if (store.state.auth.user != null) {
            return next()
        } else {
            return next('/login')
        }
    } else {
        return next('/login')
    }
}

const routes = [
    {
        path: '/',
        redirect: '/board'
    },
    {
        path: '/oauth2-process',
        name: 'OAuth2Processor',
        component: OAuth2Processor
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/board',
        component: Boards,
        beforeEnter: requireAuth,
        children: [
            {
                path: '',
                name: 'Calendar',
                component: Calendar
            },
            {
                path: ':id',
                name: 'Board',
                component: Board
            }
        ]
    },
    {
        path: '*',
        name: 'NotFound',
        component: NotFound
    }
]

export default new VueRouter({
    mode: 'history',
    routes
});
