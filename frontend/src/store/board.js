import * as boardApi from '@/api/board'
import router from '@/router'

export default {
    namespaced: true,
    state: {
        boards: [],
        board: {
            title: '',
            columns: []
        },
        tasks: []
    },
    mutations: {
        SET_BOARDS(state, boards) {
            state.boards = boards
        },
        SET_BOARD(state, board) {
            state.board = board
        },
        ADD_BOARD(state, board) {
            state.boards.push(board)
        },
        SET_TASKS(state, tasks) {
            state.tasks = tasks
        }
    },
    actions: {
        async getBoards({ commit }) {
            try {
                const response = await boardApi.getBoards()

                commit('SET_BOARDS', response.data)
            } catch (error) {
                console.log(error)
            }
        },
        async getBoard({ commit }, id) {
            try {
                const response = await boardApi.getBoard(id)

                commit('SET_BOARD', response.data)
            } catch (error) {
                console.log(error)
            }
        },
        async createNew({ commit }) {
            try {
                const response = await boardApi.createNew()

                if (response.status === 200) {
                    commit('ADD_BOARD', response.data)
                    router.push("/board/" + response.data.id)
                }
            } catch (error) {
                console.log(error)
            }
        },
        async update({ state, commit }) {
            try {
                const response = await boardApi.update(state.board)

                commit('SET_BOARD', response.data)

                return true
            } catch (error) {
                return false
            }
        },
        async getTasks({ commit }) {
            try {
                const response = await boardApi.getTasks()
                console.log(response.data)
                commit('SET_TASKS', response.data)
            } catch (error) {
                console.log(error)
            }
        }
    }
}
