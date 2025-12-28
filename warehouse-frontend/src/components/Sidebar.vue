<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      mode="vertical"
      router
      :collapse="false"
    >
      <!-- 首页 -->
      <el-menu-item index="/dashboard" v-if="hasMenuPermission('dashboard') || !hasAnyMenuPermission()">
        <i class="el-icon-house"></i>
        <span>首页</span>
      </el-menu-item>

      <!-- 仓库管理 -->
      <el-submenu index="warehouse" v-if="hasMenuPermission('warehouse_management')">
        <template slot="title">
          <i class="el-icon-box"></i>
          <span>仓库管理</span>
        </template>
        <el-menu-item index="/warehouse/list" v-if="hasMenuPermission('warehouse_list')">
          <span slot="title">仓库列表</span>
        </el-menu-item>
        <el-menu-item index="/warehouse/inventory" v-if="hasMenuPermission('inventory_overview')">
          <span slot="title">库存总览</span>
        </el-menu-item>
      </el-submenu>

      <!-- 商品管理 -->
      <el-submenu index="goods" v-if="hasMenuPermission('goods_management')">
        <template slot="title">
          <i class="el-icon-goods"></i>
          <span>商品管理</span>
        </template>
        <el-menu-item index="/goods/list" v-if="hasMenuPermission('goods_list')">
          <span slot="title">商品列表</span>
        </el-menu-item>
        <el-menu-item index="/goods/category" v-if="hasMenuPermission('goods_category')">
          <span slot="title">商品分类</span>
        </el-menu-item>
      </el-submenu>

      <!-- 出入库管理 -->
      <el-submenu index="inOut" v-if="hasMenuPermission('in_out_management')">
        <template slot="title">
          <i class="el-icon-s-operation"></i>
          <span>出入库管理</span>
        </template>
        <el-menu-item index="/inOut/record" v-if="hasMenuPermission('in_out_record')">
          <span slot="title">出入库记录</span>
        </el-menu-item>
      </el-submenu>

      <!-- 统计分析 -->
      <el-submenu index="statistics" v-if="hasMenuPermission('statistics_management')">
        <template slot="title">
          <i class="el-icon-data-analysis"></i>
          <span>统计分析</span>
        </template>
        <el-menu-item index="/statistics/overview" v-if="hasMenuPermission('statistics_overview')">
          <span slot="title">统计总览</span>
        </el-menu-item>
      </el-submenu>

      <!-- 企业管理 -->
      <el-menu-item index="/enterprise/info" v-if="hasMenuPermission('enterprise_info')">
        <i class="el-icon-office-building"></i>
        <span>企业管理</span>
      </el-menu-item>

      <!-- 用户管理 -->
      <el-submenu index="user" v-if="hasMenuPermission('user_management')">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </template>
        <el-menu-item index="/user/list" v-if="hasMenuPermission('user_list')">
          <span slot="title">用户列表</span>
        </el-menu-item>
        <el-menu-item index="/user/permission" v-if="hasMenuPermission('permission_management') && hasSuperAdminRole">
          <span slot="title">权限管理</span>
        </el-menu-item>
      </el-submenu>
      
      <!-- 个人中心 -->
      <el-submenu index="profile" v-if="hasMenuPermission('profile_management')">
        <template slot="title">
          <i class="el-icon-setting"></i>
          <span>个人中心</span>
        </template>
        <el-menu-item index="/user/profile" v-if="hasMenuPermission('user_profile')">
          <span slot="title">个人信息</span>
        </el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
import { getUserMenuPermissions } from '@/api/permission'

export default {
  name: 'Sidebar',
  data() {
    return {
      userMenuPermissions: []
    }
  },
  async created() {
    await this.loadUserMenuPermissions()
  },
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
  },
  methods: {
    // 检查用户是否有指定菜单权限
    hasMenuPermission(menuCode) {
      // 超级管理员拥有所有权限
      if (this.hasSuperAdminRole) {
        return true
      }
      
      // 检查用户是否拥有指定菜单权限
      return this.userMenuPermissions.includes(menuCode)
    },
    // 检查是否有任何菜单权限（用于确定是否显示首页）
    hasAnyMenuPermission() {
      return this.userMenuPermissions && this.userMenuPermissions.length > 0
    },
    // 加载用户菜单权限
    async loadUserMenuPermissions() {
      try {
        const userInfo = this.$store.state.user.userInfo
        if (userInfo) {
          // 仅当用户存在时尝试获取权限
          const response = await getUserMenuPermissions(userInfo.id)
          // 成功获取权限时更新本地存储
          this.userMenuPermissions = response || []
        }
      } catch (error) {
        console.error('加载用户菜单权限失败:', error)
        // 失败时设置为空数组，用户将只有默认权限（如首页等）
        this.userMenuPermissions = []
      }
    }
  },
  watch: {
    // 监听路由变化，重新检查权限
    '$route': {
      handler() {
        // 路由变化时可以执行一些权限检查逻辑
      },
      immediate: true
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