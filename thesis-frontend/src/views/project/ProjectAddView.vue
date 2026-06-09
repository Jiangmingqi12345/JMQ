<template>
  <div class="project-add-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>添加项目</span>
        </div>
      </template>
      
      <el-form ref="formRef" :model="form" label-width="120px">
        <el-form-item label="项目名称" prop="title" required>
          <el-input v-model="form.title" placeholder="请输入项目名称" />
        </el-form-item>
        
        <el-form-item label="项目描述" prop="description" required>
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        
        <el-form-item label="指导老师" prop="teacherId" required>
          <el-select v-model="form.teacherId" placeholder="请选择指导老师">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.name"
              :value="teacher.id.toString()"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="人数限制" prop="maxStudents" required>
          <el-input
            v-model="form.maxStudents"
            placeholder="请输入项目的最大可选题人数"
            type="number"
            min="1"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status" required>
          <el-radio-group v-model="form.status">
            <el-radio :label="0">开放</el-radio>
            <el-radio :label="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
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

// 教师列表
const teachers = ref([])

// 表单数据
const form = reactive({
  title: '',
  description: '',
  teacherId: '',
  maxStudents: 1,
  status: 0
})

// 获取教师列表
const getTeachers = async () => {
  try {
    const response = await service.get('/users/list', {
      params: {
        role: 'TEACHER',
        page: 1,
        size: 100 // 一次性获取所有教师
      }
    })
    teachers.value = response.data.data || []
  } catch (error) {
    ElMessage.error('获取教师列表失败：' + (error.message || '未知错误'))
    console.error('获取教师列表失败：', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    if (!form.title || !form.description || !form.teacherId) {
      ElMessage.warning('请填写完整的项目信息')
      return
    }
    
    // 调用后端API添加项目
    const response = await service.post('/projects', form)
    
    // 显示成功消息
    ElMessage.success('添加项目成功')
    
    // 跳转到项目列表页面
    router.push('/project/list')
  } catch (error) {
    // 显示错误消息
    ElMessage.error('添加项目失败：' + (error.message || '未知错误'))
    console.error('添加项目失败：', error)
  }
}

// 重置表单
const handleReset = () => {
  form.title = ''
  form.description = ''
  form.teacherId = ''
  form.maxStudents = 1
  form.status = 0
}

// 取消操作
const handleCancel = () => {
  router.push('/project/list')
}

// 组件挂载时获取教师列表
onMounted(() => {
  getTeachers()
})
</script>

<style scoped>
.project-add-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>