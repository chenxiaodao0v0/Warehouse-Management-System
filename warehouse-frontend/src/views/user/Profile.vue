<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header" class="profile-header">
        <h3>个人信息</h3>
      </div>
      
      <el-form 
        :model="profileForm" 
        :rules="profileRules" 
        ref="profileForm"
        label-width="100px"
        class="profile-form"
      >
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-tag :type="profileForm.role === 1 ? 'danger' : 'primary'">
            {{ profileForm.role === 1 ? '超级管理员' : '信息管理员' }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'">
            {{ profileForm.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="updateProfile"
            :loading="updateLoading"
          >
            更新信息
          </el-button>
          <el-button 
            @click="openChangePasswordDialog"
          >
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="changePasswordDialogVisible"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules" 
        ref="passwordForm"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入当前密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changePasswordDialogVisible = false">取 消</el-button>
        <el-button 
          type="primary" 
          @click="changePassword"
          :loading="passwordLoading"
        >
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserInfo, updateUserInfo } from '@/api/auth'
import { changePassword } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      profileForm: {
        id: '',
        username: '',
        nickname: '',
        role: null,
        status: null,
        phone: '',
        email: ''
      },
      profileRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      updateLoading: false,
      changePasswordDialogVisible: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      passwordLoading: false
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    // 加载用户信息
    async loadProfile() {
      try {
        const userData = await getUserInfo()
        // 响应拦截器已处理 res.data，所以直接使用返回值
        if (userData && userData.id) {
          this.profileForm = { ...userData }
        } else {
          this.$message.error('获取用户信息失败')
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        this.$message.error('获取用户信息失败')
      }
    },
    
    // 更新用户信息
    async updateProfile() {
      try {
        await this.$refs.profileForm.validate()
        this.updateLoading = true
        
        const result = await updateUserInfo(this.profileForm)
        // 响应拦截器已处理，直接使用返回结果
        if (result === '用户信息更新成功') {
          this.$message.success('更新成功')
          // 更新本地存储的用户信息
          this.$store.commit('user/SET_USER_INFO', this.profileForm)
          // 重新加载最新的用户信息
          this.loadProfile()
        } else {
          this.$message.error('更新失败')
        }
      } catch (error) {
        console.error('更新用户信息失败：', error)
        this.$message.error('更新失败')
      } finally {
        this.updateLoading = false
      }
    },
    
    // 打开修改密码对话框
    openChangePasswordDialog() {
      // 重置表单
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.changePasswordDialogVisible = true
    },
    
    // 修改密码
    async changePassword() {
      try {
        await this.$refs.passwordForm.validate()
        this.passwordLoading = true
        
        const result = await changePassword(this.profileForm.id, this.passwordForm)
        // 响应拦截器已处理，直接使用返回结果
        if (result === '密码修改成功') {
          this.$message.success('密码修改成功')
          // 关闭修改密码对话框
          this.changePasswordDialogVisible = false
        } else {
          this.$message.error('密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败：', error)
        this.$message.error('密码修改失败')
      } finally {
        this.passwordLoading = false
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
}

.profile-form {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>