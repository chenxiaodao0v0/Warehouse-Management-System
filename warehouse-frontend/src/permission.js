import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { login, getUserInfo } from '@/api/user'
import { getUserMenuPermissions } from '@/api/permission'
import { imul } from 'core-js/core/number'

NProgress.configure({ showSpinner: false })

// 白名单，无需权限验证的路由
const whiteList = ['/login', '/register']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  // 检查用户是否已登录
  const hasToken = store.getters.token
  
  if (hasToken) {
    // 用户已登录
    if (to.path === '/login') {
      // 如果已登录用户访问登录页，重定向到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 检查用户信息是否存在
      const hasUserInfo = store.getters.userInfo && Object.keys(store.getters.userInfo).length > 0
      
      if (hasUserInfo) {
        // 如果已有用户信息，直接检查权限
        if (await checkPermission(to, store.getters.userInfo)) {
          next()
        } else {
          next({ path: '/401' })
        }
      } else {
        try {
          // 获取用户信息
          const { data } = await store.dispatch('user/getInfo')
          
          // 获取用户菜单权限
          const permissions = await store.dispatch('user/getUserPermissions')
          
          // 将权限信息存储到store
          store.commit('user/SET_PERMISSIONS', permissions)
          
          // 检查权限
          if (await checkPermission(to, data)) {
            next()
          } else {
            next({ path: '/401' })
          }
        } catch (error) {
          // 获取用户信息失败，跳转到登录页
          await store.dispatch('user/resetToken')
          Message.error(error || '获取用户信息失败')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    // 用户未登录
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，允许访问
      next()
    } else {
      // 重定向到登录页
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

/**
 * 检查用户是否有访问指定路由的权限
 */
async function checkPermission(route, userInfo) {
  // 超级管理员拥有所有权限
  if (userInfo.role === 1) {
    return true
  }
  
  // 获取用户菜单权限
  const userPermissions = store.getters.userPermissions || []
  
  // 获取当前路由对应的菜单权限码
  const menuCode = route.meta.menuCode
  
  // 如果路由没有定义menuCode，则允许访问
  if (!menuCode) {
    return true
  }
  
  // 检查用户是否拥有该菜单权限
  return userPermissions.includes(menuCode)
}