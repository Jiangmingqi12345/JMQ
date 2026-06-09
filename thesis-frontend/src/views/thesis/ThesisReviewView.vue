<template>
  <div class="thesis-review-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评审论文</span>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>
      
      <div v-else>
        <el-descriptions title="论文基本信息" :column="2" border>
          <el-descriptions-item label="论文ID">{{ thesis.id }}</el-descriptions-item>
          <el-descriptions-item label="论文标题">{{ thesis.title }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ thesis.studentName }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ thesis.projectName }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ thesis.uploadTime }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ thesis.fileSize }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{ thesis.fileType }}</el-descriptions-item>
          <el-descriptions-item label="当前状态"><el-tag :type="getStatusColor(thesis.status)">{{ getStatusText(thesis.status) }}</el-tag></el-descriptions-item>
        </el-descriptions>
        
        <div class="review-section" style="margin-top: 30px">
          <h3 style="margin-bottom: 20px">评审内容</h3>
          
          <el-form ref="formRef" :model="form" label-width="120px">
            <el-form-item label="评审分数" prop="score" required>
              <el-input-number
                v-model="form.score"
                :min="0"
                :max="100"
                :step="1"
                :precision="0"
                placeholder="请输入0-100的整数分数"
                class="score-input"
              />
            </el-form-item>
            
            <el-form-item label="评审等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择评审等级">
                <el-option label="优秀" value="EXCELLENT" />
                <el-option label="良好" value="GOOD" />
                <el-option label="中等" value="AVERAGE" />
                <el-option label="及格" value="PASS" />
                <el-option label="不及格" value="FAIL" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="评审意见" prop="comment">
              <el-input
                v-model="form.comment"
                type="textarea"
                :rows="6"
                placeholder="请输入详细评审意见"
              />
            </el-form-item>
            
            <el-form-item label="是否通过" prop="pass">
              <el-switch v-model="form.pass" active-text="通过" inactive-text="不通过" />
            </el-form-item>
            
            <el-form-item label="评审理由" prop="reviewReason" v-if="!form.pass">
              <el-input
                v-model="form.reviewReason"
                type="textarea"
                :rows="4"
                placeholder="请输入驳回理由（必填）"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">提交评审</el-button>
              <el-button @click="handleReset">重置</el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 路由实例
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive({
  score: 85,
  level: 'GOOD',
  comment: '',
  pass: true,
  reviewReason: ''
})

// 论文详情
const thesis = reactive({
  id: '',
  title: '',
  studentName: '',
  projectName: '',
  uploadTime: '',
  fileSize: '',
  fileType: '',
  status: ''
})

// 获取论文ID
const thesisId = ref(route.query.id)
console.log('初始论文ID：', thesisId.value)

// 监听路由变化，当论文ID变化时重新获取论文详情
watch(
  () => route.query.id,
  (newId) => {
    if (newId) {
      console.log('路由参数变化，新论文ID：', newId)
      thesisId.value = newId
      loadThesisDetail()
    }
  }
)

// 调用API获取论文详情
const loadThesisDetail = async () => {
  if (!thesisId.value) {
    ElMessage.error('论文ID不能为空')
    return
  }
  
  try {
    loading.value = true
    const response = await service.get(`/thesis/detail/${thesisId.value}`)
    
    if (response.code === 200) {
      const thesisData = response.data
      thesis.id = thesisData.id
      thesis.title = thesisData.title
      thesis.studentName = thesisData.studentName
      thesis.projectName = thesisData.projectName
      thesis.uploadTime = thesisData.uploadTime
      thesis.fileSize = thesisData.fileSize
      thesis.fileType = thesisData.fileType
      thesis.status = thesisData.status
      
      // 初始化表单数据，显示已有评审信息
      // 只在有有效数据时才更新表单字段，否则保持默认值
      if (thesisData.score !== undefined && thesisData.score !== null) {
        // 确保分数是数字类型
        form.score = Number(thesisData.score)
      } else {
        // 否则使用默认值85
        form.score = 85
      }
      
      // 如果有有效level数据，使用该数据，否则保持默认值
      if (thesisData.level && ['EXCELLENT', 'GOOD', 'AVERAGE', 'PASS', 'FAIL'].includes(thesisData.level)) {
        form.level = thesisData.level
      } else {
        // 否则使用默认值GOOD
        form.level = 'GOOD'
      }
      
      // 如果有reviewComment数据，使用该数据，否则保持默认值
      if (thesisData.reviewComment !== undefined && thesisData.reviewComment !== null) {
        form.comment = thesisData.reviewComment
      } else {
        // 否则使用空字符串
        form.comment = ''
      }
      
      // 如果有reviewReason数据，使用该数据，否则保持默认值
      if (thesisData.reviewReason !== undefined && thesisData.reviewReason !== null) {
        form.reviewReason = thesisData.reviewReason
      } else {
        // 否则使用空字符串
        form.reviewReason = ''
      }
      
      // 根据当前状态初始化通过/不通过开关
      if (thesisData.status === 'PASSED') {
        form.pass = true
      } else if (thesisData.status === 'REJECTED') {
        form.pass = false
      } else {
        // 否则保持默认值true
        form.pass = true
      }
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

// 页面加载时获取论文详情
onMounted(() => {
  loadThesisDetail()
})

// 监听路由变化，当论文ID变化时重新获取论文详情
watch(
  () => route.query.id,
  (newId) => {
    if (newId) {
      thesisId.value = newId
      loadThesisDetail()
    }
  }
)

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

// 提交评审
const handleSubmit = async () => {
  try {
    console.log('开始提交评审')
    console.log('表单数据:', form)
    
    // 表单验证：如果不通过，评审理由必填
    if (!form.pass && !form.reviewReason.trim()) {
      ElMessage.warning('请输入评审理由')
      return
    }
    
    // 构造请求数据
    const requestData = {
      status: form.pass ? 'PASSED' : 'REJECTED',
      reviewReason: form.reviewReason || '',
      score: form.score,
      level: form.level,
      reviewComment: form.comment || ''
    }
    console.log('请求数据:', requestData)
    
    // 构造请求URL
    const url = `/thesis/review/${thesisId.value}`
    console.log('请求URL:', url)
    
    // 使用 axios 发送请求
    console.log('开始发送请求')
    const response = await service.post(url, requestData, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    })
    console.log('请求发送成功')
    console.log('响应数据:', response)
    
    // 显示成功消息
    ElMessage.success('评审提交成功')
    
    // 使用 replace 跳转并添加时间戳，确保页面完全刷新
    router.replace({
      path: '/thesis/list',
      query: {
        t: Date.now() // 添加时间戳，避免缓存
      }
    })
  } catch (error) {
    console.error('评审提交失败：', error)
    console.error('错误详情:', JSON.stringify(error, null, 2))
    ElMessage.error('评审提交失败：' + (error.message || '未知错误'))
  }
}

// 重置表单
const handleReset = () => {
  form.score = 85
  form.level = 'GOOD'
  form.comment = ''
  form.pass = true
  form.reviewReason = ''
}

// 取消操作
const handleCancel = () => {
  router.push('/thesis/list')
}
</script>

<style scoped>
.thesis-review-container {
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

.review-section {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.score-input {
  width: 200px;
}
</style>