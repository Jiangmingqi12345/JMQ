<template>
  <div class="selection-apply-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>申请选题</span>
        </div>
      </template>
      
      <!-- 已有选题提示 -->
      <el-alert
        v-if="hasSelection"
        title="提示"
        type="warning"
        :description="existingSelection ? `您已有${existingSelection.status === 'APPROVED' ? '已批准' : '待审核'}的选题：${existingSelection.projectName}` : '您已有选题，无法再次申请'"
        show-icon
        :closable="false"
      />
      
      <el-form ref="formRef" :model="form" label-width="120px">
        <el-form-item label="学生姓名" prop="studentName" required>
          <el-input v-model="form.studentName" placeholder="请输入学生姓名" :readonly="true" />
        </el-form-item>
        
        <el-form-item label="可选项目" prop="projectId" required>
          <el-select v-model="form.projectId" placeholder="请选择项目" :loading="projectsLoading" :disabled="hasSelection">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.title"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="申请理由" prop="reason" required>
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入申请理由"
            :disabled="hasSelection"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading" :disabled="hasSelection">提交申请</el-button>
          <el-button @click="handleReset" :disabled="hasSelection">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 路由实例
const router = useRouter()

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive({
  studentName: '学生1', // 这里可以从登录信息中获取
  projectId: '',
  reason: ''
})

// 加载状态
const projectsLoading = ref(false)
const submitLoading = ref(false)

// 可选项目列表
const projects = ref([])

// 是否已存在选题的标志
const hasSelection = ref(false)

// 选题信息
const existingSelection = ref(null)

// 获取可选项目列表
const getProjects = async () => {
  try {
    projectsLoading.value = true
    // 调用后端API获取可选项目列表（只获取已批准的项目）
    const response = await service.get('/projects/list', {
      params: {
        page: 1,
        size: 100, // 获取足够多的项目
        name: '' // 不进行名称过滤
      }
    })
    
    // 过滤掉已结束和人满的项目
    const allProjects = response.data.data || []
    projects.value = allProjects.filter(project => {
      // 只保留开放状态的项目
      const isOpen = project.status === 0
      // 只保留未满额的项目（如果没有设置人数限制，视为可以无限选）
      const isNotFull = !project.maxStudents || (project.selectedCount || 0) < project.maxStudents
      return isOpen && isNotFull
    })
    
    console.log('过滤后的项目列表:', projects.value)
  } catch (error) {
    ElMessage.error('获取可选项目列表失败：' + (error.message || '未知错误'))
    console.error('获取可选项目列表失败：', error)
  } finally {
    projectsLoading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    submitLoading.value = true
    console.log('提交申请，projectId:', form.projectId)
    
    // 检查是否已有选题
    if (hasSelection.value) {
      ElMessage.error('您已有选题，无法再次申请')
      return
    }
    
    // 检查用户是否已登录（包括本地存储和Session）
    const user = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    
    if (!user || !token) {
      // 本地存储中没有用户信息，跳转到登录页
      ElMessage.error('请先登录')
      router.push({ name: 'login' })
      return
    }
    
    // 检查Session有效性（注意：实际接口路径是/login/check-login，因为LoginController有/login前缀）
    try {
      await service.get('/login/check-login')
      console.log('Session有效')
    } catch (error) {
      // Session无效，清除本地存储，跳转到登录页
      console.error('Session无效:', error)
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      ElMessage.error('登录已过期，请重新登录')
      router.push({ name: 'login' })
      return
    }
    
    try {
      // 调用后端API提交申请，包含申请理由
      const response = await service.post(`/projects/${form.projectId}/apply`, {
        reason: form.reason
      })
      console.log('申请提交成功，响应:', response)
      
      ElMessage.success(response.message || '申请提交成功，等待审核')
      handleReset()
    } catch (apiError) {
      // API请求失败，可能是Session过期
      if (apiError.message.includes('未授权') || apiError.message.includes('Missing session')) {
        // 清除本地存储，跳转到登录页
        localStorage.removeItem('user')
        localStorage.removeItem('token')
        ElMessage.error('登录已过期，请重新登录')
        router.push({ name: 'login' })
        return
      }
      
      // 其他API错误
      throw apiError
    }
  } catch (error) {
    console.error('申请提交失败，完整错误信息:', error)
    let errorMsg = '申请提交失败：'
    
    if (error.response) {
      // 服务器返回了错误响应
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
      console.error('响应头:', error.response.headers)
      
      // 尝试获取后端返回的具体错误信息
      if (error.response.data.message) {
        errorMsg += error.response.data.message
      } else if (error.response.data) {
        // 如果返回的是字符串直接使用
        errorMsg += error.response.data
      } else {
        errorMsg += `HTTP ${error.response.status}`
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      console.error('请求发出但没有收到响应:', error.request)
      errorMsg += '服务器无响应'
    } else {
      // 请求配置有问题
      console.error('请求配置错误:', error.message)
      errorMsg += error.message || '未知错误'
    }
    
    ElMessage.error(errorMsg)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  form.projectId = ''
  form.reason = ''
}

// 检查学生是否已有选题
const checkStudentSelection = async () => {
  try {
    // 检查用户是否已登录
    const user = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    
    if (!user || !token) {
      return
    }
    
    const parsedUser = JSON.parse(user)
    
    // 调用后端API获取学生的选题记录
    const response = await service.get(`/selections/student`)
    const selections = response.data.data || []
    
    if (selections.length > 0) {
      // 检查是否有已批准的选题
      const approvedSelection = selections.find(selection => selection.status === 'APPROVED')
      if (approvedSelection) {
        hasSelection.value = true
        existingSelection.value = approvedSelection
        return
      }
      
      // 检查是否有待审核的选题
      const pendingSelection = selections.find(selection => selection.status === 'PENDING')
      if (pendingSelection) {
        hasSelection.value = true
        existingSelection.value = pendingSelection
        return
      }
    }
    
    // 没有已批准或待审核的选题
    hasSelection.value = false
    existingSelection.value = null
  } catch (error) {
    console.error('检查学生选题失败:', error)
    // 出错时默认允许申请，由后端进行最终检查
    hasSelection.value = false
    existingSelection.value = null
  }
}

// 组件挂载时获取可选项目列表、当前用户信息和检查选题状态
onMounted(async () => {
  await getProjects()
  
  // 获取当前登录用户信息，自动填充学生姓名
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      if (user.name) {
        form.studentName = user.name
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  // 检查学生是否已有选题
  await checkStudentSelection()
})
</script>

<style scoped>
.selection-apply-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>