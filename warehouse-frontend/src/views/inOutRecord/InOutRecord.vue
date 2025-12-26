<template>
  <div class="inout-container">
    <el-card class="inout-card">
      <div slot="header" class="clearfix">
        <span>出入库记录</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="fetchRecordList">刷新</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.contactPerson" 
          placeholder="请输入对接人" 
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
        <el-table-column prop="id" label="ID" width="100"></el-table-column>
        <el-table-column prop="goodsId" label="商品ID" width="100"></el-table-column>
        <el-table-column prop="warehouseId" label="仓库ID" width="100"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : scope.row.type === 2 ? 'danger' : 'warning'">
              {{ scope.row.type === 1 ? '入库' : scope.row.type === 2 ? '出库' : '调货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
        <el-table-column prop="contactPerson" label="对接人" width="100"></el-table-column>
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
    
    <!-- 新增/编辑出入库记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :before-close="handleCloseDialog"
    >
      <el-form :model="recordForm" :rules="recordRules" ref="recordForm" label-width="120px">
        <el-form-item label="操作类型" prop="type">
          <el-radio-group v-model="recordForm.type" @change="handleTypeChange">
            <el-radio :label="1">入库</el-radio>
            <el-radio :label="2">出库</el-radio>
            <el-radio :label="3">调货</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="商品ID" prop="goodsId">
          <el-input v-model="recordForm.goodsId" placeholder="请输入商品ID"></el-input>
        </el-form-item>
        
        <el-form-item label="操作仓库ID" prop="warehouseId">
          <el-input v-model="recordForm.warehouseId" placeholder="请输入操作仓库ID"></el-input>
        </el-form-item>
        
        <el-form-item 
          v-if="recordForm.type === 3" 
          label="目标仓库ID" 
          prop="relatedWarehouseId">
          <el-input 
            v-model="recordForm.relatedWarehouseId" 
            placeholder="请输入调货目标仓库ID">
          </el-input>
        </el-form-item>
        
        <el-form-item label="操作数量" prop="quantity">
          <el-input-number 
            v-model="recordForm.quantity" 
            :min="1" 
            placeholder="请输入操作数量">
          </el-input-number>
        </el-form-item>
        
        <el-form-item label="对接人" prop="contactPerson">
          <el-input v-model="recordForm.contactPerson" placeholder="请输入对接人姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="recordForm.contactPhone" placeholder="请输入对接人联系电话"></el-input>
        </el-form-item>
        
        <el-form-item label="操作备注">
          <el-input 
            v-model="recordForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入操作备注">
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDialog">取 消</el-button>
        <el-button type="primary" @click="handleFormSubmit">确 定</el-button>
      </div>
    </el-dialog>
    
    <!-- 查看出入库记录详情对话框 -->
    <el-dialog
      title="出入库记录详情"
      :visible.sync="detailDialogVisible"
      width="600px"
      :before-close="handleCloseDetailDialog"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="记录ID">{{ recordDetail.id }}</el-descriptions-item>
        <el-descriptions-item label="商品ID">{{ recordDetail.goodsId }}</el-descriptions-item>
        <el-descriptions-item label="操作仓库ID">{{ recordDetail.warehouseId }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="recordDetail.type === 1 ? 'success' : recordDetail.type === 2 ? 'danger' : 'warning'">
            {{ recordDetail.type === 1 ? '入库' : recordDetail.type === 2 ? '出库' : '调货' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标仓库ID" v-if="recordDetail.type === 3">
          {{ recordDetail.relatedWarehouseId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作数量">{{ recordDetail.quantity }}</el-descriptions-item>
        <el-descriptions-item label="对接人">{{ recordDetail.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ recordDetail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="操作备注">{{ recordDetail.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDate(recordDetail.operateTime) }}</el-descriptions-item>
        <el-descriptions-item label="操作人ID">{{ recordDetail.operatorId }}</el-descriptions-item>
      </el-descriptions>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDetailDialog">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getInOutRecordList, addInRecord, addOutRecord, addTransferRecord } from '@/api/inOutRecord'

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
      },
      dialogVisible: false,
      dialogTitle: '',
      recordForm: {
        id: '',
        type: 1, // 默认入库
        goodsId: '',
        warehouseId: '',
        relatedWarehouseId: '',
        quantity: 1,
        contactPerson: '',
        contactPhone: '',
        remark: '',
        operatorId: '' // 需要从用户登录信息中获取
      },
      recordRules: {
        type: [
          { required: true, message: '请选择操作类型', trigger: 'change' }
        ],
        goodsId: [
          { required: true, message: '请输入商品ID', trigger: 'blur' }
        ],
        warehouseId: [
          { required: true, message: '请输入操作仓库ID', trigger: 'blur' }
        ],
        quantity: [
          { required: true, message: '请输入操作数量', trigger: 'blur' },
          { type: 'number', min: 1, message: '数量必须为正整数', trigger: 'blur' }
        ],
        contactPerson: [
          { required: true, message: '请输入对接人姓名', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入对接人联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
        ]
      },
      detailDialogVisible: false,
      recordDetail: {}
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
        console.log('请求参数:', params); // 添加调试信息
        const res = await getInOutRecordList(params)
        console.log('API响应（已处理）:', res); // 添加调试信息
        
        // 由于响应拦截器直接返回了res.data，这里接收到的是分页对象
        // 需要直接使用res作为分页数据
        if (res && res.records !== undefined) {
          this.recordList = res.records || []
          this.pagination.total = res.total || 0
          console.log('提取的数据:', this.recordList); // 添加调试信息
        } else {
          this.$message.error('获取出入库记录失败')
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
    handleAdd() {
      const userInfo = this.$store.state.user.userInfo;
      if (!userInfo || !userInfo.id) {
        this.$message.error('无法获取当前登录用户信息，请重新登录');
        return;
      }
      
      this.dialogTitle = '新增出入库记录'
      this.recordForm = {
        id: '',
        type: 1, // 默认入库
        goodsId: '',
        warehouseId: '',
        relatedWarehouseId: '',
        quantity: 1,
        contactPerson: '',
        contactPhone: '',
        remark: '',
        operatorId: userInfo.id // 从登录用户信息中获取
      }
      this.dialogVisible = true
    },
    handleView(row) {
      this.recordDetail = { ...row }; // 复制记录数据
      this.detailDialogVisible = true; // 显示详情对话框
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchRecordList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.fetchRecordList()
    },
    handleTypeChange(val) {
      // 当类型为调货时，清空目标仓库ID
      if (val !== 3) {
        this.recordForm.relatedWarehouseId = ''
      }
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.$refs.recordForm && this.$refs.recordForm.resetFields()
    },
    handleCloseDetailDialog() {
      this.detailDialogVisible = false;
    },
    handleFormSubmit() {
      this.$refs.recordForm.validate(async (valid) => {
        if (valid) {
          try {
            // 设置操作人ID，从用户登录信息中获取
            const userInfo = this.$store.state.user.userInfo;
            if (!userInfo || !userInfo.id) {
              this.$message.error('无法获取当前登录用户信息，请重新登录');
              return;
            }
            this.recordForm.operatorId = userInfo.id;

            let res
            // 根据类型调用不同的API
            if (this.recordForm.type === 1) {
              // 入库
              console.log('提交入库数据:', this.recordForm); // 添加调试信息
              res = await addInRecord(this.recordForm)
            } else if (this.recordForm.type === 2) {
              // 出库
              console.log('提交出库数据:', this.recordForm); // 添加调试信息
              res = await addOutRecord(this.recordForm)
            } else if (this.recordForm.type === 3) {
              // 调货
              console.log('提交调货数据:', this.recordForm); // 添加调试信息
              res = await addTransferRecord(this.recordForm)
            }
            
            console.log('API响应（已处理）:', res); // 添加调试信息
            
            // 响应拦截器已经处理了成功情况，这里直接是业务数据或成功消息
            this.$message.success('操作成功')
            this.handleCloseDialog()
            // 重新获取列表
            this.fetchRecordList()
          } catch (error) {
            console.error('提交出入库记录失败：', error)
            // 错误已在拦截器中处理，这里主要是捕获异常
            this.$message.error('提交出入库记录失败')
          }
        } else {
          this.$message.error('请填写正确的信息')
          return false
        }
      })
    },
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      
      // 处理ISO 8601格式的日期字符串
      let date = null
      if (typeof dateStr === 'string') {
        // 替换T和Z以兼容更多格式
        date = new Date(dateStr.replace('T', ' ').replace('Z', ''));
      } else {
        date = new Date(dateStr)
      }
      
      if (isNaN(date.getTime())) {
        return dateStr // 如果无法解析日期，则返回原始字符串
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