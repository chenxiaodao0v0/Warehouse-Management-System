<template>
  <div class="enterprise-container">
    <el-card class="enterprise-card">
      <div slot="header" class="clearfix">
        <span>企业信息</span>
      </div>
      
      <div class="enterprise-info">
        <el-descriptions title="企业基本信息" :column="1" border>
          <el-descriptions-item label="企业名称">加载中...</el-descriptions-item>
          <el-descriptions-item label="企业地址">加载中...</el-descriptions-item>
          <el-descriptions-item label="联系人">加载中...</el-descriptions-item>
          <el-descriptions-item label="联系电话">加载中...</el-descriptions-item>
          <el-descriptions-item label="企业简介">加载中...</el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="handleEdit">编辑企业信息</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EnterpriseInfo',
  data() {
    return {
      enterpriseInfo: {}
    }
  },
  created() {
    this.fetchEnterpriseInfo()
  },
  methods: {
    async fetchEnterpriseInfo() {
      try {
        // 这里应该调用获取企业信息的API
        this.$message.info('企业信息获取功能待实现')
      } catch (error) {
        console.error('获取企业信息失败：', error)
        this.$message.error('获取企业信息失败')
      }
    },
    handleEdit() {
      this.$message.info('编辑企业信息功能待实现')
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