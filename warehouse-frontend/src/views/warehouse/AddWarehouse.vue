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
        <el-table-column prop="id" label="仓库ID" width="150">
          <template slot-scope="scope">
            {{ formatId(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column prop="warehouseName" label="仓库名称" width="150"></el-table-column>
        <el-table-column prop="address" label="仓库地址" width="200"></el-table-column>
        <el-table-column prop="contact" label="负责人" width="100"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
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
        
        console.log('仓库列表响应:', res) // 添加调试信息
        
        // 检查响应结构，兼容不同格式
        if (res && res.data && res.data.records !== undefined) {
          // 标准分页响应格式：{ data: { records: [], total: 0 } }
          this.warehouseList = res.data.records || []
          this.pagination.total = res.data.total || 0
        } else if (res && res.records !== undefined) {
          // 兼容直接返回分页数据格式：{ records: [], total: 0 }
          this.warehouseList = res.records || []
          this.pagination.total = res.total || 0
        } else {
          // 其他情况，初始化为空数组
          this.warehouseList = []
          this.pagination.total = 0
        }
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
    },
    // 格式化ID显示，截取UUID的后8位
    formatId(id) {
      if (!id) return ''
      // 如果是UUID格式，截取后8位，否则返回原ID
      if (id.length > 8) {
        return '...' + id.slice(-8)
      }
      return id
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