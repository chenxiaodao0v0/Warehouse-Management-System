<template>
  <div class="user-list-container">
    <el-card class="user-list-card">
      <div slot="header" class="user-list-header">
        <h3>用户管理</h3>
      </div>

      <!-- 搜索和操作区域 -->
      <div class="user-list-operation">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="关键词">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="用户名/昵称" 
              clearable
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="handleSearch"
              :disabled="!hasSuperAdminRole"
            >
              搜索
            </el-button>
            <el-button 
              type="primary" 
              @click="handleAdd"
              :disabled="!hasSuperAdminRole"
            >
              添加用户
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表 -->
      <el-table 
        :data="userList" 
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="150"></el-table-column>
        <el-table-column label="角色" width="120">
          <template slot-scope="scope">
            {{ scope.row.role === 1 ? '超级管理员' : '信息管理员' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-dropdown @command="handleCommand" size="mini" :disabled="!hasSuperAdminRole">
              <el-button size="mini" type="primary">
                操作 <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item 
                  :command="{action: 'edit', row: scope.row}" 
                  :disabled="!hasSuperAdminRole"
                  icon="el-icon-edit"
                >
                  编辑
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'resetPassword', row: scope.row}" 
                  :disabled="!hasSuperAdminRole"
                  icon="el-icon-key"
                >
                  重置密码
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'toggleStatus', row: scope.row}" 
                  :disabled="!hasSuperAdminRole"
                  :icon="scope.row.status === 1 ? 'el-icon-video-pause' : 'el-icon-video-play'"
                >
                  {{ scope.row.status === 1 ? '禁用' : '启用' }}
                </el-dropdown-item>
                <el-dropdown-item 
                  :command="{action: 'delete', row: scope.row}" 
                  :disabled="!hasSuperAdminRole"
                  divided
                  icon="el-icon-delete"
                  class="danger-item"
                >
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      >
      </el-pagination>
    </el-card>

    <!-- 用户编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="userForm" 
        :rules="userRules" 
        ref="userForm"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="userForm.username" 
            :disabled="dialogType === 'edit'"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="1"></el-option>
            <el-option label="信息管理员" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleDialogSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, addUser, updateUser, deleteUser, resetPassword, toggleUserStatus } from '@/api/user'

export default {
  name: 'UserList',
  data() {
    return {
      loading: false,
      userList: [],
      searchForm: {
        keyword: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      userForm: {
        id: '',
        username: '',
        nickname: '',
        role: 2, // 默认信息管理员
        status: 1, // 默认启用
        phone: '',
        email: ''
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    // 检查用户是否有超级管理员权限
    hasSuperAdminRole() {
      const userInfo = this.$store.state.user.userInfo
      return userInfo && userInfo.role === 1 // 1 是超级管理员
    }
  },
  created() {
    this.fetchUserList()
  },
  methods: {
    // 获取用户列表
    async fetchUserList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          keyword: this.searchForm.keyword
        }
        const res = await getUserList(params)
        console.log('用户列表API响应:', res) // 添加调试信息
        this.userList = res.records || []
        console.log('处理后的用户列表:', this.userList) // 添加调试信息
        this.pagination.total = res.total || 0
      } catch (error) {
        console.error('获取用户列表失败：', error)
        this.$message.error('获取用户列表失败')
      } finally {
        this.loading = false
      }
    },

    // 搜索用户
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchUserList()
    },

    // 添加用户
    handleAdd() {
      this.dialogType = 'add'
      this.userForm = {
        id: '',
        username: '',
        nickname: '',
        role: 2, // 默认信息管理员
        status: 1, // 默认启用
        phone: '',
        email: ''
      }
      this.dialogVisible = true
    },

    // 编辑用户
    handleEdit(row) {
      this.dialogType = 'edit'
      // 深拷贝，避免修改表单时影响原数据
      this.userForm = { ...row }
      this.dialogVisible = true
    },

    // 删除用户
    async handleDelete(row) {
      try {
        await this.$confirm(
          `确定要删除用户 "${row.username}" 吗？此操作不可撤销！`, 
          '警告', 
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
            confirmButtonClass: 'el-button--danger' // 添加危险按钮样式
          }
        )
        
        await deleteUser(row.id)
        this.$message.success('用户删除成功')
        this.fetchUserList() // 重新获取列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败：', error)
          this.$message.error('删除失败：' + (error.message || error))
        } else {
          this.$message.info('已取消删除')
        }
      }
    },

    // 重置密码
    async handleResetPassword(row) {
      try {
        await this.$confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await resetPassword(row.id)
        this.$message.success('密码重置成功，默认密码为123456')
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重置密码失败：', error)
          this.$message.error('重置密码失败')
        }
      }
    },

    // 切换用户状态
    async handleToggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1;
      const statusText = newStatus === 1 ? '启用' : '禁用';
      
      try {
        await this.$confirm(
          `确定要${statusText}用户 "${row.username}" 吗？`, 
          '提示', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: newStatus === 1 ? 'success' : 'warning'
          }
        )
        
        await toggleUserStatus(row.id, newStatus);
        this.$message.success(`用户${statusText}成功`);
        this.fetchUserList(); // 重新获取列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error(`用户${statusText}失败：`, error);
          this.$message.error(`${statusText}失败：` + (error.message || error));
        } else {
          this.$message.info('已取消操作');
        }
      }
    },

    // 分页大小变化
    handleSizeChange(pageSize) {
      this.pagination.pageSize = pageSize
      this.pagination.pageNum = 1
      this.fetchUserList()
    },

    // 当前页变化
    handleCurrentChange(pageNum) {
      this.pagination.pageNum = pageNum
      this.fetchUserList()
    },

    // 对话框提交
    handleDialogSubmit() {
      this.$refs.userForm.validate(async (valid) => {
        if (!valid) {
          return this.$message.warning('请完善表单信息')
        }

        try {
          if (this.dialogType === 'add') {
            await addUser(this.userForm)
            this.$message.success('添加用户成功')
          } else {
            await updateUser(this.userForm)
            this.$message.success('更新用户成功')
          }
          
          this.dialogVisible = false
          this.fetchUserList() // 重新获取列表
        } catch (error) {
          console.error(`${this.dialogType === 'add' ? '添加' : '更新'}用户失败：`, error)
          this.$message.error(`${this.dialogType === 'add' ? '添加' : '更新'}用户失败`)
        }
      })
    },

    // 处理下拉菜单命令
    handleCommand(command) {
      const { action, row } = command;
      switch(action) {
        case 'edit':
          this.handleEdit(row);
          break;
        case 'resetPassword':
          this.handleResetPassword(row);
          break;
        case 'toggleStatus':
          this.handleToggleStatus(row);
          break;
        case 'delete':
          this.handleDelete(row);
          break;
        default:
          console.warn('未知的操作:', action);
      }
    }
  }
}
</script>

<style scoped>
.user-list-container {
  padding: 20px;
}

.user-list-card {
  min-height: 80vh;
}

.user-list-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-list-operation {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
}

.search-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.danger-item {
  color: #f56c6c;
}
.danger-item:hover {
  color: white;
  background-color: #f56c6c;
}
