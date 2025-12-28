<template>
  <div class="permission-management">
    <div class="header">
      <h2>权限管理</h2>
    </div>

    <el-card class="permission-card">
      <div slot="header" class="card-header">
        <span>用户权限分配</span>
      </div>

      <!-- 用户选择 -->
      <div class="user-selection">
        <el-select 
          v-model="selectedUserId" 
          placeholder="请选择用户"
          @change="loadUserPermissions"
          style="width: 300px;"
        >
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname || user.username || user.name"
            :value="user.id"
          >
          </el-option>
        </el-select>
        <div v-if="!userList || userList.length === 0" class="no-data-message">
          暂无用户数据
        </div>
      </div>

      <!-- 权限分配 -->
      <div class="permission-assignment" v-if="selectedUserId">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="菜单权限" name="menu">
            <div class="menu-permissions">
              <el-tree
                ref="menuTree"
                :data="menuOptions"
                show-checkbox
                node-key="code"
                :default-expand-all="true"
                :props="menuTreeProps"
              >
              </el-tree>
            </div>
          </el-tab-pane>
          <el-tab-pane label="仓库权限" name="warehouse">
            <div class="warehouse-permissions">
              <el-checkbox-group v-model="selectedWarehouses">
                <el-checkbox
                  v-for="warehouse in warehouseList"
                  :key="warehouse.id"
                  :label="warehouse.id"
                >
                  {{ warehouse.name || warehouse.warehouseName || warehouse.id }}
                </el-checkbox>
              </el-checkbox-group>
              <div v-if="!warehouseList || warehouseList.length === 0" class="no-data-message">
                暂无仓库数据
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>

        <div class="action-buttons">
          <el-button @click="resetPermissions">重置</el-button>
          <el-button type="primary" @click="savePermissions">保存权限</el-button>
        </div>
      </div>
      <div v-else class="select-prompt">
        请先选择一个用户以查看和分配权限
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserList } from '@/api/user'
import { getWarehouseList } from '@/api/warehouse'
import { getAllPermissions, setAllPermissions } from '@/api/permission'

export default {
  name: 'PermissionManagement',
  data() {
    return {
      activeTab: 'menu',
      selectedUserId: '',
      userList: [],
      warehouseList: [],
      selectedWarehouses: [],
      menuOptions: [
        {
          code: 'user_management',
          label: '用户管理',
          children: [
            {
              code: 'user_list',
              label: '用户列表'
            },
            {
              code: 'permission_management',
              label: '权限管理'
            }
          ]
        },
        {
          code: 'warehouse_management',
          label: '仓库管理',
          children: [
            {
              code: 'warehouse_list',
              label: '仓库列表'
            },
            {
              code: 'inventory_overview',
              label: '库存总览'
            }
          ]
        },
        {
          code: 'goods_management',
          label: '商品管理',
          children: [
            {
              code: 'goods_list',
              label: '商品列表'
            },
            {
              code: 'goods_category',
              label: '商品分类'
            }
          ]
        },
        {
          code: 'in_out_management',
          label: '出入库管理',
          children: [
            {
              code: 'in_out_record',
              label: '出入库记录'
            }
          ]
        },
        {
          code: 'statistics_management',
          label: '统计分析',
          children: [
            {
              code: 'statistics_overview',
              label: '统计总览'
            }
          ]
        },
        {
          code: 'enterprise_management',
          label: '企业管理',
          children: [
            {
              code: 'enterprise_info',
              label: '企业信息'
            }
          ]
        }
      ],
      menuTreeProps: {
        children: 'children',
        label: 'label'
      },
      currentMenuPermissions: []
    }
  },
  async mounted() {
    console.log('PermissionManagement mounted')
    await this.loadUserList()
    await this.loadWarehouseList()
    console.log('UserList loaded:', this.userList)
    console.log('WarehouseList loaded:', this.warehouseList)
  },
  methods: {
    async loadUserList() {
      try {
        const response = await getUserList({ pageNum: 1, pageSize: 1000 })
        console.log('用户列表API响应:', response) // 添加调试信息
        // 拦截器已处理响应格式，直接使用返回的数据
        this.userList = response?.records || []
        console.log('设置后的userList:', this.userList) // 添加调试信息
      } catch (error) {
        console.error('获取用户列表错误:', error)
        this.$message.error('获取用户列表失败')
      }
    },
    async loadWarehouseList() {
      try {
        const response = await getWarehouseList({ pageNum: 1, pageSize: 1000 })
        console.log('仓库列表API响应:', response) // 添加调试信息
        // 拦截器已处理响应格式，直接使用返回的数据
        this.warehouseList = response?.records || []
        console.log('设置后的warehouseList:', this.warehouseList) // 添加调试信息
      } catch (error) {
        console.error('获取仓库列表错误:', error)
        this.$message.error('获取仓库列表失败')
      }
    },
    async loadUserPermissions(userId) {
      if (!userId) return
      
      try {
        const response = await getAllPermissions(userId)
        console.log('用户权限API响应:', response) // 添加调试信息
        const permissions = response // 拦截器已处理响应格式
        
        // 设置菜单权限
        this.currentMenuPermissions = permissions.menuPermissions || []
        this.$nextTick(() => {
          this.$refs.menuTree.setCheckedKeys(this.currentMenuPermissions)
        })
        
        // 设置仓库权限
        this.selectedWarehouses = permissions.warehousePermissions || []
      } catch (error) {
        console.error('获取用户权限错误:', error)
        // 检查是否为权限错误
        if (error?.response?.status === 403) {
          this.$message.error('权限不足，只有超级管理员可以查看和修改用户权限')
        } else {
          this.$message.error('获取用户权限失败')
        }
      }
    },
    async savePermissions() {
      if (!this.selectedUserId) {
        this.$message.warning('请先选择一个用户')
        return
      }

      // 获取选中的菜单权限
      const checkedMenuKeys = this.$refs.menuTree.getCheckedKeys()
      const halfCheckedMenuKeys = this.$refs.menuTree.getHalfCheckedKeys()
      const allMenuPermissions = [...checkedMenuKeys, ...halfCheckedMenuKeys]

      try {
        console.log('准备保存权限:', {
          userId: this.selectedUserId,
          menuCodes: allMenuPermissions,
          warehouseIds: this.selectedWarehouses
        });
        
        const response = await setAllPermissions({
          userId: this.selectedUserId,
          menuCodes: allMenuPermissions,
          warehouseIds: this.selectedWarehouses
        })
        
        console.log('权限保存响应:', response);
        
        // 拦截器已处理响应格式，直接检查是否成功
        this.$message.success('权限保存成功')
      } catch (error) {
        console.error('权限保存错误:', error)
        // 检查错误类型并显示详细信息
        if (error?.response?.status === 403) {
          this.$message.error('权限不足，只有超级管理员可以修改用户权限')
        } else if (error?.response?.data?.msg) {
          this.$message.error(`权限保存失败: ${error.response.data.msg}`)
        } else {
          this.$message.error(`权限保存失败: ${error.message || '未知错误'}`)
        }
      }
    },
    resetPermissions() {
      if (!this.selectedUserId) return
      
      // 重置菜单权限
      this.$refs.menuTree.setCheckedKeys(this.currentMenuPermissions)
      
      // 重置仓库权限 - 修复bug：之前错误地使用了菜单权限数据
      this.selectedWarehouses = [...this.selectedWarehouses]
    }
  }
}
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.permission-card {
  min-height: 400px;
}

.user-selection {
  margin-bottom: 20px;
}

.permission-assignment {
  margin-top: 20px;
}

.menu-permissions {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;
}

.warehouse-permissions {
  padding: 10px 0;
}

.warehouse-permissions .el-checkbox {
  display: block;
  margin-bottom: 10px;
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
}

.card-header {
  font-weight: bold;
}

.no-data-message {
  color: #999;
  font-style: italic;
  padding: 10px 0;
}

.select-prompt {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
</style>