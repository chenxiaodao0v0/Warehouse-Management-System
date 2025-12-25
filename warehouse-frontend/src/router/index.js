import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/Login'
import HomeView from '@/views/HomeView'
import AddWarehouse from '@/views/warehouse/AddWarehouse'
import GoodsList from '@/views/goods/GoodsList'
import EnterpriseInfo from '@/views/enterprise/EnterpriseInfo'
import InOutRecord from '@/views/inOutRecord/InOutRecord'
import store from '@/store'
// 导入Element的Message组件
import { Message } from 'element-ui'

Vue.use(Router)

const router = new Router({
  mode: 'hash',
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'Home',
      component: HomeView,
      meta: { requiresAuth: true },
      redirect: '/goods/list',
      children: [
        {
          path: 'warehouse/add',
          name: 'AddWarehouse',
          component: AddWarehouse
        },
        {
          path: 'goods/list',
          name: 'GoodsList',
          component: GoodsList
        },
        {
          path: 'enterprise/info',
          name: 'EnterpriseInfo',
          component: EnterpriseInfo
        },
        {
          path: 'inout/record',
          name: 'InOutRecord',
          component: InOutRecord
        }
      ]
    },
    {
      path: '*',
      redirect: '/goods/list'
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = store.state.user.token
  
  if (to.meta.requiresAuth && !token) {
    // 使用导入的Message组件，避免未初始化问题
    Message.warning('请先登录')
    next('/login')
  } else {
    next()
  }
})

export default router