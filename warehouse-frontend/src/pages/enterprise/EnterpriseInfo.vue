<template>
  <el-card>
    <h3>企业基本信息</h3>
    <el-form :model="enterpriseForm" label-width="120px" :rules="rules" ref="formRef">
      <el-form-item label="企业名称" prop="name">
        <el-input v-model="enterpriseForm.name" placeholder="请输入企业名称"></el-input>
      </el-form-item>
      <el-form-item label="企业地址" prop="address">
        <el-input v-model="enterpriseForm.address" placeholder="请输入企业地址"></el-input>
      </el-form-item>
      <el-form-item label="联系人" prop="contact">
        <el-input v-model="enterpriseForm.contact" placeholder="请输入联系人"></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="enterpriseForm.phone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="enterpriseForm.remark" type="textarea" placeholder="请输入备注"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleUpdate">保存修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { getEnterpriseInfo, updateEnterprise } from '@/api/enterprise'

export default {
  name: 'EnterpriseInfo',
  data() {
    return {
      enterpriseForm: {},
      rules: {
        name: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
        address: [{ required: true, message: '请输入企业地址', trigger: 'blur' }],
        contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    // 页面加载时查询企业信息
    this.getInfo()
  },
  methods: {
    async getInfo() {
      const res = await getEnterpriseInfo()
      this.enterpriseForm = res.data
    },
    async handleUpdate() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          const res = await updateEnterprise(this.enterpriseForm)
          this.$message.success(res.msg)
          // 重新查询刷新数据
          this.getInfo()
        }
      })
    }
  }
}
</script>
