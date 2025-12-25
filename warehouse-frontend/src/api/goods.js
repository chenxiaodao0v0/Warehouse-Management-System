// 商品模块接口封装
import request from '@/utils/request'

/**
 * 获取商品列表
 * @param {Object} params 查询参数（pageNum, pageSize, goodsName）
 * @returns Promise
 */
export function getGoodsList(params) {
  return request({
    url: '/goods/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取商品详情
 * @param {String} id 商品ID
 * @returns Promise
 */
export function getGoodsById(id) {
  return request({
    url: `/goods/${id}`,
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
    url: '/goods/add',
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
    url: '/goods/update',
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
    url: `/goods/${id}`,
    method: 'delete'
  })
}