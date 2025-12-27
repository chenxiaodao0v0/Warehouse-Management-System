// 商品模块接口封装
import request from '@/utils/request'

/**
 * 分页查询商品列表
 * @param {Object} params 查询参数（pageNum, pageSize, goodsName）
 * @returns Promise
 */
export function getGoodsList(params) {
  return request({
    url: '/api/goods/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询商品详情
 * @param {String} id 商品ID
 * @returns Promise
 */
export function getGoodsById(id) {
  return request({
    url: `/api/goods/${id}`,
    method: 'get'
  })
}

/**
 * 新增商品
 * @param {Object} data 商品数据
 * @returns Promise
 */
export function addGoods(data) {
  return request({
    url: '/api/goods/add',
    method: 'post',
    data
  })
}

/**
 * 修改商品
 * @param {Object} data 商品数据
 * @returns Promise
 */
export function updateGoods(data) {
  return request({
    url: '/api/goods/update',
    method: 'put',
    data
  })
}

/**
 * 删除商品
 * @param {String} id 商品ID
 * @returns Promise
 */
export function deleteGoods(id) {
  return request({
    url: `/api/goods/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除商品
 * @param {Array} ids 商品ID数组
 * @returns Promise
 */
export function batchDeleteGoods(ids) {
  return request({
    url: '/api/goods/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据商品名称模糊查询
 * @param {String} goodsName 商品名称
 * @returns Promise
 */
export function getGoodsByName(goodsName) {
  return request({
    url: '/api/goods/name',
    method: 'get',
    params: { goodsName }
  })
}

/**
 * 根据仓库ID查询商品列表
 * @param {String} warehouseId 仓库ID
 * @returns Promise
 */
export function getGoodsByWarehouseId(warehouseId) {
  return request({
    url: `/api/goods/warehouse/${warehouseId}`,
    method: 'get'
  })
}

/**
 * 上传商品图片
 * @param {FormData} data 包含图片文件的表单数据
 * @returns Promise
 */
export function uploadGoodsImg(data) {
  return request({
    url: '/api/goods/upload',
    method: 'post',
    data
  })
}