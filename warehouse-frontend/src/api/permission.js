// 权限管理相关API接口
import request from '@/utils/request'

// 获取用户菜单权限
export function getUserMenuPermissions(userId) {
  return request({
    url: `/api/permission/menu/${userId}`,
    method: 'get'
  })
}

// 设置用户菜单权限
export function setUserMenuPermissions(data) {
  return request({
    url: '/api/permission/menu',
    method: 'post',
    data
  })
}

// 获取用户仓库权限
export function getUserWarehousePermissions(userId) {
  return request({
    url: `/api/permission/warehouse/${userId}`,
    method: 'get'
  })
}

// 设置用户仓库权限
export function setUserWarehousePermissions(data) {
  return request({
    url: '/api/permission/warehouse',
    method: 'post',
    data
  })
}

// 获取用户所有权限
export function getAllPermissions(userId) {
  return request({
    url: `/api/permission/all/${userId}`,
    method: 'get'
  })
}

// 批量设置用户权限
export function setAllPermissions(data) {
  return request({
    url: '/api/permission/batch',
    method: 'post',
    data
  })
}