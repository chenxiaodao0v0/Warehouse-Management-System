import request from '@/utils/request'

// 获取统计信息
export function getStatistics() {
  return request({
    url: '/api/statistics',
    method: 'get'
  })
}

// 获取趋势数据
export function getTrendData(params) {
  return request({
    url: '/api/statistics/trend',
    method: 'get',
    params
  })
}

// 获取仓库库存分布
export function getWarehouseDistribution() {
  return request({
    url: '/api/statistics/warehouse-distribution',
    method: 'get'
  })
}

// 获取进出货物数量前十排行
export function getTopTenInOutData(params) {
  return request({
    url: '/api/statistics/top-ten',
    method: 'get',
    params
  })
}