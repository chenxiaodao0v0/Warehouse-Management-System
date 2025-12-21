import request from '@/utils/request'

// 查询企业信息
export function getEnterpriseInfo() {
  return request({
    url: '/api/enterprise/info',
    method: 'get'
  })
}

// 修改企业信息
export function updateEnterprise(data) {
  return request({
    url: '/api/enterprise/update',
    method: 'put',
    data: data
  })
}
