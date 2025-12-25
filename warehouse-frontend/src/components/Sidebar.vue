<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :unique-opened="true"
      :router="true"
      background-color="#fff"
      text-color="#333"
      active-text-color="#1989fa"
    >
      <el-menu-item index="/goods/list">
        <i class="el-icon-goods"></i>
        <span slot="title">商品管理</span>
      </el-menu-item>
      
      <el-menu-item index="/warehouse/add">
        <i class="el-icon-office-building"></i>
        <span slot="title">仓库管理</span>
      </el-menu-item>
      
      <el-menu-item index="/inout/record">
        <i class="el-icon-tickets"></i>
        <span slot="title">出入库记录</span>
      </el-menu-item>
      
      <el-menu-item index="/enterprise/info">
        <i class="el-icon-s-home"></i>
        <span slot="title">企业信息</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
  computed: {
    activeMenu() {
      const { path } = this.$route
      return path
    }
  }
}
</script>

<style scoped>
.sidebar-container {
  height: 100%;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>