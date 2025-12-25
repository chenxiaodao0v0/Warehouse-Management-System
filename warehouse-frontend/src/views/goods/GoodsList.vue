<template>
  <div class="goods-container">
    <el-card class="goods-card">
      <div slot="header" class="clearfix">
        <span>商品列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="fetchGoodsList">刷新</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-box">
        <el-input 
          v-model="searchForm.goodsName" 
          placeholder="请输入商品名称" 
          style="width: 200px; margin-right: 10px;"
        ></el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增商品</el-button>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="goodsList" 
        v-loading="loading"
        style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="商品ID" width="150">
          <template slot-scope="scope">
            {{ formatId(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="categoryId" label="商品分类" width="150">
          <template slot-scope="scope">
            {{ getCategoryName(scope.row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="pic" label="图片" width="100">
          <template slot-scope="scope">
            <img v-if="getImageUrl(scope.row.pic)" :src="getImageUrl(scope.row.pic)" style="width: 40px; height: 40px;" @error="imageError($event, scope.row.name)" @load="imageLoad">
            <span v-else>暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        style="margin-top: 20px; text-align: right;"
      >
      </el-pagination>
    </el-card>
    
    <!-- 新增商品对话框 -->
    <el-dialog
      title="新增商品"
      :visible.sync="dialogVisible"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <el-form :model="goodsForm" :rules="goodsRules" ref="goodsForm" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="goodsForm.name" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="goodsForm.price" :precision="2" :step="0.1" placeholder="请输入商品价格"></el-input-number>
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="goodsForm.categoryId" placeholder="请选择商品分类" style="width: 100%">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :headers="uploadHeaders"
            accept="image/*"
          >
            <img v-if="goodsForm.pic" :src="getImageUrl(goodsForm.pic)" class="avatar" alt="商品图片">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="goodsForm.status" placeholder="请选择状态">
            <el-option label="上架" :value="1"></el-option>
            <el-option label="下架" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            v-model="goodsForm.remark">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDialog">取 消</el-button>
        <el-button type="primary" @click="handleAddSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getGoodsList, addGoods } from '@/api/goods'
import { getCategoryList } from '@/api/goodsCategory'
import store from '@/store'

export default {
  name: 'GoodsListPage',
  data() {
    return {
      goodsList: [],
      loading: false,
      searchForm: {
        goodsName: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      goodsForm: {
        name: '',
        price: null,
        categoryId: '',
        pic: '',
        status: 1, // 默认上架
        remark: ''
      },
      categoryList: [], // 分类列表
      goodsRules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
          { type: 'number', min: 0, message: '价格必须为非负数', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择商品分类', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择商品状态', trigger: 'change' }
        ]
      },
      uploadUrl: `${process.env.VUE_APP_BASE_API || 'http://localhost:8080'}/api/goods/upload`,
      uploadHeaders: {
        'Authorization': `Bearer ${store.state.user.token}`
      }
    }
  },
  created() {
    this.fetchGoodsList()
    this.fetchCategoryList()
  },
  methods: {
    async fetchGoodsList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          goodsName: this.searchForm.goodsName
        }
        const res = await getGoodsList(params)
        
        // 根据API响应结构调整数据获取方式
        if (res && res.data) {
          // 标准分页响应格式：{ data: { records: [], total: 0 } }
          this.goodsList = res.data.records || []
          this.pagination.total = res.data.total || 0
        } else if (res && res.records) {
          // 兼容直接返回分页数据格式：{ records: [], total: 0 }
          this.goodsList = res.records || []
          this.pagination.total = res.total || 0
        } else {
          // 其他情况，初始化为空数组
          this.goodsList = []
          this.pagination.total = 0
        }
        
        // 调试信息
        console.log('获取到的商品列表:', this.goodsList)
      } catch (error) {
        console.error('获取商品列表失败：', error)
        this.$message.error('获取商品列表失败')
        // 发生错误时也要重置数据
        this.goodsList = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },
    // 获取商品分类列表
    async fetchCategoryList() {
      try {
        const res = await getCategoryList()
        this.categoryList = res || []
      } catch (error) {
        console.error('获取商品分类列表失败：', error)
        this.$message.error('获取商品分类列表失败')
        this.categoryList = []
      }
    },
    // 根据图片路径生成正确的图片URL
    getImageUrl(picPath) {
      console.log('处理图片路径:', picPath) // 调试信息
      
      // 如果是完整的URL（以http或https开头），直接返回
      if (picPath && (picPath.startsWith('http://') || picPath.startsWith('https://'))) {
        console.log('完整URL:', picPath) // 调试信息
        return picPath
      }
      
      // 如果是相对路径或后端返回的图片路径，拼接后端API基础路径
      if (picPath) {
        // 获取基础API地址，如果环境变量未设置，则使用默认地址
        const baseUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
        // 如果picPath已经是完整的后端路径（以/开头），则直接拼接
        if (picPath.startsWith('/')) {
          // 移除重复的斜杠
          const fullPath = `${baseUrl}${picPath}`
          console.log('拼接后路径:', fullPath) // 调试信息
          return fullPath
        } else {
          // 否则添加统一的图片访问前缀
          const fullPath = `${baseUrl}/${picPath}`
          console.log('拼接后路径:', fullPath) // 调试信息
          return fullPath
        }
      }
      
      // 如果没有提供图片路径，返回null表示没有图片
      console.log('没有提供图片路径') // 调试信息
      return null
    },
    // 格式化日期，只显示年月日
    formatDate(dateStr) {
      if (!dateStr) return ''
      
      // 将日期字符串转换为Date对象
      const date = new Date(dateStr)
      
      // 检查日期是否有效
      if (isNaN(date.getTime())) {
        return ''
      }
      
      // 格式化为 YYYY-MM-DD 格式
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')  // 月份从0开始，需要+1
      const day = String(date.getDate()).padStart(2, '0')
      
      return `${year}-${month}-${day}`
    },
    imageError(event, goodsName) {
      // 图片加载失败时，显示默认图片
      console.warn(`商品"${goodsName}"的图片加载失败: ${event.target.src}`)
      console.error(`图片加载失败，URL:`, event.target.src)
      event.target.src = require('@/assets/logo.png') // 使用默认图片
    },
    imageLoad() {
      // 图片加载成功时的处理
    },
    handleSearch() {
      this.pagination.pageNum = 1
      this.fetchGoodsList()
    },
    handleAdd() {
      // 重置表单
      this.resetForm()
      this.dialogVisible = true
    },
    resetForm() {
      this.goodsForm = {
        name: '',
        price: null,
        categoryId: '',
        pic: '',
        status: 1, // 默认上架
        remark: ''
      }
      if (this.$refs.goodsForm) {
        this.$refs.goodsForm.resetFields()
      }
    },
    handleCloseDialog() {
      this.dialogVisible = false
      this.resetForm()
    },
    handleUploadSuccess(response, file) {
      // 上传成功后更新图片路径
      if (response && response.data) {
        this.goodsForm.pic = response.data
      } else {
        this.$message.error('图片上传失败')
      }
    },
    async handleAddSubmit() {
      this.$refs.goodsForm.validate(async (valid) => {
        if (valid) {
          try {
            await addGoods(this.goodsForm)
            this.$message.success('添加商品成功')
            this.dialogVisible = false
            // 重置表单
            this.resetForm()
            // 刷新商品列表
            this.fetchGoodsList()
          } catch (error) {
            console.error('添加商品失败：', error)
            this.$message.error('添加商品失败')
          }
        } else {
          this.$message.error('请填写正确的商品信息')
          return false
        }
      })
    },
    handleEdit(row) {
      this.$message.info(`编辑商品：${row.name} (ID: ${this.formatId(row.id)})`)
    },
    handleDelete(id) {
      this.$confirm(`确定要删除这个商品吗？商品ID: ${this.formatId(id)}`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 导入删除商品的API
          const { deleteGoods } = await import('@/api/goods')
          const response = await deleteGoods(id)
          
          console.log('删除API响应:', response) // 调试信息
          
          // 检查响应是否成功
          // 优先检查code字段，如果不存在则检查是否为成功状态
          if(response) {
            if(response.code !== undefined) {
              // 后端返回了code字段
              if(response.code === 200) {
                this.$message.success('删除成功')
                this.fetchGoodsList() // 重新获取商品列表
              } else {
                this.$message.error(response.msg || '删除失败')
              }
            } else {
              // 没有code字段，可能是直接返回数据
              this.$message.success('删除成功')
              this.fetchGoodsList() // 重新获取商品列表
            }
          } else {
            this.$message.error('删除失败')
          }
        } catch (error) {
          console.error('删除商品失败：', error)
          console.error('错误详情：', error.response) // 调试信息
          
          // 即使出现错误，也可能是删除成功但响应处理有问题，刷新列表
          if (error.response && error.response.status >= 200 && error.response.status < 300) {
            // HTTP状态码表示成功，但可能响应格式有误
            this.$message.success('删除成功')
            this.fetchGoodsList()
          } else {
            this.$message.error('删除失败')
          }
        }
      }).catch(() => {
        // 用户取消删除
        this.$message.info('已取消删除')
      })
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchGoodsList()
    },
    handleCurrentChange(val) {
      this.pagination.pageNum = val
      this.fetchGoodsList()
    },
    // 格式化ID显示，截取UUID的后8位
    formatId(id) {
      if (!id) return ''
      // 如果是UUID格式，截取后8位，否则返回原ID
      if (id.length > 8) {
        return '...' + id.slice(-8)
      }
      return id
    },
    
    // 根据分类ID获取分类名称
    getCategoryName(categoryId) {
      if (!categoryId || !this.categoryList || this.categoryList.length === 0) {
        return '未知分类'
      }
      
      const category = this.categoryList.find(cat => cat.id === categoryId)
      return category ? category.name : '未知分类'
    }
  }
}
</script>

<style scoped>
.goods-container {
  width: 100%;
  height: 100%;
}

.goods-card {
  height: 100%;
}

.search-box {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>