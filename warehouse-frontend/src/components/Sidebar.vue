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
      <el-menu-item index="/dashboard">
        <i class="el-icon-s-data"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      
      <!-- 商品管理 -->
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-goods"></i>
          <span>商品管理</span>
        </template>
        <el-menu-item index="/goods/list">
          <i class="el-icon-menu"></i>
          <span slot="title">商品信息</span>
        </el-menu-item>
      </el-submenu>
      
      <!-- 仓库管理 -->
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-office-building"></i>
          <span>仓库管理</span>
        </template>
        <el-menu-item index="/warehouse/add">
          <i class="el-icon-menu"></i>
          <span slot="title">仓库信息</span>
        </el-menu-item>
      </el-submenu>
      
      <!-- 出入库调度 -->
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-tickets"></i>
          <span>出入库调度</span>
        </template>
        <el-menu-item index="/inoutrecord">
          <i class="el-icon-menu"></i>
          <span slot="title">出入库记录</span>
        </el-menu-item>
      </el-submenu>
      
      <!-- 企业 -->
      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-s-home"></i>
          <span>企业</span>
        </template>
        <el-menu-item index="/enterprise/info">
          <i class="el-icon-menu"></i>
          <span slot="title">企业信息</span>
        </el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'Sidebar',
  computed: {
    // 计算当前活跃的菜单项，基于当前路由
    activeMenu() {
      const { meta, path } = this.$route
      // 如果路由有指定的activeMenu，则使用它，否则使用路由路径
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    }
  }
}
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  overflow-y: auto;
}
.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style>