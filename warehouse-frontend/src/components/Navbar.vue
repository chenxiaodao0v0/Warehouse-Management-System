<template>
  <div class="navbar-container">
    <div class="navbar-brand">
      <h3>仓库管理系统</h3>
    </div>
    <div class="navbar-user">
      <span class="user-info">欢迎，{{ userInfo.nickname || userInfo.username }}</span>
      <el-dropdown @command="handleCommand" class="user-dropdown">
        <span class="el-dropdown-link">
          <i class="el-icon-setting"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="profile">
            <i class="el-icon-user"></i>
            个人信息
          </el-dropdown-item>
          <el-dropdown-item command="logout">
            <i class="el-icon-switch-button"></i>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Navbar',
  created() {
    // 从store中获取用户信息
    this.userInfo = this.$store.state.user.userInfo
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.goToProfile()
      } else if (command === 'logout') {
        this.handleLogout()
      }
    },
    
    goToProfile() {
      this.$router.push('/dashboard')
      this.$nextTick(() => {
        this.$router.push('/user/profile')
      })
    },
    
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

.password-btn, .logout-btn, .el-dropdown-link {
  color: #fff;
  cursor: pointer;
}

.el-dropdown-link i {
  font-size: 18px;
}
</style>