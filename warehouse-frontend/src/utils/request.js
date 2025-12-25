import axios from 'axios'
import Vue from 'vue'
import store from '@/store'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端接口基础路径（移除/api，因为后端Controller已包含/api前缀）
  timeout: 5000
})

// 请求拦截器：添加 JWT Token
request.interceptors.request.use(
  config => {
    // 从 Vuex 获取 Token（Vue2 中通过 store.state 访问）
    const token = store.state.user.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    Vue.prototype.$message.error('请求参数错误')
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理错误和响应格式
request.interceptors.response.use(
  response => {
    const res = response.data
    // 后端统一响应格式 R<?>：code=200 为成功
    if (res.code !== 200) {
      Vue.prototype.$message.error(res.msg || '操作失败')
      return Promise.reject(res.msg)
    }
    return res.data // 直接返回业务数据，简化页面调用
  },
  error => {
    // Token 失效（401）处理
    if (error.response && error.response.status === 401) {
      Vue.prototype.$message.error('登录已过期，请重新登录')
      // 清除 Vuex 和本地缓存的 Token
      store.commit('user/clearToken')
      // 跳转登录页（Vue2 路由跳转）
      router.push('/login')
    } else {
      Vue.prototype.$message.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// 全局挂载，页面可通过 this.$http 调用（Vue2 特性）
Vue.prototype.$http = request

export default request
