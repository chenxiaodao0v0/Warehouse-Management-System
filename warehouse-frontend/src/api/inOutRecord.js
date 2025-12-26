import request from '@/utils/request'

// 分页查询出入库记录
export function getInOutRecordList(params) {
  return request({
    url: '/api/inout/page',
    method: 'get',
    params
  })
}

// 根据商品ID查询出入库记录
export function getInOutRecordByGoodsId(goodsId) {
  return request({
    url: `/api/inout/record/goods/${goodsId}`,
    method: 'get'
  })
}

// 根据时间范围和仓库ID查询出入库记录
export function getInOutRecordByTimeAndWarehouse(params) {
  return request({
    url: '/api/inout/record',
    method: 'get',
    params
  })
}

// 根据ID查询出入库记录详情
export function getInOutRecordById(id) {
  return request({
    url: `/api/inout/${id}`,
    method: 'get'
  })
}

// 商品入库
export function addInRecord(data) {
  return request({
    url: '/api/inout/in',
    method: 'post',
    data
  })
}

// 商品出库
export function addOutRecord(data) {
  return request({
    url: '/api/inout/out',
    method: 'post',
    data
  })
}

// 商品调货
export function addTransferRecord(data) {
  return request({
    url: '/api/inout/transfer',
    method: 'post',
    data
  })
}