import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import Login from '@/views/Login.vue' // 导入登录页面

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Home',
    redirect: '/dashboard' // 重定向到仪表板
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: HomeView
  },
  {
    path: '/goods/list',
    name: 'GoodsList',
    component: HomeView,
    children: [
      {
        path: '',
        name: 'GoodsListPage',
        component: () => import('@/views/goods/GoodsList.vue')
      },
      {
        path: '/warehouse/add',
        name: 'AddWarehouse',
        component: () => import('@/views/warehouse/AddWarehouse.vue')
      },
      {
        path: '/enterprise/info',
        name: 'EnterpriseInfo',
        component: () => import('@/views/enterprise/EnterpriseInfo.vue')
      },
      {
        path: '/inoutrecord',
        name: 'InOutRecord',
        component: () => import('@/views/inOutRecord/InOutRecord.vue')
      }
    ]
  },

]

const router = new VueRouter({
  mode: 'history',
  // base: "/",
  routes
})

export default router