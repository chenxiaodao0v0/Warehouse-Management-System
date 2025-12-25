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
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="categoryId" label="分类ID" width="100"></el-table-column>
        <el-table-column prop="warehouseId" label="仓库ID" width="100"></el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
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
export default {
  name: 'GoodsList',
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
        const res = await this.$http.get('/goods/list', { params })
        this.goodsList = res.records || []
        this.pagination.total = res.total
      } catch (error) {
        console.error('获取商品列表失败：', error)
        this.$message.error('获取商品列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchGoodsList()
    },
    handleAdd() {
      this.$message.info('新增商品功能待实现')
    },
    handleEdit(row) {
      this.$message.info(`编辑商品：${row.name}`)
    },
    handleDelete(id) {
      this.$confirm('确定要删除这个商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用删除API
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
