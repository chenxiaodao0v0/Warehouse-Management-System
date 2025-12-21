// vue.config.js - Vue2 项目端口配置
module.exports = {
  devServer: {
    port: 8081, // 前端端口修改为8081（可自定义其他端口，如8082、8090，只要不是8080即可）
    open: true, // 启动项目后自动打开浏览器
    proxy: {
      // 跨域代理配置（可选，解决前后端跨域问题，后续开发必备）
      '/api': {
        target: 'http://localhost:8080', // 后端接口基础路径（后端8080端口）
        changeOrigin: true, // 开启跨域
        pathRewrite: {
          '^/api': '/api' // 路径重写（无需修改，保持默认）
        }
      }
    }
  }
}
