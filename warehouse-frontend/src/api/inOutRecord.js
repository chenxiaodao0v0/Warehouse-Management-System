import request from '@/utils/request'

// 分页查询出入库/调货记录
export function getInOutRecordList(params) {
  return request({
    url: '/api/inout/page',
    method: 'get',
    params
  })
}

// 商品入库
export function goodsInStock(data) {
  return request({
    url: '/api/inout/in',
    method: 'post',
    data
  })
}

// 商品出库
export function goodsOutStock(data) {
  return request({
    url: '/api/inout/out',
    method: 'post',
    data
  })
}

// 商品调货
export function goodsTransfer(data) {
  return request({
    url: '/api/inout/transfer',
    method: 'post',
    data
  })
}

// 根据ID查询出入库/调货记录详情
export function getInOutRecordById(id) {
  return request({
    url: `/api/inout/${id}`,
    method: 'get'
  })
}

// 按时间范围+仓库ID查出入库记录
export function getRecordByTimeAndWarehouse(params) {
  return request({
    url: '/api/inout/record',
    method: 'get',
    params
  })
}

// 按商品ID查所有出入库/调货记录
export function getRecordByGoodsId(goodsId) {
  return request({
    url: `/api/inout/record/goods/${goodsId}`,
    method: 'get'
  })
}