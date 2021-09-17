import http from '@/api/http'

export function getBoards() {
    return http.get('/api/v1/board/mine')
}

export function getBoard(id) {
    return http.get('/api/v1/board/' + id)
}

export function createNew() {
    return http.post('/api/v1/board')
}

export function update(board) {
    return http.put('/api/v1/board', {
        ...board
    })
}

export function getTasks() {
    return http.get('/api/v1/board/tasks')
}
