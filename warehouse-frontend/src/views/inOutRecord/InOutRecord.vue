<template>
  <div class="inout-container">
    <el-card class="inout-card">
      <div slot="header" class="clearfix">
        <span>出入库记录</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleRefresh">刷新</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.contactPerson" 
          placeholder="请输入联系人" 
          style="width: 200px; margin-right: 10px;"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增记录</el-button>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="recordList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="120">
          <template slot-scope="scope">
            {{ formatId(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column prop="goodsId" label="商品ID" width="120">
          <template slot-scope="scope">
            {{ formatId(scope.row.goodsId) }}
          </template>
        </el-table-column>
        <el-table-column prop="warehouseId" label="仓库ID" width="120">
          <template slot-scope="scope">
            {{ formatId(scope.row.warehouseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : scope.row.type === 2 ? 'danger' : 'warning'">
              {{ scope.row.type === 1 ? '入库' : scope.row.type === 2 ? '出库' : '调货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.operateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleView(scope.row)">查看</el-button>
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
import { getInOutRecordList } from '@/api/inOutRecord'

export default {
  name: 'InOutRecord',
  data() {
    return {
      recordList: [],
      loading: false,
      searchForm: {
        contactPerson: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  created() {
    this.fetchRecordList()
  },
  methods: {
    async fetchRecordList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          contactPerson: this.searchForm.contactPerson
        }
        const res = await getInOutRecordList(params)
        
        // 检查响应结构，兼容不同格式
        if (res && res.data && res.data.records !== undefined) {
          // 标准分页响应格式：{ data: { records: [], total: 0 } }
          this.recordList = res.data.records || []
          this.pagination.total = res.data.total || 0
        } else if (res && res.records !== undefined) {
          // 兼容直接返回分页数据格式：{ records: [], total: 0 }
          this.recordList = res.records || []
          this.pagination.total = res.total || 0
        } else {
          // 其他情况，初始化为空数组
          this.recordList = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('获取出入库记录失败：', error)
        this.$message.error('获取出入库记录失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchRecordList()
    },
    handleRefresh() {
      this.fetchRecordList()
    },
    handleAdd() {
      this.$message.info('新增出入库记录功能待实现')
    },
    handleView(row) {
      this.$message.info(`查看记录：${row.id}`)
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchRecordList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.fetchRecordList()
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
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      
      const date = new Date(dateStr)
      if (isNaN(date.getTime())) {
        return ''
      }
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
  }
}
</script>

<style scoped>
.inout-container {
  width: 100%;
  height: 100%;
}

.inout-card {
  height: 100%;
}

.search-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
</style>