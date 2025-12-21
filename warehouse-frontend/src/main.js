// Vue2 核心入口文件
import Vue from 'vue'
import App from './App.vue'
import router from './router' // 路由
import store from './store' // Vuex状态管理

// 1. 引入Element UI（Vue2专属）及样式
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// 2. 引入全局样式
import './assets/css/global.css'

// 3. 全局注册Element UI（Vue2写法）
Vue.use(ElementUI)

// 关闭生产环境提示
Vue.config.productionTip = false

// 创建Vue实例（Vue2核心写法）
new Vue({
  el: '#app', // 挂载到index.html的#app节点
  router, // 注入路由
  store, // 注入Vuex
  render: h => h(App) // 渲染根组件
})
