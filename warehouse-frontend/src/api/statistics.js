import request from '@/utils/request'

// 获取统计信息
export function getStatistics() {
  return request({
    url: '/api/statistics',
    method: 'get',
    timeout: 10000 // 增加超时时间到10秒
  })
}

// 获取近期出入库趋势数据
export function getTrendData(params) {
  return request({
    url: '/api/statistics/trend',
    method: 'get',
    params,
    timeout: 10000 // 增加超时时间到10秒
  })
}

// 获取仓库库存分布数据
export function getWarehouseDistribution() {
  return request({
    url: '/api/statistics/warehouse-distribution',
    method: 'get',
    timeout: 10000 // 增加超时时间到10秒
  })
}

