<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>毕业设计管理系统</h2>
        <p>请使用您的账号登录</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
            size="large"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            size="large"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
        <el-form-item class="register-link">
          <el-link type="primary" @click="goRegister">没有账号？去注册</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

// 路由实例
const router = useRouter()

// 用户状态管理
const userStore = useUserStore()

// 登录表单引用
const loginFormRef = ref(null)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 登录加载状态
const loading = ref(false)

// 登录表单规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  // 表单验证
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用登录接口
    const response = await axios.post('/login/check', {
      username: loginForm.username,
      password: loginForm.password
    })
    
    // 登录成功，保存用户信息和token
    const userInfo = {
      id: response.data.id,
      username: response.data.username,
      name: response.data.name,
      role: response.data.role,
      email: response.data.email,
      major: response.data.major
    }
    
    // 保存用户信息到localStorage
    localStorage.setItem('user', JSON.stringify(userInfo))
    localStorage.setItem('token', response.data.token || 'mock-token-' + Date.now())
    
    // 保存用户信息到store
    userStore.login(userInfo, response.data.token || 'mock-token-' + Date.now())
    
    // 显示成功消息
    ElMessage.success('登录成功')
    
    // 跳转到首页
    router.push('/home')
  } catch (error) {
    // 显示错误消息
    ElMessage.error(error.message || '登录失败')
    console.error('登录错误:', error)
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}

// 跳转到注册页面
const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.login-form {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (min-width: 1200px) {
  .login-box {
    max-width: 1000px;
  }
  
  .login-form {
    max-width: 700px;
  }
}

@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-form {
    max-width: 100%;
  }
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.login-title p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  padding: 12px;
  font-size: 16px;
}

.register-link {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .login-title h2 {
    font-size: 20px;
  }
}
</style>
