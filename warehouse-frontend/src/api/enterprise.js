import request from '@/utils/request'

// 查询企业信息
export function getEnterpriseInfo() {
  return request({
    url: '/api/enterprise/info', // 添加/api前缀保持一致
    method: 'get'
  })
}

// 修改企业信息
export function updateEnterprise(data) {
  return request({
    url: '/api/enterprise/update', // 添加/api前缀保持一致
    method: 'put',
    data: data
  })
}

// 初始化企业信息
export function initEnterprise(data) {
  return request({
    url: '/api/enterprise/init', // 初始化企业信息的API
    method: 'post',
    data: data
  })
}

// 获取企业列表
export function getEnterpriseList() {
  return request({
    url: '/api/enterprise/list', // 添加获取企业列表的API
    method: 'get'
  })
}