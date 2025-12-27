<template>
  <div class="inventory-container">
    <h2 class="page-title">仓库库存总览</h2>
    <div class="warehouse-grid">
      <div 
        v-for="warehouse in warehouses" 
        :key="warehouse.id" 
        class="warehouse-card"
      >
        <div class="warehouse-header">
          <h3 class="warehouse-name">{{ warehouse.warehouseName || warehouse.name }}</h3>
          <span class="warehouse-status" :class="warehouse.status === 1 ? 'status-active' : 'status-inactive'">
            {{ warehouse.status === 1 ? '运营中' : '已停用' }}
          </span>
        </div>
        <div class="warehouse-details">
          <p class="detail-item">
            <span class="label">仓库编号:</span>
            <span class="value">{{ warehouse.warehouseNo || warehouse.no || warehouse.id }}</span>
          </p>
          <p class="detail-item">
            <span class="label">位置:</span>
            <span class="value">{{ warehouse.location || warehouse.address || '未设置' }}</span>
          </p>
          <p class="detail-item">
            <span class="label">库存总量:</span>
            <span class="value">{{ warehouse.totalStock || 0 }} 件</span>
          </p>
          <p class="detail-item">
            <span class="label">商品种类:</span>
            <span class="value">{{ warehouse.goodsCategoryCount || 0 }} 种</span>
          </p>
          <p class="detail-item">
            <span class="label">商品总数:</span>
            <span class="value">{{ warehouse.totalGoodsCount || 0 }} 件</span>
          </p>
        </div>
        <div class="warehouse-actions">
          <button class="btn-enter" @click="enterWarehouse(warehouse.id)">
            进入仓库
          </button>
        </div>
      </div>
    </div>

    <!-- 加载提示 -->
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && warehouses.length === 0" class="empty-state">
      <p>暂无仓库信息</p>
    </div>
  </div>
</template>

<script>
import { getWarehouseList } from '@/api/warehouse'
import request from '@/utils/request' // 使用request直接调用仓库-商品关联接口

export default {
  name: 'InventoryOverview',
  data() {
    return {
      warehouses: [],
      loading: false
    }
  },
  created() {
    this.fetchWarehouses()
  },
  methods: {
    async fetchWarehouses() {
      this.loading = true
      try {
        console.log('开始获取仓库列表...')
        const response = await getWarehouseList({ pageNum: 1, pageSize: 100 })
        console.log('仓库列表API响应:', response)
        
        // 检查响应结构 - 根据控制台输出，响应直接包含分页数据
        let warehouseRecords = []
        
        if (response && response.records) {
          // 直接返回分页数据结构
          warehouseRecords = response.records
        } else if (response && response.data && response.data.records) {
          // 响应包装在data中
          warehouseRecords = response.data.records
        } else if (Array.isArray(response)) {
          // 直接返回数组
          warehouseRecords = response
        } else if (response && response.code === 200 && response.data && response.data.records) {
          // 标准响应格式
          warehouseRecords = response.data.records
        }
        
        console.log(`获取到 ${warehouseRecords.length} 个仓库`)
        
        // 为每个仓库添加商品统计信息
        this.warehouses = await Promise.all(
          warehouseRecords.map(async (warehouse) => {
            console.log(`处理仓库: ${warehouse.id || warehouse.warehouseId}`)
            // 获取该仓库的商品库存信息以统计数量
            try {
              // 使用正确的API路径，包含/api前缀
              const goodsRes = await request({
                url: `/api/warehouse-goods/warehouse/${warehouse.id || warehouse.warehouseId}`,
                method: 'get'
              })
              
              console.log(`仓库 ${warehouse.id || warehouse.warehouseId} 仓库-商品关联API响应:`, goodsRes)
              
              if (goodsRes && (goodsRes.code === 200 || Array.isArray(goodsRes.data) || Array.isArray(goodsRes))) {
                // 兼容不同的响应结构
                let warehouseGoodsList = []
                if (Array.isArray(goodsRes.data)) {
                  warehouseGoodsList = goodsRes.data
                } else if (Array.isArray(goodsRes)) {
                  warehouseGoodsList = goodsRes
                } else if (goodsRes.code === 200 && goodsRes.data) {
                  warehouseGoodsList = goodsRes.data
                }
                
                console.log(`仓库 ${warehouse.id || warehouse.warehouseId} 有 ${warehouseGoodsList.length} 条库存记录`)
                
                // 计算库存总量
                const totalStock = warehouseGoodsList.reduce((sum, item) => sum + (item.stock || 0), 0)
                
                // 获取商品种类数（通过关联的商品ID去重）
                const uniqueGoodsIds = [...new Set(warehouseGoodsList.map(wg => wg.goodsId))].filter(Boolean)
                
                return {
                  ...warehouse,
                  totalStock: totalStock,
                  goodsCategoryCount: uniqueGoodsIds.length,
                  totalGoodsCount: totalStock
                }
              }
            } catch (err) {
              console.error(`获取仓库 ${warehouse.id || warehouse.warehouseId} 商品库存信息失败`, err)
            }
            
            return {
              ...warehouse,
              totalStock: 0,
              goodsCategoryCount: 0,
              totalGoodsCount: 0
            }
          })
        )
        
        console.log('最终处理后的仓库列表:', this.warehouses)
      } catch (error) {
        console.error('获取仓库列表失败:', error)
        this.$message.error('获取仓库列表失败')
      } finally {
        this.loading = false
      }
    },
    enterWarehouse(warehouseId) {
      console.log('进入仓库:', warehouseId)
      this.$router.push(`/warehouse/detail/${warehouseId}`)
    }
  }
}
</script>

<style scoped>
.inventory-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
  text-align: center;
}

.warehouse-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.warehouse-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  transition: all 0.3s ease;
}

.warehouse-card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.warehouse-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.warehouse-name {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.warehouse-status {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background-color: #f0f9ff;
  color: #409eff;
  border: 1px solid #b3d8ff;
}

.status-inactive {
  background-color: #fcf0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.warehouse-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  color: #606266;
}

.label {
  color: #909399;
}

.value {
  color: #303133;
  font-weight: 500;
}

.warehouse-actions {
  text-align: right;
}

.btn-enter {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.btn-enter:hover {
  background-color: #66b1ff;
}

.loading, .empty-state {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}
</style>