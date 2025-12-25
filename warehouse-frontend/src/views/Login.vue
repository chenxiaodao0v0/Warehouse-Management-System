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
          // 保存 Token 和用户信息到 Vuex
          this.$store.commit('user/setLoginInfo', {
            token: res.token,
            userInfo: res.userInfo
          })
          this.$message.success('登录成功')
          this.$router.push('/')
        } catch (err) {
          console.error('登录失败：', err)
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
