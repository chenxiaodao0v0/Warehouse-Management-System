// Vue2 路由配置（Vue Router 3）
import Vue from 'vue'
import Router from 'vue-router'
import { Message } from 'element-ui' // 提示组件
import store from '@/store' // 引入Vuex
import EnterpriseInfo from '@/pages/enterprise/EnterpriseInfo.vue'
// 导入页面组件
import Login from '@/pages/LoginPage.vue' // 登录页
import Index from '@/pages/IndexPage.vue' // 首页

// Vue2 全局注册Router
Vue.use(Router)

// 路由规则配置
const routes = [
  {
    path: '/',
    redirect: '/index' // 默认跳转首页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false } // 无需登录即可访问
  },
  {
    path: '/index',
    name: 'Index',
    component: Index,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
   {
    path: '/enterprise/info',
    name: 'EnterpriseInfo',
    component: EnterpriseInfo,
    meta: { requiresAuth: true } // 需要登录才能访问
  }
  // 后续可添加其他功能模块路由（如用户管理、仓库管理）
  // {
  //   path: '/user/list',
  //   name: 'UserList',
  //   component: () => import('@/pages/user/UserList.vue'), // 懒加载（推荐）
  //   meta: { requiresAuth: true }
  // }
]

// 创建Router实例（Vue2写法）
const router = new Router({
  mode: 'hash', // Vue2 推荐hash模式（兼容性更好，无需后端配置），history模式需后端支持
  routes // 路由规则
})

// 路由守卫：拦截未登录用户访问需要权限的页面
router.beforeEach((to, from, next) => {
  // 判断当前页面是否需要登录权限
  if (to.meta.requiresAuth) {
    // 从Vuex中获取Token
    const token = store.state.token
    if (token) {
      // 有Token，放行
      next()
    } else {
      // 无Token，提示并跳转登录页
      Message({
        message: '请先登录系统',
        type: 'warning'
      })
      next('/login') // 跳转登录页
    }
  } else {
    // 无需登录，直接放行
    next()
  }
})

export default router
