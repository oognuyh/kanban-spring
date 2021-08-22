import Vue from 'vue'
import VueRouter from 'vue-router'
import OAuth2Processor from '@/views/OAuth2Processor'
import NotFound from '@/views/404'
import HelloWorld from '@/components/HelloWorld'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HelloWorld
    },
    {
        path: '/oauth2-process',
        name: 'OAuth2Processor',
        component: OAuth2Processor
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
})