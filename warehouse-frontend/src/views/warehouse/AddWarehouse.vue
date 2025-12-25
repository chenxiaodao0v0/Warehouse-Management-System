<template>
  <div class="warehouse-container">
    <el-card class="warehouse-card">
      <div slot="header" class="clearfix">
        <span>仓库管理</span>
        <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.warehouseName" 
          placeholder="请输入仓库名称" 
          style="width: 200px; margin-right: 10px;"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增仓库</el-button>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="warehouseList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="warehouseName" label="仓库名称" width="150"></el-table-column>
        <el-table-column prop="warehouseCode" label="仓库编码" width="120"></el-table-column>
        <el-table-column prop="location" label="位置" width="200"></el-table-column>
        <el-table-column prop="manager" label="负责人" width="100"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
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
import { getWarehouseList } from '@/api/warehouse'

export default {
  name: 'AddWarehouse',
  data() {
    return {
      warehouseList: [],
      loading: false,
      searchForm: {
        warehouseName: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.fetchWarehouseList()
  },
  methods: {
    async fetchWarehouseList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          warehouseName: this.searchForm.warehouseName
        }
        const res = await getWarehouseList(params)
        this.warehouseList = res.records || []
        this.pagination.total = res.total
      } catch (error) {
        console.error('获取仓库列表失败：', error)
        this.$message.error('获取仓库列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchWarehouseList()
    },
    handleAdd() {
      this.$message.info('新增仓库功能待实现')
    },
    handleEdit(row) {
      this.$message.info(`编辑仓库：${row.warehouseName}`)
    },
    handleDelete(id) {
      this.$confirm('确定要删除这个仓库吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 这里应该调用删除API
          this.$message.success('删除成功')
          this.fetchWarehouseList()
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchWarehouseList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.fetchWarehouseList()
    }
  }
}
</script>

<style scoped>
.warehouse-container {
  width: 100%;
  height: 100%;
}

.warehouse-card {
  height: 100%;
}

.search-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>