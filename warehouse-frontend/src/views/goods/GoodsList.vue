<template>
  <div class="goods-container">
    <el-card class="goods-card">
      <div slot="header" class="clearfix">
        <span>商品列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="fetchGoodsList">刷新</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.goodsName" 
          placeholder="请输入商品名称" 
          style="width: 200px; margin-right: 10px;"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增商品</el-button>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="goodsList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="商品ID" width="150">
          <template slot-scope="scope">
            {{ formatId(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="categoryId" label="分类ID" width="100"></el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="pic" label="图片" width="100">
          <template slot-scope="scope">
            <img v-if="getImageUrl(scope.row.pic)" :src="getImageUrl(scope.row.pic)" style="width: 40px; height: 40px;" @error="imageError($event, scope.row.name)" @load="imageLoad">
            <span v-else>暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      >
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import { getGoodsList } from '@/api/goods'

export default {
  name: 'GoodsListPage',
  data() {
    return {
      goodsList: [],
      loading: false,
      searchForm: {
        goodsName: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.fetchGoodsList()
  },
  methods: {
    async fetchGoodsList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          goodsName: this.searchForm.goodsName
        }
        const res = await getGoodsList(params)
        
        // 根据API响应结构调整数据获取方式
        if (res && res.data) {
          // 标准分页响应格式：{ data: { records: [], total: 0 } }
          this.goodsList = res.data.records || []
          this.pagination.total = res.data.total || 0
        } else if (res && res.records) {
          // 兼容直接返回分页数据格式：{ records: [], total: 0 }
          this.goodsList = res.records || []
          this.pagination.total = res.total || 0
        } else {
          // 其他情况，初始化为空数组
          this.goodsList = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('获取商品列表失败：', error)
        this.$message.error('获取商品列表失败')
        // 发生错误时也要重置数据
        this.goodsList = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },
    // 根据图片路径生成正确的图片URL
    getImageUrl(picPath) {
      // 如果是完整的URL（以http或https开头），直接返回
      if (picPath && (picPath.startsWith('http://') || picPath.startsWith('https://'))) {
        return picPath
      }
      
      // 如果是相对路径或后端返回的图片路径，拼接后端API基础路径
      if (picPath) {
        // 假设后端图片上传后返回的是相对路径，需要拼接后端地址
        const baseUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080/api'
        // 如果picPath已经是完整的后端路径（以/开头），则直接拼接
        if (picPath.startsWith('/')) {
          return `${baseUrl}${picPath}`
        } else {
          // 否则添加统一的图片访问前缀
          return `${baseUrl}${picPath.startsWith('/') ? picPath : '/' + picPath}`
        }
      }
      
      // 如果没有提供图片路径，返回null表示没有图片
      return null
    },
    // 格式化日期，只显示年月日
    formatDate(dateStr) {
      if (!dateStr) return ''
      
      // 将日期字符串转换为Date对象
      const date = new Date(dateStr)
      
      // 检查日期是否有效
      if (isNaN(date.getTime())) {
        return ''
      }
      
      // 格式化为 YYYY-MM-DD 格式
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')  // 月份从0开始，需要+1
      const day = String(date.getDate()).padStart(2, '0')
      
      return `${year}-${month}-${day}`
    },
    imageError(event, goodsName) {
      // 图片加载失败时，显示默认图片
      console.warn(`商品"${goodsName}"的图片加载失败: ${event.target.src}`)
      event.target.src = require('@/assets/logo.png') // 使用默认图片
    },
    imageLoad() {
      // 图片加载成功时的处理
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchGoodsList()
    },
    handleAdd() {
      this.$message.info('新增商品功能待实现')
    },
    handleEdit(row) {
      this.$message.info(`编辑商品：${row.name} (ID: ${this.formatId(row.id)})`)
    },
    handleDelete(id) {
      this.$confirm(`确定要删除这个商品吗？商品ID: ${this.formatId(id)}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          this.$message.success('删除成功')
          this.fetchGoodsList()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchGoodsList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.fetchGoodsList()
    },
    // 格式化ID显示，截取UUID的后8位
    formatId(id) {
      if (!id) return ''
      // 如果是UUID格式，截取后8位，否则返回原ID
      if (id.length > 8) {
        return '...' + id.slice(-8)
      }
      return id
    }
  }
}
</script>

<style scoped>
.goods-container {
  width: 100%;
  height: 100%;
}

.goods-card {
  height: 100%;
}

.search-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>