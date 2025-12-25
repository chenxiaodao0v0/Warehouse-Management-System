<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">仓库管理系统登录</h2>
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        class="login-form"
      >
        <el-form-item label="账号" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入登录账号"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入登录密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label-width="0px">
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { userLogin } from '@/api/user' // 导入封装的登录接口

export default {
  name: 'PageLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (!valid) {
          return this.$message.warning('请完善登录信息')
        }
        try {
          // 调用修正后的登录接口（直接传表单数据）
          const res = await userLogin(this.loginForm)
          
          console.log('登录响应:', res) // 添加调试信息
          
          // 检查响应结构，尝试多种可能的结构
          let loginResponse;
          
          // 检查是否是 { data: {...} } 结构
          if (res && res.data) {
            loginResponse = res.data;
          } 
          // 检查是否是直接返回数据结构
          else if (res && res.token) {
            loginResponse = res;
          }
          
          // 如果仍然没有找到有效数据，抛出错误
          if (!loginResponse) {
            throw new Error('登录响应数据格式错误');
          }
          
          // 保存 Token 和用户信息到 Vuex
          this.$store.commit('user/setLoginInfo', {
            token: loginResponse.token,
            userInfo: {
              id: loginResponse.userId,
              nickname: loginResponse.nickname,
              role: loginResponse.role
            }
          })
          this.$message.success('登录成功')
          this.$router.push('/')
        } catch (err) {
          console.error('登录失败：', err)
          this.$message.error('登录失败，请检查账号密码')
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-card {
  width: 400px;
  padding: 30px;
}
.login-title {
  text-align: center;
  color: #1989fa;
  margin-bottom: 20px;
}
.login-form {
  margin-top: 10px;
}
.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}
</style>
