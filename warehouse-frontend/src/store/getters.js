const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  name: state => state.user.userInfo?.nickname || state.user.userInfo?.username,
  avatar: state => state.user.userInfo?.avatar,
  introduction: state => state.user.userInfo?.introduction,
  roles: state => state.user.userInfo?.roles,
  permissions: state => state.user.userInfo?.permissions || state.user.permissions,
  userPermissions: state => state.user.permissions,
  permission_routes: state => state.permission.routes,
  topbarRouters: state => state.permission.topbarRouters,
  defaultRoutes: state => state.permission.defaultRoutes,
  sidebarRouters: state => state.permission.sidebarRouters
}
export default getters