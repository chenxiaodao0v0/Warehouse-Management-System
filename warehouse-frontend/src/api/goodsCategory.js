// 商品分类模块接口封装
import request from '@/utils/request'

/**
 * 获取商品分类列表
 * @returns Promise
 */
export function getCategoryList() {
  return request({
    url: '/api/goods/category/list',
    method: 'get'
  })
}

/**
 * 分页查询商品分类列表
 * @param {Object} params 查询参数（pageNum, pageSize, name）
 * @returns Promise
 */
export function getCategoryPage(params) {
  return request({
    url: '/api/goods/category/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询商品分类详情
 * @param {String} id 分类ID
 * @returns Promise
 */
export function getCategoryById(id) {
  return request({
    url: `/api/goods/category/${id}`,
    method: 'get'
  })
}

/**
 * 新增商品分类
 * @param {Object} data 分类数据
 * @returns Promise
 */
export function addCategory(data) {
  return request({
    url: '/api/goods/category/add',
    method: 'post',
    data
  })
}

/**
 * 修改商品分类
 * @param {Object} data 分类数据
 * @returns Promise
 */
export function updateCategory(data) {
  return request({
    url: '/api/goods/category/update',
    method: 'put',
    data
  })
}

/**
 * 删除商品分类
 * @param {String} id 分类ID
 * @returns Promise
 */
export function deleteCategory(id) {
  return request({
    url: `/api/goods/category/${id}`,
    method: 'delete'
  })
}