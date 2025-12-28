<template>
  <div class="navbar-container">
    <div class="navbar-brand">
      <h3>仓库管理系统</h3>
    </div>
    <div class="navbar-user">
      <span class="user-info">欢迎，{{ userInfo.nickname || userInfo.username }}</span>
      <el-button type="text" @click="handleChangePassword" class="password-btn">修改密码</el-button>
      <el-button type="text" @click="handleLogout" class="logout-btn">退出</el-button>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="passwordDialogVisible"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmNewPassword">
          <el-input v-model="passwordForm.confirmNewPassword" type="password" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitChangePassword">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { changePassword } from '@/api/user'

export default {
  name: 'Navbar',
  data() {
    // 自定义确认密码验证规则
    const validateConfirmNewPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      userInfo: {},
      passwordDialogVisible: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmNewPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmNewPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // 从store中获取用户信息
    this.userInfo = this.$store.state.user.userInfo
  },
  methods: {
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除token和用户信息
        this.$store.commit('user/clearToken')
        // 跳转到登录页
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }).catch(() => {
        // 取消操作
      })
    },
    
    handleChangePassword() {
      // 重置表单
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      }
      this.passwordDialogVisible = true
    },
    
    async submitChangePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (!valid) {
          return this.$message.warning('请完善密码信息')
        }

        try {
          // 获取当前用户ID
          const userId = this.$store.state.user.userInfo.id
          
          await changePassword(userId, {
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          })
          
          this.$message.success('密码修改成功')
          this.passwordDialogVisible = false
        } catch (error) {
          console.error('修改密码失败：', error)
          this.$message.error('密码修改失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.navbar-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.navbar-brand h3 {
  color: #fff;
  margin: 0;
}

.navbar-user {
  display: flex;
  align-items: center;
}

.user-info {
  color: #fff;
  margin-right: 20px;
}

.password-btn, .logout-btn {
  color: #fff;
}
</style>