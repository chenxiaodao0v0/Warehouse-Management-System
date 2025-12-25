import request from '@/utils/request'

// 查询企业信息
export function getEnterpriseInfo() {
  return request({
    url: '/enterprise/info', // 去掉重复的/api
    method: 'get'
  })
}

// 修改企业信息
export function updateEnterprise(data) {
  return request({
    url: '/enterprise/update', // 去掉重复的/api
    method: 'put',
    data: data
  })
}
