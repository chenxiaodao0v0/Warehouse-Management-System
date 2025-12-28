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
        <i class="el-icon-house"></i>
        <span slot="title">首页</span>
      </el-menu-item>

      <!-- 商品管理 -->
      <el-submenu index="goods">
        <template slot="title">
          <i class="el-icon-shopping-cart-full"></i>
          <span>商品管理</span>
        </template>
        <el-menu-item index="/goods/list">
          <span slot="title">商品列表</span>
        </el-menu-item>
      </el-submenu>

      <!-- 仓库管理 -->
      <el-submenu index="warehouse">
        <template slot="title">
          <i class="el-icon-warehouse"></i>
          <span>仓库管理</span>
        </template>
        <el-menu-item index="/warehouse/add">
          <span slot="title">添加仓库</span>
        </el-menu-item>
        <el-menu-item index="/warehouse/inventory">
          <span slot="title">库存信息</span>
        </el-menu-item>
      </el-submenu>

      <!-- 企业管理 -->
      <el-submenu index="enterprise">
        <template slot="title">
          <i class="el-icon-office-building"></i>
          <span>企业管理</span>
        </template>
        <el-menu-item index="/enterprise/info">
          <span slot="title">企业信息</span>
        </el-menu-item>
      </el-submenu>

      <!-- 出入库管理 -->
      <el-submenu index="inout">
        <template slot="title">
          <i class="el-icon-data-line"></i>
          <span>出入库管理</span>
        </template>
        <el-menu-item index="/inoutrecord">
          <span slot="title">出入库记录</span>
        </el-menu-item>
      </el-submenu>

      <!-- 用户管理 - 只有超级管理员才能看到 -->
      <el-submenu v-if="hasSuperAdminRole" index="user">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/user/list">
          <span slot="title">用户列表</span>
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
    },
    // 检查用户是否有超级管理员权限
    hasSuperAdminRole() {
      const userInfo = this.$store.state.user.userInfo
      return userInfo && userInfo.role === 1 // 1 是超级管理员
    }
  }
}
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  padding: 10px 0;
}

.sidebar-menu {
  border: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style>