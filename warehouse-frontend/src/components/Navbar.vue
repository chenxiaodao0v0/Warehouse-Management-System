<template>
  <div class="navbar-container">
    <div class="navbar-brand">
      <h3>仓库管理系统</h3>
    </div>
    <div class="navbar-user">
      <span class="user-info">欢迎，{{ userInfo.nickname || userInfo.username || userInfo.name || '用户' }}</span>
      <el-button type="text" @click="handleLogout" class="logout-btn">退出</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Navbar',
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo || {}
    }
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

.logout-btn {
  color: #fff;
}
</style>