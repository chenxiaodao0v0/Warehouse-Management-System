// 用户模块接口封装
import request from '@/utils/request'

/**
 * 用户登录接口
 * @param {Object} data 登录参数（username + password）
 * @returns Promise
 */
export function userLogin(data) {
  return request({
    url: '/user/login', // 去掉重复的/api（baseURL已包含）
    method: 'post',
    data: data // 改用data传递请求体（适配后端@RequestBody）
  })
}
