<template>
  <!-- 根容器 + 路由出口：所有路由匹配的组件会在这里渲染 -->
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 清除默认样式，避免布局异常 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
  width: 100%;
  height: 100%;
}
</style>
