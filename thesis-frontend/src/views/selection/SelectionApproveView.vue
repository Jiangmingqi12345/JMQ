<template>
  <div class="selection-approve-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>审核选题</span>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else>
        <el-descriptions title="选题申请详情" :column="2" border>
          <el-descriptions-item label="申请ID">{{ selection.id }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ selection.studentName }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ selection.projectName }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDate(selection.selectTime) }}</el-descriptions-item>
          <el-descriptions-item label="申请理由" :span="2">{{ selection.reason || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="approve-actions" style="margin-top: 30px">
          <el-form ref="formRef" :model="form" label-width="120px">
            <el-form-item label="审核结果" prop="status" required>
              <el-radio-group v-model="form.status">
                <el-radio label="APPROVED">通过</el-radio>
                <el-radio label="REJECTED">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="审核意见" prop="comment">
              <el-input
                v-model="form.comment"
                type="textarea"
                :rows="3"
                placeholder="请输入审核意见"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="success" @click="handleApprove">通过</el-button>
              <el-button type="danger" @click="handleReject">拒绝</el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
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

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive({
  status: 'APPROVED',
  comment: ''
})

// 选题详情
const selection = reactive({
  id: '',
  studentName: '',
  projectName: '',
  selectTime: '',
  reason: ''
})

// 获取选题ID
const selectionId = route.query.id

// 获取选题详情
const getSelectionDetail = async () => {
  try {
    // 检查选题ID是否有效
    if (!selectionId) {
      ElMessage.error('未找到选题ID')
      router.push('/selection/list')
      return
    }
    
    loading.value = true
    const response = await service.get(`/selections/${selectionId}`)
    
    // 统一处理响应数据，兼容不同的响应格式
    let selectionData = response.data
    if (response.data && response.data.data) {
      selectionData = response.data.data
    }
    
    Object.assign(selection, selectionData)
  } catch (error) {
    // 处理错误，显示友好的错误信息
    let errorMsg = '获取选题详情失败'
    if (error.response && error.response.status === 400) {
      errorMsg = '无效的选题请求'
    } else if (error.message) {
      errorMsg = `获取选题详情失败：${error.message}`
    }
    
    ElMessage.error(errorMsg)
    console.error('获取选题详情失败：', error)
    router.push('/selection/list')
  } finally {
    loading.value = false
  }
}

// 格式化日期为YYYY-MM-DD
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 组件挂载时获取选题详情
onMounted(() => {
  getSelectionDetail()
})

// 处理通过
const handleApprove = () => {
  form.status = 'APPROVED'
  submitApproval()
}

// 处理拒绝
const handleReject = () => {
  form.status = 'REJECTED'
  submitApproval()
}

// 提交审核
const submitApproval = async () => {
  try {
    loading.value = true
    let response
    if (form.status === 'APPROVED') {
      // 调用审核通过API
      response = await service.put(`/selections/${selectionId}/approve`)
    } else {
      // 调用审核拒绝API
      response = await service.put(`/selections/${selectionId}/reject`, null, {
        params: {
          reason: form.comment
        }
      })
    }
    ElMessage.success(response.message || '审核成功')
    router.push('/selection/list')
  } catch (error) {
    ElMessage.error('审核失败：' + (error.message || '未知错误'))
    console.error('审核失败：', error)
  } finally {
    loading.value = false
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/selection/list')
}
</script>

<style scoped>
.selection-approve-container {
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

.approve-actions {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}
</style>