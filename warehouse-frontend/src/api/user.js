// 用户管理相关API接口
import request from '@/utils/request'

// 登录
export function userLogin(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/user/list',
    method: 'get',
    params
  })
}

// 添加用户
export function addUser(data) {
  return request({
    url: '/api/user/add',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/api/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/user/delete/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id) {
  return request({
    url: `/api/user/reset-password/${id}`,
    method: 'put'
  })
}

// 修改密码
export function changePassword(id, data) {
  return request({
    url: `/api/user/change-password/${id}`,
    method: 'put',
    data
  })
}

// 切换用户状态
export function toggleUserStatus(id, status) {
  return request({
    url: `/api/user/toggle-status/${id}?status=${status}`,
    method: 'put'
  })
}