<template>
  <div class="login-container">
    <!-- Element UI 登录表单 -->
    <el-card class="login-card">
      <div class="login-title">仓储管理系统登录</div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入登录账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入登录密码"></el-input>
        </el-form-item>
        <el-form-item label-width="0px" style="text-align: center;">
          <el-button type="primary" @click="handleLogin" size="medium">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
// 导入登录接口
import { userLogin } from '@/api/user'

export default {
  name: 'PageLogin',
  data() {
    return {
      // 登录表单数据
      loginForm: {
        username: '', // 账号
        password: ''  // 密码
      },
      // 表单校验规则
      loginRules: {
        username: [
          { required: true, message: '请输入登录账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 处理登录逻辑
    async handleLogin() {
      // 先校验表单
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          // 表单校验通过，调用登录接口
          try {
            const res = await userLogin(this.loginForm)
            // 登录成功：存储Token和用户信息到Vuex
            this.$store.commit('SET_TOKEN', res.data.token)
            this.$store.commit('SET_USER_INFO', {
              userId: res.data.userId,
              nickname: res.data.nickname,
              role: res.data.role
            })
            // 提示登录成功
            this.$message.success('登录成功！')
            // 跳转首页
            this.$router.push('/index')
          } catch (error) {
            // 登录失败：提示错误信息（后端返回的msg已在request.js中处理，此处无需额外提示）
            console.log('登录失败：', error)
          }
        } else {
          // 表单校验失败
          this.$message.warning('请完善登录信息！')
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 登录页样式，居中显示 */
.login-container {
  width: 100%;
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-card {
  width: 400px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
}
.login-title {
  font-size: 20px;
  text-align: center;
  margin-bottom: 20px;
  font-weight: bold;
  color: #1989fa;
}
</style>