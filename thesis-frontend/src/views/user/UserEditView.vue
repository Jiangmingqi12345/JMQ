<template>
  <div class="user-edit-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>编辑用户</span>
          <el-button type="text" @click="handleCancel">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        style="max-width: 600px; margin: 0 auto"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
            <el-option label="学生" value="STUDENT"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" clearable></el-input>
        </el-form-item>
        
        <el-form-item label="专业" prop="major">
          <el-input v-model="formData.major" placeholder="请输入专业"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="不修改密码请留空" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="不修改密码请留空" show-password></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import service from '../../utils/axios'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const userId = ref(route.params.id)

// 表单数据
const formData = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  role: '',
  email: '',
  major: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (formData.password && value !== formData.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户详情
const getUserDetail = async () => {
  try {
    loading.value = true
    const response = await service.get(`/users/${userId.value}`)
    
    const userData = response.data
    formData.username = userData.username
    formData.name = userData.name
    formData.role = userData.role
    formData.email = userData.email
    formData.major = userData.major
  } catch (error) {
    ElMessage.error('获取用户详情失败：' + error.message)
    console.error('获取用户详情失败：', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取用户信息
onMounted(() => {
  getUserDetail()
})

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 准备提交数据，移除confirmPassword字段
    const submitData = { ...formData }
    delete submitData.confirmPassword
    
    // 调用API更新用户
    await service.put(`/users/${userId.value}`, submitData)
    
    ElMessage.success('编辑成功')
    router.push('/user/list')
  } catch (error) {
    if (error.message) {
      ElMessage.error('编辑失败：' + error.message)
    } else {
      ElMessage.error('编辑失败，请重试')
    }
    console.error('编辑用户失败：', error)
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  router.go(-1)
}
</script>

<style scoped>
.user-edit-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
