<template>
  <div class="inout-container">
    <el-card class="inout-card">
      <div slot="header" class="clearfix">
        <span>出入库记录</span>
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
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="goodsId" label="商品ID" width="100"></el-table-column>
        <el-table-column prop="warehouseId" label="仓库ID" width="100"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'in' ? 'success' : scope.row.type === 'out' ? 'danger' : 'warning'">
              {{ scope.row.type === 'in' ? '入库' : scope.row.type === 'out' ? '出库' : '调货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
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
        // 这里应该调用获取出入库记录的API
        // const res = await getInOutRecordList(params)
        this.$message.info('出入库记录功能待实现')
        // 临时使用模拟数据
        this.recordList = [
          {
            id: '1',
            goodsId: 'G001',
            warehouseId: 'W001',
            type: 'in',
            quantity: 100,
            contactPerson: '张三',
            phone: '13800138000',
            status: 1,
            createTime: '2025-12-25 10:30:00'
          },
          {
            id: '2',
            goodsId: 'G002',
            warehouseId: 'W002',
            type: 'out',
            quantity: 50,
            contactPerson: '李四',
            phone: '13900139000',
            status: 1,
            createTime: '2025-12-25 11:30:00'
          }
        ]
        this.pagination.total = this.recordList.length
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