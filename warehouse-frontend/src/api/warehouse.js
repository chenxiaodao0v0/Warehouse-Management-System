// 仓库模块接口封装
import request from '@/utils/request'

/**
 * 获取仓库列表
 * @param {Object} params 查询参数（pageNum, pageSize, warehouseName）
 * @returns Promise
 */
export function getWarehouseList(params) {
  return request({
    url: '/api/warehouse/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取仓库详情
 * @param {String} id 仓库ID
 * @returns Promise
 */
export function getWarehouseById(id) {
  return request({
    url: `/api/warehouse/${id}`,
    method: 'get'
  })
}

/**
 * 新增仓库
 * @param {Object} data 仓库数据
 * @returns Promise
 */
export function addWarehouse(data) {
  return request({
    url: '/api/warehouse/add',
    method: 'post',
    data
  })
}

/**
 * 修改仓库
 * @param {Object} data 仓库数据
 * @returns Promise
 */
export function updateWarehouse(data) {
  return request({
    url: '/api/warehouse/update',
    method: 'put',
    data
  })
}

/**
 * 删除仓库
 * @param {String} id 仓库ID
 * @returns Promise
 */
export function deleteWarehouse(id) {
  return request({
    url: `/api/warehouse/${id}`,
    method: 'delete'
  })
}

/**
 * 根据仓库ID获取商品列表（包含商品详细信息）
 * @param {String} warehouseId 仓库ID
 * @returns Promise
 */
export function getGoodsWithInfoByWarehouseId(warehouseId) {
  return request({
    url: `/api/warehouse-goods/warehouse/info/${warehouseId}`,
    method: 'get'
  })
}