// Vue2 Vuex 3 状态管理配置（存储Token、用户信息等全局状态）
import Vue from 'vue'
import Vuex from 'vuex'

// Vue2 全局注册Vuex
Vue.use(Vuex)

// 创建Vuex Store实例
export default new Vuex.Store({
  // 全局状态（类似组件的data）
  state: {
    token: localStorage.getItem('token') || '', // Token（优先从本地存储获取，防止页面刷新丢失）
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null // 用户信息（如昵称、角色）
  },

  // 同步修改状态（唯一允许修改state的方式，组件中通过this.$store.commit('方法名')调用）
  mutations: {
    // 存储Token
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token) // 同步存储到本地存储
    },

    // 存储用户信息
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo)) // 对象需转为JSON字符串存储
    },

    // 清除Token和用户信息（退出登录时使用）
    CLEAR_TOKEN(state) {
      state.token = ''
      state.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  },

  // 异步操作（可调用mutations修改状态，组件中通过this.$store.dispatch('方法名')调用）
  actions: {
    // 示例：退出登录（异步操作，实际可直接调用mutations）
    logout({ commit }) {
      commit('CLEAR_TOKEN')
    }
  },

  // 状态派生（类似组件的computed，用于格式化state数据）
  getters: {
    // 获取用户角色（方便组件快速调用）
    userRole: state => {
      return state.userInfo ? state.userInfo.role : null
    }
  }
})
