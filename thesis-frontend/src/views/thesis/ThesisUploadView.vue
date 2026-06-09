<template>
  <div class="thesis-upload-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>上传论文</span>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <el-form v-else ref="formRef" :model="form" label-width="120px">
        <!-- 后端会从Session中获取当前用户，不需要手动输入学生姓名 -->
        
        <el-form-item label="论文标题" prop="title" required>
          <el-input v-model="form.title" placeholder="请输入论文标题" />
        </el-form-item>
        
        <el-form-item label="所属项目" prop="projectId" required>
          <!-- 如果只有一个已通过的项目，显示项目名，否则显示选择框 -->
          <div v-if="!showProjectSelect && approvedProject" class="project-info-display">
            <el-input
              v-model="approvedProject.title"
              readonly
              placeholder="已通过的项目"
            />
          </div>
          <el-select
            v-else
            v-model="form.projectId"
            placeholder="请选择项目"
            :loading="projectsLoading"
          >
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.title"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="论文文件" prop="file" required>
          <el-upload
            ref="uploadRef"
            drag
            :on-change="handleFileChange"
            :file-list="fileList"
            :auto-upload="false"
            accept=".pdf,.doc,.docx"
            :before-upload="(file) => false"
            multiple="false"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              拖放文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持上传PDF、Word文档，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
          <div v-if="form.id" class="existing-file-info" style="margin-top: 10px; font-size: 12px; color: #606266;">
            现有文件：{{ existingFileName }}
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">{{ form.id ? '更新上传' : '提交上传' }}</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 导入图标
import { UploadFilled } from '@element-plus/icons-vue'

// 路由实例
const route = useRoute()

// 表单引用
const formRef = ref()
const uploadRef = ref()

// 加载状态
const loading = ref(false)

// 表单数据
const form = reactive({
  id: '',
  title: '',
  projectId: '',
  file: null
})

// 文件列表
const fileList = ref([])

// 现有文件名
const existingFileName = ref('')

// 项目列表
const projects = ref([])
const projectsLoading = ref(false)

// 已通过的项目信息
const approvedProject = ref(null)

// 是否显示项目选择框
const showProjectSelect = ref(true)

// 获取可选项目列表 - 只显示当前学生已通过的项目
const getProjects = async () => {
  try {
    projectsLoading.value = true
    
    // 1. 先获取当前学生的选题列表
    const selectionResponse = await service.get('/selections/student', {
      params: {
        page: 1,
        size: 100
      }
    })
    
    const selectionList = selectionResponse.data.data || []
    
    // 2. 筛选出状态为已通过的选题记录
    const approvedSelections = selectionList.filter(selection => selection.status === 'APPROVED' || selection.status === 'approved')
    
    // 3. 提取出项目ID列表
    const projectIds = approvedSelections.map(selection => selection.projectId)
    
    // 4. 如果没有已通过的项目，直接返回空列表
    if (projectIds.length === 0) {
      projects.value = []
      showProjectSelect.value = true
      return
    }
    
    // 5. 获取所有项目列表
    const projectsResponse = await service.get('/projects/list', {
      params: {
        page: 1,
        size: 100,
        name: ''
      }
    })
    
    const allProjects = projectsResponse.data.data || []
    
    // 6. 筛选出当前学生已通过的项目
    projects.value = allProjects.filter(project => projectIds.includes(project.id))
    
    // 7. 如果只有一个已通过的项目，自动设置为默认项目，不显示选择框
    if (projects.value.length === 1) {
      approvedProject.value = projects.value[0]
      form.projectId = approvedProject.value.id
      showProjectSelect.value = false
    } else {
      // 如果有多个已通过的项目，显示选择框
      showProjectSelect.value = true
    }
  } catch (error) {
    ElMessage.error('获取项目列表失败：' + (error.message || '未知错误'))
    console.error('获取项目列表失败：', error)
  } finally {
    projectsLoading.value = false
  }
}

// 文件选择回调
const handleFileChange = (file, files) => {
  // 保存选中的文件
  form.file = file.raw
  // 更新文件列表（注意：files是element-plus上传组件返回的文件列表）
  fileList.value = files
  console.log('文件选择成功：', file.raw.name)
  console.log('文件列表更新：', fileList.value)
  console.log('当前文件：', form.file)
}

// 提交表单
const handleSubmit = async () => {
  console.log('提交上传，fileList长度：', fileList.value.length)
  console.log('表单数据：', form)
  
  if (!form.file) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }
  
  if (!form.title) {
    ElMessage.warning('请输入论文标题')
    return
  }
  
  if (!form.projectId) {
    ElMessage.warning('请选择所属项目')
    return
  }
  
  // 准备FormData，包含文件、论文标题和项目ID
  const formData = new FormData()
  formData.append('file', form.file)
  formData.append('title', form.title)
  formData.append('projectId', form.projectId)
  
  try {
    console.log('开始上传文件...')
    console.log('FormData字段：')
    for (let [key, value] of formData.entries()) {
      console.log(`${key}: ${value instanceof File ? value.name : value}`)
    }
    
    let response
    // 根据是否有论文ID来决定是创建新记录还是更新现有记录
    if (form.id) {
      // 更新现有论文
      console.log('更新现有论文，ID：', form.id)
      response = await service.post(`/thesis/update/${form.id}`, formData, {
        withCredentials: true,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      console.log('更新成功，响应：', response)
      console.log('响应数据：', JSON.stringify(response, null, 2))
      ElMessage.success(response.message || '论文更新成功')
    } else {
      // 创建新论文
      response = await service.post('/thesis/upload', formData, {
        withCredentials: true,
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      console.log('上传成功，响应：', response)
      console.log('响应数据：', JSON.stringify(response, null, 2))
      ElMessage.success(response.message || '论文上传成功')
    }
    
    handleReset()
  } catch (error) {
    console.error('上传失败，完整错误信息：', error)
    console.error('错误状态：', error.response?.status)
    console.error('错误数据：', error.response?.data)
    console.error('错误头：', error.response?.headers)
    ElMessage.error('论文上传失败：' + (error.message || '未知错误'))
  }
}

// 重置表单
const handleReset = () => {
  fileList.value = []
  form.id = ''
  form.title = ''
  form.projectId = ''
  form.file = null
  existingFileName.value = ''
  // 重置新添加的变量
  approvedProject.value = null
  showProjectSelect.value = true
  // 重新获取项目列表，以便正确设置默认项目
  getProjects()
}

// 获取学生已上传的论文记录
const getStudentThesis = async () => {
  try {
    loading.value = true
    
    // 从路由参数中获取 thesisId
    const thesisIdFromRoute = route.query.thesisId
    console.log('路由参数中的 thesisId：', thesisIdFromRoute)
    
    let thesis = null
    
    if (thesisIdFromRoute) {
      // 如果路由参数中有 thesisId，直接获取该论文记录
      console.log('根据 thesisId 获取论文记录：', thesisIdFromRoute)
      const response = await service.get(`/thesis/detail/${thesisIdFromRoute}`)
      if (response.code === 200) {
        thesis = response.data
        console.log('获取到论文记录：', thesis)
      }
    } else {
      // 否则，获取所有论文记录，并默认显示第一篇
      const response = await service.get('/thesis/student')
      if (response.code === 200 && response.data && response.data.length > 0) {
        thesis = response.data[0]
        console.log('获取到学生现有论文记录：', thesis)
      }
    }
    
    // 如果获取到了论文记录，填充表单
    if (thesis) {
      form.id = thesis.id
      form.title = thesis.title
      form.projectId = thesis.projectId
      existingFileName.value = thesis.fileName
      
      // 查找对应的项目信息
      const project = projects.value.find(p => p.id === thesis.projectId)
      if (project) {
        approvedProject.value = project
        showProjectSelect.value = false
      }
    }
  } catch (error) {
    console.error('获取学生论文记录失败：', error)
    // 不显示错误信息，因为学生可能还没有上传过论文
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取项目列表和学生现有论文记录
onMounted(async () => {
  await getProjects()
  await getStudentThesis()
})
</script>

<style scoped>
.thesis-upload-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>