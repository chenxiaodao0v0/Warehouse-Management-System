<template>
  <div class="enterprise-container">
    <el-card class="enterprise-card">
      <div slot="header" class="clearfix">
        <span>企业信息</span>
      </div>
      
      <div class="enterprise-info">
        <el-descriptions title="企业基本信息" :column="1" border>
          <el-descriptions-item label="企业名称">{{ enterpriseInfo.name || '加载中...' }}</el-descriptions-item>
          <el-descriptions-item label="企业地址">{{ enterpriseInfo.address || '加载中...' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ enterpriseInfo.contact || '加载中...' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ enterpriseInfo.phone || '加载中...' }}</el-descriptions-item>
          <el-descriptions-item label="企业简介">{{ enterpriseInfo.remark || '加载中...' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(enterpriseInfo.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="handleEdit">编辑企业信息</el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 编辑企业信息对话框 -->
    <el-dialog
      title="编辑企业信息"
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
        <el-button type="primary" @click="handleEditSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEnterpriseInfo, updateEnterprise } from '@/api/enterprise'

export default {
  name: 'EnterpriseInfo',
  data() {
    return {
      enterpriseInfo: {},
      dialogVisible: false,
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
        console.log('企业信息响应:', res) // 添加调试信息
        
        // 检查响应结构是否为标准格式
        if (res && res.code === 200 && res.data) {
          // 标准响应格式：{code: 200, data: {企业信息}}
          this.enterpriseInfo = res.data
        } else if (res && res.id) {
          // 直接返回企业对象格式：{id: ..., name: ..., ...}
          this.enterpriseInfo = res
        } else if (res && res.code !== 200) {
          // 响应code不是200，说明后端返回了错误信息
          this.$message.error('获取企业信息失败：' + (res.msg || '未知错误'))
        } else {
          this.$message.error('获取企业信息失败：响应格式错误')
        }
      } catch (error) {
        console.error('获取企业信息失败：', error)
        this.$message.error('获取企业信息失败')
      }
    },
    handleEdit() {
      // 将当前企业信息填充到编辑表单
      this.editForm = { ...this.enterpriseInfo }
      this.dialogVisible = true
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.$refs.editForm && this.$refs.editForm.resetFields()
    },
    async handleEditSubmit() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          try {
            await updateEnterprise(this.editForm)
            this.$message.success('修改企业信息成功')
            this.dialogVisible = false
            // 刷新企业信息
            this.fetchEnterpriseInfo()
          } catch (error) {
            console.error('修改企业信息失败：', error)
            this.$message.error('修改企业信息失败')
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
</style>