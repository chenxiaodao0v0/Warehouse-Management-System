// Vue2 Axios封装（自动携带Token、统一处理响应/异常）
import axios from 'axios'
import { Message } from 'element-ui' // Vue2 Element UI 提示组件（区别于Vue3的ElMessage）
import store from '@/store' // 引入Vuex

// 创建Axios实例
const service = axios.create({
  timeout: 5000 // 请求超时时间（5秒）
})

// 请求拦截器：发送请求前自动添加Token
service.interceptors.request.use(
  config => {
    // 从Vuex中获取Token，添加到请求头（Bearer 规范）
    if (store.state.token) {
      config.headers['Authorization'] = 'Bearer ' + store.state.token
    }
    return config
  },
  error => {
    // 请求错误控制台打印
    console.log('请求拦截器异常：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理后端返回结果
service.interceptors.response.use(
  response => {
    // 后端返回的核心数据（JSON格式）
    const res = response.data

    // 状态码非200，视为业务异常
    if (res.code !== 200) {
      // Element UI 错误提示
      Message({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 3 * 1000 // 提示显示3秒
      })

      // Token过期（401）：清除Token并跳转登录页
      if (res.code === 401) {
        store.commit('CLEAR_TOKEN') // 调用Vuex mutations清除Token
        window.location.href = '/#/login' // Vue2 hash模式路由跳转（History模式可直接写/login）
      }

      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      // 响应成功，返回核心数据
      return res
    }
  },
  error => {
    // 系统异常（如网络错误、后端500错误）
    console.log('响应拦截器异常：', error)
    Message({
      message: '服务器内部异常，请稍后重试',
      type: 'error',
      duration: 3 * 1000
    })
    return Promise.reject(error)
  }
)

// 导出封装后的Axios实例
export default service
