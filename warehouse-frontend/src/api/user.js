// 用户模块接口封装
import request from '@/utils/request'

/**
 * 用户登录接口
 * @param {Object} data 登录参数（username + password）
 * @returns Promise
 */
export function userLogin(data) {
  return request({
    url: '/api/user/login', // 对应后端接口路径，代理自动转发到后端
    method: 'post',
    params: data // 对应后端@RequestParam，用params传递（若后端用@RequestBody，用data传递）
  })
}
