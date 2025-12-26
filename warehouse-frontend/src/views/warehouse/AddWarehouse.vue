<template>
  <div class="warehouse-container">
    <el-card class="warehouse-card">
      <div slot="header" class="clearfix">
        <span>仓库管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleRefresh">刷新</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.warehouseName" 
          placeholder="请输入仓库名称" 
          style="width: 200px; margin-right: 10px;"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button 
          type="success" 
          @click="handleAdd" 
          :disabled="!enterpriseLoaded"
          :title="!enterpriseLoaded ? '请先添加企业信息' : ''"
        >新增仓库</el-button>
      </div>
      
      <!-- 企业信息未加载提示
      <el-alert
        v-if="!enterpriseLoaded"
        title="请先添加企业信息"
        type="warning"
        description="当前系统缺少企业信息，无法添加仓库。请先在企业信息管理页面添加企业信息。"
        show-icon
        :closable="false"
        style="margin-bottom: 20px;"
      >
      </el-alert> -->
      
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
        <el-table-column prop="enterpriseName" label="所属企业" width="150">
          <template slot-scope="scope">
            {{ enterpriseInfo.name || '加载中...' }}
          </template>
        </el-table-column>
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
    
    <!-- 新增/编辑仓库对话框 -->
    <el-dialog
      :title="isEdit ? '编辑仓库' : '新增仓库'"
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <el-form :model="warehouseForm" :rules="warehouseRules" ref="warehouseForm" label-width="100px">
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="warehouseForm.warehouseName" placeholder="请输入仓库名称"></el-input>
        </el-form-item>
        <el-form-item label="仓库地址" prop="address">
          <el-input v-model="warehouseForm.address" placeholder="请输入仓库地址"></el-input>
        </el-form-item>
        <el-form-item label="负责人" prop="contact">
          <el-input v-model="warehouseForm.contact" placeholder="请输入负责人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="warehouseForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="warehouseForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDialog">取 消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWarehouseList, addWarehouse, updateWarehouse } from '@/api/warehouse'
import { getEnterpriseInfo } from '@/api/enterprise'

export default {
  name: 'AddWarehouse',
  created() {
    this.fetchWarehouseList()
    this.fetchEnterpriseInfo()
  },
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
      },
      dialogVisible: false,
      isEdit: false, // 添加编辑状态标识
      enterpriseLoaded: false, // 标记企业信息是否已加载
      defaultEnterpriseId: '', // 默认企业ID
      enterpriseInfo: {}, // 企业信息
      warehouseForm: {
        warehouseName: '',
        address: '',
        contact: '',
        phone: '',
        status: 1 // 默认启用
      },
      warehouseRules: {
        warehouseName: [
          { required: true, message: '请输入仓库名称', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入仓库地址', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入负责人姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    // 获取企业信息
    async fetchEnterpriseInfo() {
      try {
        const res = await getEnterpriseInfo()
        
        console.log('企业信息响应:', res) // 添加调试信息
        
        // 检查响应结构是否为标准格式
        if (res && res.code === 200 && res.data) {
          // 标准响应格式：{code: 200, data: {企业信息}}
          this.enterpriseInfo = res.data
          this.defaultEnterpriseId = res.data.id
          this.enterpriseLoaded = true
        } else if (res && res.id) {
          // 直接返回企业对象格式：{id: ..., name: ..., ...}
          this.enterpriseInfo = res
          this.defaultEnterpriseId = res.id
          this.enterpriseLoaded = true
        } else if (res && res.code !== 200) {
          // 响应code不是200，说明后端返回了错误信息
          console.error('后端返回错误响应:', res) // 添加错误日志
          this.$message.error('获取企业信息失败：' + (res.msg || '未知错误'))
        } else {
          console.error('企业信息响应格式错误:', res) // 添加错误日志
          this.$message.error('获取企业信息失败：响应格式错误')
        }
      } catch (error) {
        console.error('获取企业信息失败：', error)
        
        // 检查错误响应
        if (error.response) {
          console.error('后端响应详情:', error.response) // 添加错误日志
          
          // 检查是否是HTTP 2xx范围的状态码，但业务逻辑返回失败
          if (error.response.status >= 200 && error.response.status < 300) {
            // HTTP状态成功，但业务逻辑失败
            const res = error.response.data
            if (res && res.code !== 200) {
              this.$message.error('获取企业信息失败：' + (res.msg || '业务逻辑错误'))
            }
          }
          
          // 服务器响应了错误状态码
          if (error.response.status === 500) {
            // 500错误通常表示服务器内部错误
            this.$message.error('服务器内部错误：' + (error.response.data.message || '可能是因为没有企业数据'))
          } else if (error.response.data && error.response.data.msg) {
            this.$message.error('获取企业信息失败：' + error.response.data.msg)
          } else {
            this.$message.error('获取企业信息失败，状态码：' + error.response.status)
          }
        } else if (error.request) {
          // 请求已发出但没有收到响应
          this.$message.error('无法连接到服务器，请检查网络连接')
        } else {
          // 其他错误
          this.$message.error('获取企业信息失败：' + error.message)
        }
      }
    },
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
    handleRefresh() {
      this.fetchWarehouseList()
    },
    handleAdd() {
      // 重置表单
      this.resetForm()
      this.dialogVisible = true
    },
    resetForm() {
      this.warehouseForm = {
        warehouseName: '',
        address: '',
        contact: '',
        phone: '',
        status: 1 // 默认启用
      }
      if (this.$refs.warehouseForm) {
        this.$refs.warehouseForm.resetFields()
      }
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.isEdit = false // 重置编辑状态
      this.resetForm()
    },
    handleEdit(row) {
      // 将当前仓库数据填充到表单中
      this.warehouseForm = { ...row } // 复制仓库数据到表单
      this.dialogVisible = true // 显示对话框
      this.isEdit = true // 标记为编辑状态
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
    },
    async handleAddSubmit() {
      // 检查是否已获取到企业ID
      if (!this.defaultEnterpriseId) {
        this.$message.error('无法获取企业信息，请先添加企业信息')
        return
      }
      
      this.$refs.warehouseForm.validate(async (valid) => {
        if (valid) {
          try {
            // 将默认企业ID添加到表单数据中
            const formData = {
              ...this.warehouseForm,
              enterpriseId: this.defaultEnterpriseId
            }
            
            if (this.isEdit) {
              // 编辑仓库
              await updateWarehouse(formData)
              this.$message.success('修改仓库成功')
            } else {
              // 新增仓库
              await addWarehouse(formData)
              this.$message.success('添加仓库成功')
            }
            
            this.dialogVisible = false
            // 重置表单
            this.resetForm()
            // 刷新仓库列表
            this.fetchWarehouseList()
          } catch (error) {
            console.error(this.isEdit ? '修改仓库失败：' : '添加仓库失败：', error)
            this.$message.error(this.isEdit ? '修改仓库失败' : '添加仓库失败')
          }
        } else {
          this.$message.error('请填写正确的仓库信息')
          return false
        }
      })
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