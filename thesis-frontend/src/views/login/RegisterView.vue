<template>
  <div class="login-container">
    <el-card shadow="hover" class="login-card">
      <template #header>
        <div class="card-header">
          <span>用户注册</span>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        status-icon
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            auto-complete="off"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            auto-complete="off"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            auto-complete="off"
            show-password
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入真实姓名"
            auto-complete="off"
            clearable
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            auto-complete="off"
            clearable
          />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input
            v-model="form.major"
            placeholder="请输入专业"
            auto-complete="off"
            clearable
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" clearable>
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
        <el-form-item class="register-link">
          <div class="footer">
            <el-link type="primary" @click="goLogin">已有账号？去登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 路由实例
const router = useRouter()

// 表单引用
const formRef = ref()

// 加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  major: '',
  role: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 处理注册
const handleRegister = async () => {
  // 表单验证
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 调用注册接口
        const response = await service.post('/users', form)
        if (response.code === 200) {
          ElMessage.success('注册成功')
          // 注册成功后跳转到登录页面
          setTimeout(() => {
            router.push({ name: 'login' })
          }, 1500)
        } else {
          ElMessage.error('注册失败：' + response.message)
        }
      } catch (error) {
        ElMessage.error('注册失败：' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转到登录页面
const goLogin = () => {
  router.push({ name: 'login' })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  box-sizing: border-box;
}

.login-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.el-form {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (min-width: 1200px) {
  .login-card {
    max-width: 1000px;
  }
  
  .el-form {
    max-width: 700px;
  }
}

@media (max-width: 768px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .el-form {
    max-width: 100%;
  }
}

.card-header {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  color: #333;
}

.register-link {
  text-align: center;
  margin-top: 10px;
}

.footer {
  text-align: center;
  margin-top: 10px;
}

.el-form {
  width: 100%;
  max-width: 450px;
  margin: 0 auto;
}
</style>