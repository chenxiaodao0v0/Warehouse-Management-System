const state = {
  userInfo: null,
  token: '',
  permissions: [] // 用户菜单权限
}

const mutations = {
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  }
}

const actions = {
  // 用户登录
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      // 登录逻辑
      login({ username: username.trim(), password: password })
        .then(response => {
          const { data } = response
          commit('SET_TOKEN', data.token)
          // 设置token到cookie
          setToken(data.token)
          resolve()
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 获取用户信息
  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then(response => {
          const { data } = response

          if (!data) {
            reject('验证失败，请重新登录.')
          }

          commit('SET_USER_INFO', data)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 获取用户权限
  getUserPermissions({ commit, state }) {
    return new Promise((resolve, reject) => {
      getUserMenuPermissions(state.userInfo.id)
        .then(response => {
          const permissions = response || []
          commit('SET_PERMISSIONS', permissions)
          resolve(permissions)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 用户登出
  logout({ commit, dispatch }) {
    return new Promise((resolve) => {
      // 登出API调用可在此处添加
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', null)
      commit('SET_PERMISSIONS', [])
      removeToken()
      resetRouter()
      // 重置访问的路由
      dispatch('permission/resetRoutes', null, { root: true })
      resolve()
    })
  },

  // 重置token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', null)
      commit('SET_PERMISSIONS', [])
      removeToken()
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}