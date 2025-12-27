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
import { getGoodsWithInfoByWarehouseId } from '@/api/warehouse'

export default {
  name: 'WarehouseInventoryDetail',
  data() {
    return {
      warehouseId: '',
      warehouseName: '',
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
        console.log('开始获取仓库详情，ID:', this.warehouseId);
        const response = await getWarehouseById(this.warehouseId);
        console.log('仓库详情响应:', response);
        
        // 检查响应结构，兼容直接返回对象和标准响应格式
        if (response && (response.code === 200 || (!response.code && response.warehouseName))) {
          // 如果是标准格式 {code: 200, data: {...}}
          if (response.code === 200 && response.data) {
            this.warehouseName = response.data.warehouseName || 
                                response.data.name || 
                                response.data.warehouse_name || 
                                `仓库 ${this.warehouseId}`;
          } 
          // 如果是直接返回对象格式
          else if (response.warehouseName) {
            this.warehouseName = response.warehouseName;
          } 
          // 其他情况使用默认值
          else {
            this.warehouseName = response.warehouseName || `仓库 ${this.warehouseId}`;
          }
          
          console.log('设置仓库名称为:', this.warehouseName);
        } else {
          // 如果后端返回错误信息，记录但不显示错误消息
          const errorMessage = response?.message || response?.msg || '获取仓库信息失败';
          console.error('获取仓库信息响应错误:', errorMessage);
          this.warehouseName = `仓库 ${this.warehouseId}`;
        }
      } catch (error) {
        console.error('获取仓库详情失败:', error);
        // 出错时也使用默认名称，不显示错误消息
        this.warehouseName = `仓库 ${this.warehouseId}`;
      }
    },
    
    async fetchGoodsList() {
      try {
        this.loading = true;
        console.log('开始获取商品列表，仓库ID:', this.warehouseId);
        // 使用正确的API接口，获取包含商品详细信息的数据
        const response = await getGoodsWithInfoByWarehouseId(this.warehouseId);
        console.log('商品列表响应:', response);
        
        // 检查响应格式，兼容多种可能的格式
        let data = [];
        
        // 情况1: 标准格式 {code: 200, data: [...]}
        if (response && response.code === 200 && response.data) {
          data = response.data;
        } 
        // 情况2: 直接返回数组
        else if (Array.isArray(response)) {
          data = response;
        }
        // 情况3: 非标准格式但包含data字段
        else if (response && response.data && Array.isArray(response.data)) {
          data = response.data;
        } 
        // 情况4: 作为后备，检查response本身是否为数组
        else {
          console.warn('响应格式不符合预期，尝试直接使用响应数据或显示错误');
          this.$message.error('获取商品列表响应格式异常');
          this.goodsList = [];
          this.loading = false; // 确保即使在异常情况下也关闭加载状态
          return;
        }
        
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
      } catch (error) {
        console.error('获取商品列表失败:', error);
        this.$message.error('获取商品列表失败: ' + (error.message || '未知错误'));
        this.goodsList = [];
      } finally {
        this.loading = false; // 无论成功还是失败，都要关闭加载状态
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
      
      // 检查是否为绝对路径（以/开头）
      if (imagePath.startsWith('/')) {
        // 从API请求的baseUrl获取正确的端口
        // 使用与API请求相同的基础URL，而不是window.location.origin
        let baseUrl = process.env.VUE_APP_API_BASE_URL;
        
        // 如果环境变量未设置，则尝试从已知的API请求中获取基础URL
        if (!baseUrl) {
          // 默认使用与前端同端口的API服务器，或者根据项目实际情况设置
          baseUrl = 'http://localhost:8080'; // 根据之前的请求，后端端口是8080
        }
        
        // 避免重复斜杠
        return `${baseUrl}${imagePath}`;
      }
      
      // 如果是相对路径，拼接基础API路径
      let baseUrl = process.env.VUE_APP_API_BASE_URL;
      if (!baseUrl) {
        baseUrl = 'http://localhost:8080';
      }
      return `${baseUrl}/${imagePath}`;
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
  border: 1px solid #dcdfe6;
}
</style>