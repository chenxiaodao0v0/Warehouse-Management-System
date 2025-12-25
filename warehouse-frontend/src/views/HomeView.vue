<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="home-header">
      <Navbar />
    </el-header>

    <el-container class="home-content">
      <!-- 侧边栏 -->
      <el-aside class="home-aside">
        <Sidebar />
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="home-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import Navbar from '@/components/Navbar.vue'
import Sidebar from '@/components/Sidebar.vue'

export default {
  name: 'HomeView',
  components: {
    Navbar,
    Sidebar
  }
}
</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.home-header {
  padding: 0;
  background-color: #1989fa;
  color: #fff;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,21,41,.35);
}

.home-content {
  flex: 1;
  overflow: hidden;
}

.home-aside {
  background-color: #fff;
  width: 200px;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  height: 100%;
  overflow-y: auto;
}

.home-main {
  padding: 20px;
  background-color: #f5f7fa;
  height: calc(100vh - 60px);
  overflow-y: auto;
}
</style>