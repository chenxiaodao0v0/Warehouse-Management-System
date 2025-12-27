<template>
  <div class="warehouse-detail-container">
    <div class="header-section">
      <el-page-header @back="goBack" content="仓库库存详情"></el-page-header>
      <h2 class="warehouse-name">{{ warehouseName }}</h2>
      <div class="warehouse-stats">
        <div class="stat-card">
          <div class="stat-value">{{ goodsList.length }}</div>
          <div class="stat-label">商品种类</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ totalStock }}</div>
          <div class="stat-label">总库存量</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ totalValue }}</div>
          <div class="stat-label">总价值</div>
        </div>
      </div>
    </div>

    <div class="content-section">
      <el-table
        :data="goodsList"
        stripe
        style="width: 100%"
        v-loading="loading"
        :empty-text="'暂无数据'"
      >
        <el-table-column prop="goodsName" label="商品名称" width="200">
          <template slot-scope="scope">
            <div class="goods-info">
              <img 
                v-if="scope.row.image" 
                :src="getImageUrl(scope.row.image)" 
                class="goods-image" 
                @error="handleImageError"
                alt="商品图片"
              />
              <img 
                v-else 
                src="@/assets/logo.png" 
                class="goods-image"
                alt="默认图片"
              />
              <span>{{ scope.row.goodsName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="goodsNo" label="商品编号" width="150"></el-table-column>
        <el-table-column prop="categoryName" label="商品类别" width="120"></el-table-column>
        <el-table-column prop="specification" label="规格" width="120"></el-table-column>
        <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        <el-table-column prop="stock" label="库存数量" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price ? Number(scope.row.price).toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button 
              size="mini" 
              type="primary"
              @click="editGoods(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="mini" 
              type="success"
              @click="addStock(scope.row)"
            >
              入库
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getWarehouseById } from '@/api/warehouse'
import { getGoodsByWarehouseId } from '@/api/goods'

export default {
  name: 'WarehouseInventoryDetail',
  data() {
    return {
      warehouseId: '',
      warehouseName: '加载中...',
      goodsList: [],
      loading: false
    }
  },
  computed: {
    totalStock() {
      if (!this.goodsList || this.goodsList.length === 0) return 0;
      return this.goodsList.reduce((sum, item) => {
        const stock = item.stock || 0;
        return sum + stock;
      }, 0);
    },
    totalValue() {
      if (!this.goodsList || this.goodsList.length === 0) return '0.00';
      const total = this.goodsList.reduce((sum, item) => {
        const price = item.price || 0;
        const stock = item.stock || 0;
        return sum + (parseFloat(price) * stock);
      }, 0);
      return total.toFixed(2);
    }
  },
  async created() {
    this.warehouseId = this.$route.params.id;
    if (!this.warehouseId) {
      this.$message.error('仓库ID不能为空');
      return;
    }
    await this.fetchWarehouseDetail();
    await this.fetchGoodsList();
  },
  methods: {
    async fetchWarehouseDetail() {
      try {
        this.loading = true;
        console.log('开始获取仓库详情，ID:', this.warehouseId);
        const response = await getWarehouseById(this.warehouseId);
        console.log('仓库详情响应:', response);
        
        if (response && response.code === 200) {
          this.warehouseName = response.data && response.data.warehouseName ? response.data.warehouseName : '未知仓库';
        } else {
          this.$message.error(response?.message || '获取仓库信息失败');
        }
      } catch (error) {
        console.error('获取仓库详情失败:', error);
        this.$message.error('获取仓库信息失败: ' + (error.message || '未知错误'));
      }
    },
    
    async fetchGoodsList() {
      try {
        this.loading = true;
        console.log('开始获取商品列表，仓库ID:', this.warehouseId);
        const response = await getGoodsByWarehouseId(this.warehouseId);
        console.log('商品列表响应:', response);
        
        if (response && response.code === 200) {
          // 确保数据是数组且包含有效数据
          const data = response.data || [];
          if (Array.isArray(data)) {
            // 确保所有数据都具有正确的格式
            this.goodsList = data.map(item => ({
              goodsName: item.goodsName || '未知商品',
              goodsNo: item.goodsNo || 'N/A',
              categoryName: item.categoryName || '未分类',
              specification: item.specification || '',
              unit: item.unit || '个',
              stock: item.stock !== undefined && item.stock !== null ? item.stock : 0,
              price: item.price !== undefined && item.price !== null ? item.price : 0,
              image: item.image || ''
            }));
            console.log('处理后的商品列表:', this.goodsList);
          } else {
            this.goodsList = [];
          }
        } else {
          this.$message.error(response?.message || '获取商品列表失败');
          this.goodsList = [];
        }
      } catch (error) {
        console.error('获取商品列表失败:', error);
        this.$message.error('获取商品列表失败: ' + (error.message || '未知错误'));
        this.goodsList = [];
      } finally {
        this.loading = false;
      }
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    getImageUrl(imagePath) {
      if (!imagePath) {
        return require('@/assets/logo.png');
      }
      
      // 判断是否为完整URL
      if (imagePath.startsWith('http') || imagePath.startsWith('//')) {
        return imagePath;
      }
      
      // 拼接基础API路径
      const baseUrl = process.env.VUE_APP_API_BASE_URL || '';
      const path = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      return `${baseUrl}${path}`;
    },
    
    handleImageError(event) {
      event.target.src = require('@/assets/logo.png');
    },
    
    editGoods(goods) {
      // 这里可以跳转到商品编辑页面
      this.$router.push(`/goods/edit/${goods.goodsId || goods.id}`)
    },
    
    addStock(goods) {
      // 这里可以跳转到入库操作页面
      this.$router.push(`/inoutrecord/add?goodsId=${goods.goodsId || goods.id}&warehouseId=${this.warehouseId}`)
    }
  }
}
</script>

<style scoped>
.warehouse-detail-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.warehouse-name {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin: 20px 0;
}

.warehouse-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px;
  min-width: 120px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.content-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.goods-info {
  display: flex;
  align-items: center;
}

.goods-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  margin-right: 10px;
  border-radius: 4px;
}

</style>