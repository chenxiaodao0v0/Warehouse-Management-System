import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user: {
      namespaced: true,
      state: {
        token: localStorage.getItem('token') || '',
        // 增加JSON解析异常处理
        userInfo: (() => {
          try {
            return JSON.parse(localStorage.getItem('userInfo') || '{}')
          } catch (e) {
            console.error('解析userInfo失败：', e)
            return {}
          }
        })()
      },
      mutations: {
        setLoginInfo(state, payload) {
          state.token = payload.token
          state.userInfo = payload.userInfo
          localStorage.setItem('token', payload.token)
          localStorage.setItem('userInfo', JSON.stringify(payload.userInfo))
        },
        clearToken(state) {
          state.token = ''
          state.userInfo = {}
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
        }
      },
      actions: {},
      getters: {}
    }
  }
})
