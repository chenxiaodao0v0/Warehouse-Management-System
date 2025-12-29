import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { 
          title: '首页',
          menuCode: 'dashboard'
        }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('../views/user/UserList.vue'),
        meta: { 
          title: '用户列表',
          menuCode: 'user_list'
        }
      },
      {
        path: 'user/permission',
        name: 'PermissionManagement',
        component: () => import('../views/user/PermissionManagement.vue'),
        meta: { 
          title: '权限管理',
          menuCode: 'permission_management'
        }
      },
      {
        path: '/user/profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { 
          title: '个人信息',
          menuCode: 'user_profile'
        }
      },
      {
        path: 'warehouse/list',
        name: 'WarehouseList',
        component: () => import('../views/warehouse/AddWarehouse.vue'),
        meta: { 
          title: '仓库列表',
          menuCode: 'warehouse_list'
        }
      },
      {
        path: 'warehouse/inventory',
        name: 'InventoryOverview',
        component: () => import('../views/warehouse/InventoryOverview.vue'),
        meta: { 
          title: '库存总览',
          menuCode: 'inventory_overview'
        }
      },
      {
        path: 'goods/list',
        name: 'GoodsList',
        component: () => import('../views/goods/GoodsList.vue'),
        meta: { 
          title: '商品列表',
          menuCode: 'goods_list'
        }
      },
      {
        path: 'goods/category',
        name: 'GoodsCategory',
        component: () => import('../views/goods/GoodsCategory.vue'),
        meta: { 
          title: '商品分类',
          menuCode: 'goods_category'
        }
      },
      {
        path: 'inOut/record',
        name: 'InOutRecord',
        component: () => import('../views/inOutRecord/InOutRecord.vue'),
        meta: { 
          title: '出入库记录',
          menuCode: 'in_out_record'
        }
      },
      // 统计总览功能尚未实现，暂时注释掉
      // {
      //   path: 'statistics/overview',
      //   name: 'StatisticsOverview',
      //   component: () => import('../views/statistics/Overview.vue'),
      //   meta: { 
      //     title: '统计总览',
      //     menuCode: 'statistics_overview'
      //   }
      // },
      {
        path: 'enterprise/info',
        name: 'EnterpriseInfo',
        component: () => import('../views/enterprise/EnterpriseInfo.vue'),
        meta: { 
          title: '企业信息',
          menuCode: 'enterprise_info'
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/401',
    name: 'Unauthorized',
    component: () => import('@/views/401.vue')
  },
  {
    path: '*',
    redirect: '/401'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth !== false)) {
    // 检查用户是否已登录
    if (!store.state.user.token) {
      // 如果未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 如果已登录，继续
      next()
    }
  } else {
    // 如果不需要登录，继续
    next()
  }
})

export default router