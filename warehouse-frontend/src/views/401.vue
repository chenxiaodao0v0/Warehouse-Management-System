<template>
  <div class="unauthorized-container">
    <div class="unauthorized-content">
      <div class="error-icon">
        <i class="el-icon-lock"></i>
      </div>
      <h1>无权限访问</h1>
      <p class="error-desc">抱歉，您没有权限访问此页面</p>
      <div class="action-buttons">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Unauthorized',
  methods: {
    goHome() {
      this.$router.push({ path: '/' })
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.unauthorized-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.unauthorized-content {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 60px;
  color: #f56c6c;
  margin-bottom: 20px;
}

.error-icon i {
  font-size: 60px;
}

.unauthorized-content h1 {
  color: #303133;
  font-size: 24px;
  margin: 0 0 10px;
}

.error-desc {
  color: #909399;
  font-size: 16px;
  margin: 0 0 30px;
}

.action-buttons .el-button {
  margin: 0 10px;
}
</style>