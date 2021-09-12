import * as authApi from '@/api/auth'
import router from '../router'

export default {
    namespaced: true,
    state: {
        user: {
            imageUrl: null,
        }
    },
    mutations: {
        SET_USER(state, user) {
            state.user = user
        }
    },
    actions: {
        async getUser({commit}) {
            try {
                if (localStorage.getItem('authToken') !== null) {
                    const response = await authApi.getUser()
                    
                    commit('SET_USER', response.data)
                }
            } catch (error) {
                console.log(error)
            }
        },
        logout() {
            localStorage.removeItem('authToken')
            localStorage.removeItem('refreshToken')
            router.replace('/login')
        },
        async refreshTokens() {
            try {
                const refreshToken = localStorage.getItem('refreshToken')
                
                if (refreshToken) {
                    const response = await authApi.refreshTokens(refreshToken)
                    
                    localStorage.setItem('authToken', response.headers['x-auth-token'])
                    localStorage.setItem('refreshToken', response.headers['x-refresh-token'])
                }
            } catch (error) {
                console.log(error)
            }
        }
    }
}