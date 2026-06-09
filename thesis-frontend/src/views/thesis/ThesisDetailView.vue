<template>
  <div class="thesis-detail-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>论文详情</span>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else>
        <el-descriptions title="论文基本信息" :column="2" border>
          <el-descriptions-item label="论文ID">{{ thesisDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="论文标题">{{ thesisDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ thesisDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ thesisDetail.projectName }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ formatDate(thesisDetail.uploadTime) }}</el-descriptions-item>
          <el-descriptions-item label="论文状态">
            <el-tag :type="getStatusColor(thesisDetail.status)">
              {{ getStatusText(thesisDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="文件名">{{ thesisDetail.fileName }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ thesisDetail.fileSize }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{ thesisDetail.fileType }}</el-descriptions-item>
          <el-descriptions-item label="评审分数">{{ thesisDetail.score || 0 }}</el-descriptions-item>
          <el-descriptions-item label="评审等级">
            <el-tag :type="getLevelColor(thesisDetail.level)">
              {{ getLevelText(thesisDetail.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评审意见" :span="2">
            <div v-if="thesisDetail.reviewComment" class="review-comment-box">
              {{ thesisDetail.reviewComment }}
            </div>
            <span v-else>暂无评审意见</span>
          </el-descriptions-item>
          <el-descriptions-item label="评审理由" :span="2">
            <div v-if="thesisDetail.reviewReason" class="review-reason-box">
              {{ thesisDetail.reviewReason }}
            </div>
            <span v-else>暂无评审理由</span>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions" style="margin-top: 30px">
          <el-button type="primary" @click="handleDownload">下载论文</el-button>
          <el-button v-if="thesisDetail.status === 'REJECTED' || thesisDetail.status === 'rejected'" type="success" @click="handleReupload">重新上传</el-button>
          <el-button @click="handleBack">返回列表</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 路由实例
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 论文详情
const thesisDetail = reactive({
  id: '',
  title: '',
  studentName: '',
  projectName: '',
  uploadTime: '',
  status: '',
  fileName: '',
  fileSize: '',
  fileType: '',
  reviewReason: '',
  score: 0,
  level: '',
  reviewComment: ''
})

// 获取论文ID
const thesisId = route.query.id

// 获取论文详情
const getThesisDetail = async () => {
  try {
    loading.value = true
    const response = await service.get(`/thesis/detail/${thesisId}`)
    
    // 添加详细的调试日志
    console.log('响应数据:', JSON.stringify(response, null, 2))
    console.log('响应code:', response.code)
    console.log('响应message:', response.message)
    console.log('响应data:', JSON.stringify(response.data, null, 2))
    
    if (response.code === 200) {
      const data = response.data
      console.log('完整响应数据:', JSON.stringify(response, null, 2))
      console.log('data对象:', JSON.stringify(data, null, 2))
      console.log('data.id:', data.id)
      console.log('data.title:', data.title)
      console.log('data.studentName:', data.studentName)
      console.log('data.projectName:', data.projectName)
      console.log('data.reviewReason:', data.reviewReason)
      console.log('data中是否包含reviewReason:', 'reviewReason' in data)
      
      // 填充表单数据
      thesisDetail.id = data.id
      thesisDetail.title = data.title
      thesisDetail.studentName = data.studentName
      thesisDetail.projectName = data.projectName
      thesisDetail.uploadTime = data.uploadTime
      thesisDetail.status = data.status
      thesisDetail.fileName = data.fileName
      thesisDetail.fileSize = data.fileSize
      thesisDetail.fileType = data.fileType
      thesisDetail.reviewReason = data.reviewReason || ''
      thesisDetail.score = data.score || 0
      thesisDetail.level = data.level || ''
      thesisDetail.reviewComment = data.reviewComment || ''
      
      console.log('更新后的thesisDetail:', JSON.stringify(thesisDetail, null, 2))
      console.log('thesisDetail.reviewReason:', thesisDetail.reviewReason)
    } else {
      ElMessage.error('获取论文详情失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('获取论文详情失败：' + (error.message || '未知错误'))
    console.error('获取论文详情失败：', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'UPLOADED':
      return 'info'
    case 'PASSED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    default:
      return 'warning'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'UPLOADED':
      return '已上传'
    case 'PASSED':
      return '已通过'
    case 'REJECTED':
      return '已驳回'
    default:
      return '未知状态'
  }
}

// 获取评审等级颜色
const getLevelColor = (level) => {
  switch (level) {
    case 'EXCELLENT':
      return 'success'
    case 'GOOD':
      return 'warning'
    case 'AVERAGE':
      return 'info'
    case 'PASS':
      return 'primary'
    case 'FAIL':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取评审等级文本
const getLevelText = (level) => {
  switch (level) {
    case 'EXCELLENT':
      return '优秀'
    case 'GOOD':
      return '良好'
    case 'AVERAGE':
      return '中等'
    case 'PASS':
      return '及格'
    case 'FAIL':
      return '不及格'
    default:
      return '暂无等级'
  }
}

// 处理下载
const handleDownload = () => {
  // 直接通过window.open打开下载链接
  window.open(`http://localhost:8080/thesis/download/${thesisDetail.id}`, '_blank')
}

// 处理返回
const handleBack = () => {
  router.push('/thesis/list')
}

// 处理重新上传
const handleReupload = async () => {
  try {
    await service.post(`/thesis/reupload/${thesisId}`)
    ElMessage.success('重新上传成功')
    // 跳转到论文上传页面，并传递论文ID
    router.push({ path: '/thesis/upload', query: { thesisId } })
  } catch (error) {
    ElMessage.error('重新上传失败：' + (error.message || '未知错误'))
    console.error('重新上传失败：', error)
  }
}

// 组件挂载时获取论文详情
onMounted(() => {
  if (thesisId) {
    getThesisDetail()
  } else {
    ElMessage.error('论文ID不能为空')
    router.push('/thesis/list')
  }
})
</script>

<style scoped>
.thesis-detail-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  padding: 20px 0;
}

.detail-actions {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.review-reason-box {
  background-color: #f9f0f0;
  padding: 10px;
  border-radius: 4px;
  border-left: 4px solid #f56c6c;
  word-break: break-word;
  white-space: pre-wrap;
}

.review-comment-box {
  background-color: #f0f5f9;
  padding: 10px;
  border-radius: 4px;
  border-left: 4px solid #409eff;
  word-break: break-word;
  white-space: pre-wrap;
}
</style>