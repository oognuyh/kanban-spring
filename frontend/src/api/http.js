import axios from 'axios'
import router from '../router'
import store from '../store'

const instance = axios.create({
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
})

instance.interceptors.request.use(
    function (config) {
        const authToken = localStorage.getItem('authToken')

        if (authToken) {
            config['headers'] = {
                Authorization: `Bearer ${authToken}`
            }
        }

        return config
    }
)

instance.interceptors.response.use(
    function (response) {
        return response
    },
    async function (error) {
        if (error.response.status === 401) {
            const originalRequest = error.config

            if (originalRequest.url.includes("refreshTokens")) {
                router.replace("/login").catch(()=>{})
                return Promise.reject(error)
            }

            if (!originalRequest._retry) {
                originalRequest._retry = true
                await store.dispatch('auth/refreshTokens')
                
                const authToken = localStorage.getItem('authToken')

                originalRequest.headers.Authorization = `Bearer ${authToken}`

                return axios(originalRequest)
            }
        }

        return Promise.reject(error)
    }
)

export default instance