<template>
  <div class="goods-category-container">
    <el-card class="category-card">
      <div slot="header" class="clearfix">
        <span class="card-title">商品分类管理</span>
        <el-button 
          style="float: right;" 
          type="primary" 
          @click="handleAddCategory"
        >
          <i class="el-icon-plus"></i> 新增分类
        </el-button>
      </div>

      <!-- 分类表格 -->
      <el-table
        :data="categoryList"
        style="width: 100%"
        row-key="id"
        v-loading="loading"
      >
        <el-table-column prop="name" label="分类名称" width="250"></el-table-column>
        <el-table-column prop="remark" label="描述" width="400"></el-table-column>
        <el-table-column label="创建时间" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      >
      </el-pagination>
    </el-card>

    <!-- 分类编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        :model="categoryForm"
        :rules="categoryRules"
        ref="categoryForm"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="分类描述" prop="remark">
          <el-input
            type="textarea"
            v-model="categoryForm.remark"
            placeholder="请输入分类描述"
            :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCategoryPage, addCategory, updateCategory, deleteCategory } from '@/api/goodsCategory'

export default {
  name: 'GoodsCategory',
  data() {
    return {
      loading: false,
      categoryList: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      categoryForm: {
        id: null,
        name: '',
        remark: ''
      },
      categoryRules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.dialogType === 'add' ? '新增商品分类' : '编辑商品分类'
    }
  },
  created() {
    this.fetchCategoryList()
  },
  methods: {
    // 获取分类列表
    async fetchCategoryList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const response = await getCategoryPage(params)
        this.categoryList = response.records || []
        this.pagination.total = response.total || 0
      } catch (error) {
        console.error('获取商品分类列表失败:', error)
        this.$message.error('获取商品分类列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 格式化日期，只显示年月日
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 处理分页变化
    handleSizeChange(pageSize) {
      this.pagination.pageSize = pageSize
      this.fetchCategoryList()
    },
    handleCurrentChange(currentPage) {
      this.pagination.currentPage = currentPage
      this.fetchCategoryList()
    },
    
    // 新增分类
    handleAddCategory() {
      this.dialogType = 'add'
      this.resetForm()
      this.dialogVisible = true
    },
    
    // 编辑分类
    handleEdit(row) {
      this.dialogType = 'edit'
      this.categoryForm = { ...row }
      this.dialogVisible = true
    },
    
    // 删除分类
    async handleDelete(row) {
      try {
        await this.$confirm(`确定要删除分类 "${row.name}" 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteCategory(row.id)
        this.$message.success('删除成功')
        this.fetchCategoryList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除分类失败:', error)
          this.$message.error('删除分类失败')
        }
      }
    },
    
    // 保存分类
    async handleSave() {
      this.$refs.categoryForm.validate(async (valid) => {
        if (!valid) {
          return
        }
        
        try {
          if (this.dialogType === 'add') {
            await addCategory(this.categoryForm)
            this.$message.success('新增分类成功')
          } else {
            await updateCategory(this.categoryForm)
            this.$message.success('更新分类成功')
          }
          
          this.dialogVisible = false
          this.fetchCategoryList()
        } catch (error) {
          console.error('保存分类失败:', error)
          this.$message.error(`保存分类失败: ${error.message || '未知错误'}`)
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.categoryForm = {
        id: null,
        name: '',
        remark: ''
      }
      this.$nextTick(() => {
        if (this.$refs.categoryForm) {
          this.$refs.categoryForm.clearValidate()
        }
      })
    },
    
    // 关闭对话框
    handleDialogClose() {
      this.$refs.categoryForm.clearValidate()
    }
  }
}
</script>

<style scoped>
.goods-category-container {
  padding: 20px;
}

.category-card {
  min-height: 600px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
}

.delete-dropdown-item {
  color: #f56c6c;
}
</style>