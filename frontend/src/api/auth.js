import http from '@/api/http'

export function getUser() {
    return http.get('/api/v1/auth/user')
}

export function refreshTokens(refreshToken) {
    return http.post('/api/v1/auth/refreshTokens', {
        refreshToken
    })
}