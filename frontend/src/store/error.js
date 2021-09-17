export default {
    namespaced: true,
    state: {
        message: {

        }
    },
    mutations: {
        SET_MESSAGE(state, message) {
            state.message = message
        }
    }
}
