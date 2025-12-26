<template>
  <div class="enterprise-container">
    <el-card class="enterprise-card">
      <div slot="header" class="clearfix">
        <span>企业信息</span>
      </div>
      
      <div class="enterprise-info" v-if="enterpriseInfo && Object.keys(enterpriseInfo).length > 0">
        <el-descriptions title="企业基本信息" :column="1" border>
          <el-descriptions-item label="企业名称">{{ enterpriseInfo.name || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="企业地址">{{ enterpriseInfo.address || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ enterpriseInfo.contact || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ enterpriseInfo.phone || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="企业简介">{{ enterpriseInfo.remark || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(enterpriseInfo.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="handleEdit">编辑企业信息</el-button>
        </div>
      </div>
      <div v-else class="no-enterprise-info">
        <el-alert
          title="提示：尚未添加企业信息，请先添加企业信息"
          type="warning"
          :closable="false"
          show-icon
        >
        </el-alert>
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="handleAdd">添加企业信息</el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 编辑/添加企业信息对话框 -->
    <el-dialog
      :title="isAddMode ? '添加企业信息' : '编辑企业信息'"
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="企业名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入企业名称"></el-input>
        </el-form-item>
        <el-form-item label="企业地址" prop="address">
          <el-input v-model="editForm.address" placeholder="请输入企业地址"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="editForm.contact" placeholder="请输入联系人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="企业简介" prop="remark">
          <el-input 
            type="textarea" 
            v-model="editForm.remark" 
            placeholder="请输入企业简介"
            :rows="4"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDialog">取 消</el-button>
        <el-button type="primary" @click="handleFormSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEnterpriseInfo, updateEnterprise, initEnterprise } from '@/api/enterprise'

export default {
  name: 'EnterpriseInfo',
  data() {
    return {
      enterpriseInfo: {},
      dialogVisible: false,
      isAddMode: false, // 是否为添加模式
      editForm: {
        id: '',
        name: '',
        address: '',
        contact: '',
        phone: '',
        remark: ''
      },
      editRules: {
        name: [
          { required: true, message: '请输入企业名称', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入企业地址', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入联系人姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请输入企业简介', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchEnterpriseInfo()
  },
  methods: {
    async fetchEnterpriseInfo() {
      try {
        const res = await getEnterpriseInfo()
        console.log('企业信息API响应:', res) // 添加调试日志
        // 根据响应的实际结构处理
        // 情况1: 标准响应格式 {code, msg, data}
        if (res && res.code) {
          if (res.code === 200) {
            if (res.data) {
              console.log('获取到的企业信息:', res.data) // 添加调试日志
              this.enterpriseInfo = res.data
            } else {
              // 如果没有企业信息，清空enterpriseInfo
              console.log('没有获取到企业信息') // 添加调试日志
              this.enterpriseInfo = {}
            }
          } else {
            this.$message.error(res.msg || '获取企业信息失败')
          }
        } 
        // 情况2: 直接返回企业信息对象
        else if (res && res.name && res.address) {
          console.log('直接获取到的企业信息:', res) // 添加调试日志
          this.enterpriseInfo = res
        }
        else {
          this.$message.error('获取企业信息失败')
        }
      } catch (error) {
        console.error('获取企业信息失败：', error)
        this.$message.error('获取企业信息失败')
      }
    },
    handleEdit() {
      if (!this.enterpriseInfo || Object.keys(this.enterpriseInfo).length === 0) {
        // 如果没有企业信息，进入添加模式
        this.isAddMode = true
        // 清空表单
        this.editForm = {
          id: '',
          name: '',
          address: '',
          contact: '',
          phone: '',
          remark: ''
        }
      } else {
        // 编辑模式
        this.isAddMode = false
        // 将当前企业信息填充到编辑表单
        this.editForm = { ...this.enterpriseInfo }
      }
      this.dialogVisible = true
    },
    handleAdd() {
      // 添加模式
      this.isAddMode = true
      // 清空表单
      this.editForm = {
        id: '',
        name: '',
        address: '',
        contact: '',
        phone: '',
        remark: ''
      }
      this.dialogVisible = true
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.isAddMode = false
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    async handleFormSubmit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          try {
            // 如果是添加模式，需要调用初始化接口
            if (this.isAddMode) {
              await initEnterprise(this.editForm)
            } else {
              await updateEnterprise(this.editForm)
            }
            this.$message.success(this.isAddMode ? '添加企业信息成功' : '修改企业信息成功')
            this.dialogVisible = false
            // 刷新企业信息
            this.fetchEnterpriseInfo()
          } catch (error) {
            console.error(this.isAddMode ? '添加企业信息失败：' : '修改企业信息失败：', error)
            this.$message.error(this.isAddMode ? '添加企业信息失败' : '修改企业信息失败')
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
.enterprise-container {
  width: 100%;
  height: 100%;
}

.enterprise-card {
  height: 100%;
}

.enterprise-info {
  padding: 20px 0;
}

.no-enterprise-info {
  padding: 40px;
  text-align: center;
}
</style>